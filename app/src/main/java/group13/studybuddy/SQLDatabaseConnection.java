package group13.studybuddy;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        return content;
    }



    public String updateContent(int aUserID, int aContentID, String aContent){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("text_block", aContent);
        long result = db.update(
                "Content",
                contentValues,
                "_id = " + aContentID,
                null );

        if(result == -1){
            return "Content not updated";
        }
        else{
            return "Content updated successfully!";
        }
    }

    public void loadViewersByCreatorId(){

    }


}