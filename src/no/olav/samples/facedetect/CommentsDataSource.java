package no.olav.samples.facedetect;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CommentsDataSource {

  // Database fields
  private SQLiteDatabase database;
  private MySQLiteHelper dbHelper;
  private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
      MySQLiteHelper.COLUMN_COMMENT };

  public CommentsDataSource(Context context) {
    dbHelper = new MySQLiteHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public Comment createComment(String comment) {
    ContentValues values = new ContentValues();
    values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
    long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
        values);
    Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    Comment newComment = cursorToComment(cursor);
    cursor.close();
    return newComment;
  }

  public void deleteComment(Comment comment) {
    long id = comment.getId();
    System.out.println("Comment deleted with id: " + id);
    database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
        + " = " + id, null);
  }
  
  public String[] getAllCommentsArray() {
	  String[] commentArray = new String[2000];
      
	  Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
	        allColumns, null, null, null, null, null);
	  int i = 0;
	   
	  cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Comment comment = cursorToComment(cursor);
	     
	      
	      commentArray[i] = comment.toString();
	      
	      String testComment = comment.toString();
	      //Log.i("test", "TestComment datasource   "+testComment +commentArray[i]);
	      
	      
	      
	      String strLastName = "Hansen";
	      String strAge = "33";
	      String strPoints = "333";

	      
	      i++;
	      cursor.moveToNext();
	    }
	    
	    // Make sure to close the cursor
	    cursor.close();
	    return commentArray;
	  }

  public List<Comment> getAllComments() {
    List<Comment> comments = new ArrayList<Comment>();
    int i = 0;
    Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Comment comment = cursorToComment(cursor);
      comments.add(comment);
      cursor.moveToNext();
      i++;
      
    }
    Log.i("test", "Elements in database   "+i);
    // Make sure to close the cursor
    cursor.close();
    return comments;
  }

  private Comment cursorToComment(Cursor cursor) {
    Comment comment = new Comment();
    comment.setId(cursor.getLong(0));
    comment.setComment(cursor.getString(1));
    return comment;
  }
} 
