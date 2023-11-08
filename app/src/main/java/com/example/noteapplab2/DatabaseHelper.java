    package com.example.noteapplab2;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.graphics.Bitmap;
    import android.widget.Toast;

    import androidx.annotation.Nullable;

    class DatabaseHelper extends SQLiteOpenHelper {

        private Context context;
        private static final String DATABASE_NAME = "myNoteApp.db";
        private static final int DATABASE_VERSION = 1;
        private static final String TABLE_NAME = "my_note";
        private static final String COLUMN_ID = "_id";
        private static final String COLUMN_TITLE = "note_title";
        private static final String COLUMN_DESCRIPTION = "note_desc";
        private static final String COLUMN_COLOR = "note_color";
        private static final String COLUMN_IMAGE = "image_uri";
        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_TITLE + " TEXT, " + COLUMN_DESCRIPTION + " TEXT, " + COLUMN_COLOR + " TEXT, " + COLUMN_IMAGE + " TEXT);";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);

        }

        public void addNote(String title, String description, String color, String imageUri){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_DESCRIPTION, description);
            values.put(COLUMN_COLOR, color);
            values.put(COLUMN_IMAGE, imageUri);
            long result = db.insert(TABLE_NAME,null,values);

            if(result == -1) {
                Toast.makeText(context, "Failed to save note.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Note saved!", Toast.LENGTH_SHORT).show();
            }
        }

        Cursor readAllData(){
            String query = "SELECT * FROM " + TABLE_NAME+ ";";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = null;
            if(db!= null){
                cursor = db.rawQuery(query, null);
            }
            return cursor;
        }

        //function to update note
        public void updateData(String note_id, String title, String description, String colour, String image_uri){
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_DESCRIPTION, description);
            values.put(COLUMN_COLOR, colour);
            values.put(COLUMN_IMAGE, image_uri);

            long result = sqLiteDatabase.update(TABLE_NAME, values, "_id=?", new String[] {note_id});
            if (result == -1){
                Toast.makeText(context, "Failed to update note.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Note updated!", Toast.LENGTH_SHORT).show();
            }
        }

        public void deleteNote(String note_id) {
            SQLiteDatabase db = this.getWritableDatabase();

            long result = db.delete(TABLE_NAME, COLUMN_ID+" = ?", new String[] {note_id});

            if (result == -1){
                Toast.makeText(context, "Failed to delete note.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Note deleted!", Toast.LENGTH_SHORT).show();
            }
        }
    }



