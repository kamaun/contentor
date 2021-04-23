package group13.studybuddy;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SQLDatabaseConnection extends SQLiteOpenHelper {
    private static final String DBNAME = "StudyBuddyDb";

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use for locating paths to the the database
     */
    public SQLDatabaseConnection(Context context) {
        super(context, DBNAME, null, 1);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE AccountType(" +
                "id INTEGER," +
                "name TEXT" +
            ")"
        );

        db.execSQL(
                "INSERT INTO AccountType (id, name) " +
                "VALUES (1, 'Viewer'), (2, 'Creator')"
        );

        db.execSQL(
            "CREATE TABLE Account (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "first_name TEXT," +
                "last_name TEXT," +
                "username TEXT," +
                "password TEXT," +
                "usertype INTEGER," +
                "assignedUser INTEGER," +
                "FOREIGN KEY(usertype) REFERENCES AccountType(id) " +
            ")"
        );

        db.execSQL(
            "CREATE TABLE Content (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userid INTEGER, " +
                "text_block TEXT," +
                "FOREIGN KEY(userid) REFERENCES Account(id) " +
            ")"
        );

        db.execSQL(
            "CREATE TABLE AssignedContent (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "viewerId INTEGER," +
                "contentId INTERGER," +
                "FOREIGN KEY(viewerId) REFERENCES Account(id), " +
                "FOREIGN KEY(contentId) REFERENCES Content(id) " +
            ")"
        );

    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS AccountType ");
        db.execSQL("DROP TABLE IF EXISTS Account ");
        db.execSQL("DROP TABLE IF EXISTS Content ");
        db.execSQL("DROP TABLE IF EXISTS AssignedContent ");
    }

    public String CreateAccount(AccountManager newAccount)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("first_name", newAccount.getFirstName());
        contentValues.put("last_name", newAccount.getLastName());
        contentValues.put("username", newAccount.getUserName());
        contentValues.put("password", newAccount.getPassWord());
        contentValues.put("usertype", newAccount.getReasonForUse());
        contentValues.put("assignedUser", newAccount.getCreatorID());

        long result = db.insert("Account", null, contentValues);

        if (result==-1){
            return "Account not created!";
        }
        else{
            return "Account has been created successfully!";
        }
    }


    public AccountManager LoadAccount(int aUserID) throws Exception
    {
        AccountManager accountManager = null;
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
                    "SELECT  id, username, password, first_name, last_name,  " +
                            "     usertype, assignedUser " +
                        "FROM Account " +
                        "WHERE id = ?", new String[] { Integer.toString(aUserID) } );

        if (cursor.getCount() > 0){
            if (cursor.moveToFirst()) accountManager = new AccountManager(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6)
            );
        }
        else throw new Exception("Account does not exist!");

        return accountManager;
    }


    public void UpdateAccount(AccountManager account) throws Exception
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor currentUsername = db.rawQuery("" +
                "SELECT username FROM Account " +
                "WHERE id=?", new String[]{account.getUserName()});

        if(currentUsername.getCount() > 0){
            if (currentUsername.moveToFirst()){
                if(!account.getUserName().equals(currentUsername.getString(0))){
                    Cursor existingUsernames = db.rawQuery("" +
                            "SELECT * FROM Account " +
                            "WHERE username=?", new String[]{ account.getUserName() });

                    if(existingUsernames.getCount() > 0)
                        throw new Exception("The username is already taken!");

                    existingUsernames.close();
                }
            }
        }
        currentUsername.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put("first_name", account.getFirstName());
        contentValues.put("last_name", account.getLastName());
        contentValues.put("username", account.getUserName());
        contentValues.put("password", account.getPassWord());

        long result = db.update(
                "Account",
                contentValues,
                "id = " + account.getUserID(),
                null );

        if(result == -1) throw new Exception("Content not updated");
    }

    public void DeleteAccount(AccountManager account) throws Exception
    {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(
                "Account",
                "id="+account.getUserID(),
                null
        );

        if(result == -1) throw new Exception("Content not updated");
    }

    public void CheckUsername(String username) throws Exception
    {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
        "SELECT * FROM Account " +
             "WHERE username = ?", new String[] { username } );

        if(cursor.getCount() > 0) throw new Exception("The username is already taken!");
//        return (cursor.getCount() > 0) ? true:false;
    }


    public String CheckCredentials(String username, String password) throws Exception
    {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
        "SELECT * FROM Account " +
             "WHERE username = ? AND password = ?", new String[] { username, password } );

        if (cursor.getCount() > 0){
            return "Login successful!";
        }
        else{
            return "Username/Password is incorrect!";
        }
    }

    public LoginAuthenticator loadAuth(String username){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
        "SELECT * FROM Account WHERE username = ?", new String[] { username }
        );

        LoginAuthenticator userlogin = new LoginAuthenticator();

        if(cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                userlogin.setUserID(cursor.getInt(0));
                userlogin.setUserName(cursor.getString(3));
                userlogin.setPassWord(cursor.getString(4));
                userlogin.setUserRole(cursor.getInt(5));
                userlogin.setCreatorId(cursor.getInt(6));
            }
        }
        cursor.close();

        return userlogin;
    }


    public Long createContent(int aUserID, String aContentText){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", aUserID);
        contentValues.put("text_block", aContentText);

        return db.insert("Content", null, contentValues);
    }


    public Content loadContent(int aUserID, int aContentID){
        SQLiteDatabase db = this.getWritableDatabase();
        Content content = null;

        Cursor cursor = db.rawQuery(
         "SELECT * FROM Content WHERE userid = ? AND id = ? ", new String[] {
                 Integer.toString(aUserID), Integer.toString(aContentID)
         }
        );

        if (cursor.getCount() > 0){
            if(cursor.moveToFirst()){
                content = new Content(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2)
                );
            }
        }
        cursor.close();
        return content;
    }



    public String updateContent(int aUserID, int aContentID, String aContent){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("text_block", aContent);
        long result = db.update(
                "Content",
                contentValues,
                "id = " + aContentID,
                null );

        if(result == -1){
            return "Content not updated";
        }
        else{
            return "Content updated successfully!";
        }
    }


    public DropDownList loadViewersByCreatorId(int aCreatorID){
        final List<String> viewerList  = new ArrayList<String>();
        final ArrayList<Integer> viewerID = new ArrayList<Integer>();
        SQLiteDatabase db = this.getWritableDatabase();

        Content content = null;

        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
                    "SELECT id, first_name, last_name FROM Account " +
                        "WHERE assignedUser = ? ", new String[] {
                                Integer.toString(aCreatorID)
                        }
        );

        if (cursor.getCount() > 0){
            if(cursor.moveToFirst()) {
                do{
                    viewerList.add(cursor.getString(1) + " "
                            + cursor.getString(2));
                    viewerID.add(cursor.getInt(0));
                }while(cursor.moveToNext());
            }
        }

        return new DropDownList(viewerList, viewerID);
    }

    public DropDownList loadCreators() throws Exception {
        final List<String> nameList  = new ArrayList<String>();
        final ArrayList<Integer> idList = new ArrayList<Integer>();
        SQLiteDatabase db = this.getWritableDatabase();

        Content content = null;

        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
                "SELECT id, first_name, last_name FROM Account " +
                        "WHERE usertype = ? ", new String[] {
                        Integer.toString(2)
                }
        );

        if (cursor.getCount() > 0){
            if(cursor.moveToFirst()) {
                do{
                    nameList.add(cursor.getString(1) + " "
                            + cursor.getString(2));
                    idList.add(cursor.getInt(0));
                }while(cursor.moveToNext());
            }
        }
        else throw new Exception("You don't have any viewers");

        return new DropDownList(nameList, idList);
    }


}