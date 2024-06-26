package com.example.tasker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


internal class MyDatabaseHelper(private val context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Create the table when the database is created
        override fun onCreate(db: SQLiteDatabase) {
        // SQL query to create the table
        val query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DATE + " DATE);"
        // Execute the query
        db.execSQL(query)
    }

    // Upgrade the database if needed
    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        // Drop the table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        // Recreate the table
        onCreate(db)
    }

    // Add a task to the database
    fun addTask(title: String?, description: String?, date: Date) {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_TITLE, title)
        cv.put(COLUMN_DESCRIPTION, description)
        cv.put(COLUMN_DATE, formatDate(date))
        val result = db.insert(TABLE_NAME, null, cv)
        if (result == -1L) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDate(date: Date): String {
        // Format the date as desired
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }


    // Read all data from the database
    fun readAllData(): Cursor? {
        val query = "SELECT * FROM " + TABLE_NAME
        val db = this.readableDatabase

        var cursor: Cursor? = null
        if (db != null) {
            cursor = db.rawQuery(query, null)
        }
        return cursor
    }

    // Update data in the database
    fun updateData(row_id: String, title: String?, description: String?, date: Date?) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, title)
        cv.put(COLUMN_DESCRIPTION, description)
        cv.put(COLUMN_DATE, SimpleDateFormat("yyyy-MM-dd").format(date)) // Format date to string
        db.update(TABLE_NAME, cv, "$COLUMN_ID=?", arrayOf(row_id))

        val result = db.update(TABLE_NAME, cv, "_id=?", arrayOf(row_id)).toLong()
        if (result == -1L) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // Delete a specific row from the database
    fun deleteOneRow(row_id: String) {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "_id=?", arrayOf(row_id)).toLong()
        if (result == -1L) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteAllData() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM " + TABLE_NAME)
    }

    companion object {
        // Database name and version
        private const val DATABASE_NAME = "TaskManager.db"
        private const val DATABASE_VERSION = 1

        // Table name and column names
        private const val TABLE_NAME = "task_list"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_TITLE = "task_title"
        private const val COLUMN_DESCRIPTION = "task_description"
        private const val COLUMN_DATE = "task_date"
    }
}