package pl.tlasica.habitoo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database version.
    public static final int 		DATABASE_VERSION = 1;
    public static final String 	DATABASE_NAME = "goalero.db";

	static final String SQL_CREATE_GOAL =
	"CREATE TABLE goal ( " +
	"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
	"name TEXT NOT NULL," +
	"createdon INTEGER NOT NULL," +			// trzymane jako int(YYYYMMDD)
	"removedon INTEGER," +					// trzymane jako int(YYYYMMDD)
	"desc TEXT," +
	"extra TEXT);";
			
	static final String SQL_CREATE_GOALDAY =
	"CREATE TABLE goalday ( " +
	"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
	"goalid INTEGER NOT NULL," +			
	"tstamp INTEGER NOT NULL," +			// trzymane jako int(YYYYMMDD)
	"status INTEGER," +
	"notes TEXT," + 
	"extra TEXT);";
	
    private static DatabaseHelper	singleton;
    
    public static synchronized DatabaseHelper getInstance(Context context) {
    	if (singleton == null) {
    		singleton = new DatabaseHelper( context ); 
    	}
    	return singleton;
    }
    
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_GOAL);
        db.execSQL(SQL_CREATE_GOALDAY);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	// TODO: Nothing so far
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void newGoal(String name, String desc, Calendar from) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	// Create a map of values
    	ContentValues values = new ContentValues();
    	values.put("name", name);
    	if (desc != null) values.put("desc", desc);
    	values.put("createdon", Handy.calToNum(from));
    	// Insert
    	db.insert("goal", "null", values);    	
    }
       
    // load all goals created and not removed
    public List<Goal> getOpenGoals(Calendar day) {
    	List<Goal> res = new ArrayList<Goal>();
    	SQLiteDatabase db = getReadableDatabase();
    	int dayNum = Handy.calToNum(day); 
    	Cursor cur = db.rawQuery("select _id, name, desc, createdon from goal where createdon<=? and ((removedon is null) or (removedon>?)) order by _id asc", 
    			new String[]{String.valueOf(dayNum), String.valueOf(dayNum)}); 
    	if (cur.moveToFirst() ) {
	    	do {
	    		Goal g = new Goal();
	    		g.id = cur.getInt(0);
	    		g.name = cur.getString(1);
	    		g.desc = cur.getString(2);
	    		g.createdOn = Handy.numToCal( cur.getInt(3) );
	    		res.add(g);
	    	} while( cur.moveToNext() );
    	}    	    	
    	return res;
    	//TODO: wczytać statusy na każdy dzień
    	//TODO wczytać liczniki udanych na każdy dzień
    }
        
    // load all statuses reported for particular date
    public List<GoalDay> getDailyGoalsStatus(Calendar day) {
    	List<GoalDay> res = new ArrayList<GoalDay>();
    	
    	SQLiteDatabase db = getReadableDatabase();
    	int dayNum = Handy.calToNum( day );
    	Cursor cur = db.rawQuery("select _id, goalid, tstamp, status, notes from goalday where tstamp=? order by goalid asc", new String[]{String.valueOf(dayNum)}); 
    	if (cur.moveToFirst() ) {
	    	do {
	    		GoalDay gd = new GoalDay();
	    		gd.id = cur.getInt(0);
	    		gd.goalId = cur.getInt(1);
	    		gd.tstamp = Handy.numToCal( cur.getInt(2));
	    		Integer status = cur.getInt(3);
	    		if (status!=null) gd.status = (status>0);
	    		res.add(gd);
	    	} while( cur.moveToNext() );
    	}    	    	
    	return res;
    }

    public void saveDone(int goalId, Calendar day, Boolean done ) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put("status", done);
    	
    	int updated = db.update("goalday", values, "(goalid=? and tstamp=?)", 
    			new String[]{String.valueOf(goalId), String.valueOf(Handy.calToNum(day))});
    	if (updated == 0) {
    		values.put("goalid", goalId);
    		values.put("tstamp", Handy.calToNum(day));
    		db.insert("goalday", null, values);    		
    	}
    }
        
    /*

    
    public long getNumEntries() {
    	SQLiteDatabase db = getReadableDatabase();
    	long rows = DatabaseUtils.queryNumEntries(db, Database.Entry.TABLE_NAME);
    	return rows;
    }
    
    private static final String tstampDescSortOrder = Database.Entry.COLUMN_NAME_TSTAMP + " DESC";
        
    public MoodEntry getLastEntry() {
    	SQLiteDatabase db = getReadableDatabase();
    	String[] projection = columns;
    	String sort = tstampDescSortOrder;
    	String limit = "1";
    	Cursor cur = db.query(Database.Entry.TABLE_NAME, projection, null, null, null, null, sort, limit);
    	MoodEntry res = null;
    	if (cur.moveToFirst()) {
    		res = cursorToEntry(cur);
    	}
    	cur.close();
    	return res;
    }
    
    public Cursor fetchAllEntries() {
    	SQLiteDatabase db = getReadableDatabase();
    	Cursor cur = db.rawQuery("select rowid _id, * from entry order by tstamp desc", null);
    	if (cur != null) {
    		   cur.moveToFirst();
    	}
    	return cur;
    }
    
    public Map<Mood, Long> fetchStatistics(int numDays) {
    	Map<Mood,Long> res = new HashMap<Mood,Long>();

    	Calendar cal = GregorianCalendar.getInstance();
    	cal.add( Calendar.DAY_OF_YEAR, -numDays);
    	long fromDateMillis = cal.getTimeInMillis();
    	
    	SQLiteDatabase db = getReadableDatabase();
    	Cursor cur = db.rawQuery("select mood, count(1) from entry where tstamp>? group by mood", new String[]{String.valueOf(fromDateMillis)}); 

    	if (cur.moveToFirst() ) {
	    	do {
	    		String mood = cur.getString( 0 );
	    		Long count = cur.getLong( 1 );
	    		res.put(Mood.fromString(mood), count);	    		
	    		// Log.d("fetchStatistics()", mood + " => " + String.valueOf(count));	    		
	    	} while( cur.moveToNext() );
    	}    	    	
    	
    	return res;
    }
    
        
    public MoodEntry cursorToEntry(Cursor cur) {
		long dt = cur.getLong( 0 );
		String mood = cur.getString( 1 );
		String message = cur.getString( 2 );
		return MoodEntry.create(mood, dt, message);    	
    }
    */
}
