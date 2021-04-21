package projectomicron.studybuddy;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class SQLDatabaseConnection extends SQLiteOpenHelper {
    private static final String DBNAME = "StudyBuddy-DB";

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
                "id INTEGER PRIMARY KEY," +
                "name TEXT" +
            ")"
        );

        db.execSQL(
            "CREATE TABLE Account (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "first_name TEXT," +
                "last_name TEXT," +
                "username TEXT," +
                "password TEXT," +
                "usertype INTEGER," +
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

        this.CreateRoles();
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

    public void CreateRoles() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", "Viewer");
        contentValues.put("name", "Creator");

        long result = db.insert("Account", null, contentValues);

    }

    public String CreateAccount(String first_name, String last_name, String username,
                              String password, int reasonForUse) throws Exception
    {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("first_name", first_name);
        contentValues.put("last_name", last_name);
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("usertype", reasonForUse);

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

        if(cursor.getCount() > 0) throw new Exception("Username/Password is incorrect!");

        if (cursor.getCount() > 0){
            return "Login successful!";
        }
        else{
            return "Username/Password is incorrect!";
        }
    }

    public void GetUserIDAndUserRole(String username){

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(
        "SELECT  FROM Account " +
                "WHERE username = ?", new String[] { username } );

    }
}