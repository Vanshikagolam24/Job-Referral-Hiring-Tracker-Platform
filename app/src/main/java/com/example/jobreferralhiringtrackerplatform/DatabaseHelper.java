package com.example.jobreferralhiringtrackerplatform;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
//import com.example.jobtracker.database.DatabaseHelper;

/**
 * ============================================================
 * CLASS: DatabaseHelper.java
 * ============================================================
 * PURPOSE:
 *   This class manages ALL interactions with the SQLite database.
 *   It is the single point of truth for creating, reading, updating,
 *   and deleting (CRUD) records in our local database.
 *
 * EXTENDS: SQLiteOpenHelper
 *   Android provides SQLiteOpenHelper as a base class that handles:
 *     - Creating the database file on first launch
 *     - Upgrading the database when we change its structure
 *   We extend it and override two required methods: onCreate() and onUpgrade()
 *
 * DESIGN PATTERN: Singleton (one instance shared across the whole app)
 *   We'll access this via: DatabaseHelper db = new DatabaseHelper(context);
 *
 * HOW SQLite WORKS ON ANDROID:
 *   - The database is stored as a .db file inside the app's private storage
 *   - No internet connection needed — it's 100% local
 *   - Data persists even after the app is closed
 * ============================================================
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // ─────────────────────────────────────────────────────────
    // DATABASE CONSTANTS
    // ─────────────────────────────────────────────────────────
    // Naming these as constants (static final) means:
    //   - They never change at runtime
    //   - We reference them by name, not by magic strings (avoids typos)

    /** The name of the .db file stored on the device */
    private static final String DATABASE_NAME = "JobTracker.db";

    /**
     * Database version. Increment this (e.g., 2, 3, 4...) whenever
     * you change the table structure. onUpgrade() will be called automatically.
     */
    private static final int DATABASE_VERSION = 1;

    // ── TABLE: job_applications ───────────────────────────────
    /** Table name for all job application records */
    public static final String TABLE_JOBS = "job_applications";

    // Column names for the job_applications table
    public static final String COL_ID          = "id";           // Auto-increment primary key
    public static final String COL_COMPANY     = "company";      // Company name
    public static final String COL_ROLE        = "role";         // Job title
    public static final String COL_RECRUITER   = "recruiter";    // Recruiter name
    public static final String COL_DATE        = "date";         // Application date
    public static final String COL_STATUS      = "status";       // Applied/Interview/Rejected/Selected
    public static final String COL_NOTES       = "notes";        // Extra notes
    public static final String COL_REFERRAL    = "referral_person"; // Who referred you

    // ── TABLE: interviews ─────────────────────────────────────
    /** Table name for interview records */
    public static final String TABLE_INTERVIEWS = "interviews";

    public static final String COL_INT_ID       = "id";
    public static final String COL_INT_JOB_ID   = "job_id";       // Links to job_applications.id
    public static final String COL_INT_DATE      = "interview_date";
    public static final String COL_INT_TIME      = "interview_time";
    public static final String COL_INT_TYPE      = "interview_type"; // Phone/Video/In-Person
    public static final String COL_INT_INAME     = "interviewer_name";
    public static final String COL_INT_NOTES     = "notes";

    // ── TABLE: referrals ──────────────────────────────────────
    /** Table name for referral tracking */
    public static final String TABLE_REFERRALS = "referrals";

    public static final String COL_REF_ID        = "id";
    public static final String COL_REF_NAME      = "referral_name";
    public static final String COL_REF_COMPANY   = "company";
    public static final String COL_REF_CONTACT   = "contact_number";
    public static final String COL_REF_LINKEDIN  = "linkedin";
    public static final String COL_REF_STATUS    = "status";
    public static final String COL_REF_FOLLOWUP  = "follow_up_date";

    // ─────────────────────────────────────────────────────────
    // SQL CREATE STATEMENTS
    // ─────────────────────────────────────────────────────────
    /**
     * SQL to create the job_applications table.
     *
     * BREAKDOWN:
     *   INTEGER PRIMARY KEY AUTOINCREMENT → SQLite auto-assigns a unique number
     *   TEXT NOT NULL → field is required (can't be empty/null)
     *   TEXT → field is optional
     */
    private static final String CREATE_TABLE_JOBS =
            "CREATE TABLE " + TABLE_JOBS + " ("
                    + COL_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_COMPANY   + " TEXT NOT NULL, "
                    + COL_ROLE      + " TEXT NOT NULL, "
                    + COL_RECRUITER + " TEXT, "
                    + COL_DATE      + " TEXT NOT NULL, "
                    + COL_STATUS    + " TEXT NOT NULL, "
                    + COL_NOTES     + " TEXT, "
                    + COL_REFERRAL  + " TEXT"
                    + ");";

    /** SQL to create the interviews table */
    private static final String CREATE_TABLE_INTERVIEWS =
            "CREATE TABLE " + TABLE_INTERVIEWS + " ("
                    + COL_INT_ID     + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_INT_JOB_ID + " INTEGER, "          // Foreign key to job_applications
                    + COL_INT_DATE   + " TEXT NOT NULL, "
                    + COL_INT_TIME   + " TEXT, "
                    + COL_INT_TYPE   + " TEXT, "
                    + COL_INT_INAME  + " TEXT, "
                    + COL_INT_NOTES  + " TEXT, "
                    + "FOREIGN KEY(" + COL_INT_JOB_ID + ") REFERENCES " + TABLE_JOBS + "(" + COL_ID + ")"
                    + ");";

    /** SQL to create the referrals table */
    private static final String CREATE_TABLE_REFERRALS =
            "CREATE TABLE " + TABLE_REFERRALS + " ("
                    + COL_REF_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_REF_NAME     + " TEXT NOT NULL, "
                    + COL_REF_COMPANY  + " TEXT, "
                    + COL_REF_CONTACT  + " TEXT, "
                    + COL_REF_LINKEDIN + " TEXT, "
                    + COL_REF_STATUS   + " TEXT, "
                    + COL_REF_FOLLOWUP + " TEXT"
                    + ");";

    // ─────────────────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────────────────
    /**
     * The constructor calls the parent SQLiteOpenHelper constructor.
     *
     * @param context  Android Context (needed to locate the app's storage folder)
     *
     * super() call parameters:
     *   context      → the app context
     *   DATABASE_NAME → the filename for our .db file
     *   null          → no custom cursor factory (we use default)
     *   DATABASE_VERSION → tells SQLite which version to expect
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ─────────────────────────────────────────────────────────
    // onCreate() — Called ONCE when the database is first created
    // ─────────────────────────────────────────────────────────
    /**
     * This method runs automatically the very first time the app
     * opens the database (i.e., fresh install, or after data is cleared).
     *
     * We use it to create all our tables.
     *
     * @param db  The SQLiteDatabase object that Android hands us
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute the SQL strings we built above
        db.execSQL(CREATE_TABLE_JOBS);        // Creates job_applications table
        db.execSQL(CREATE_TABLE_INTERVIEWS);  // Creates interviews table
        db.execSQL(CREATE_TABLE_REFERRALS);   // Creates referrals table
    }

    // ─────────────────────────────────────────────────────────
    // onUpgrade() — Called when DATABASE_VERSION increases
    // ─────────────────────────────────────────────────────────
    /**
     * When you change the DB structure and bump DATABASE_VERSION,
     * Android calls this automatically on next app open.
     *
     * SIMPLE STRATEGY (used here): Drop old tables and recreate them.
     * NOTE: This deletes all data — for production apps, write proper
     *       ALTER TABLE migration scripts instead.
     *
     * @param db         The database
     * @param oldVersion Previous version number
     * @param newVersion New version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables if they exist (order matters for foreign keys)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REFERRALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS);

        // Recreate from scratch
        onCreate(db);
    }

    // ─────────────────────────────────────────────────────────
    // onConfigure() — Enable foreign key support
    // ─────────────────────────────────────────────────────────
    /**
     * SQLite foreign key constraints are DISABLED by default on Android.
     * We must explicitly enable them each time a database connection is opened.
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // ═══════════════════════════════════════════════════════════
    // ██  JOB APPLICATION CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════

    // ─────────────────────────────────────────────────────────
    // INSERT — Add a new job application to the database
    // ─────────────────────────────────────────────────────────
    /**
     * Saves a new JobApplication object into the database.
     *
     * HOW IT WORKS:
     *   1. Open the database in write mode
     *   2. Create a ContentValues map (like a HashMap of column→value pairs)
     *   3. Call db.insert() which generates & runs the INSERT SQL for us
     *   4. Returns the new row's ID (or -1 if it failed)
     *
     * @param job  The JobApplication object to save
     * @return     The row ID of the newly inserted record (-1 on failure)
     */
    public long insertJob(JobApplication job) {
        // getWritableDatabase() opens (or creates) the DB in read/write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // ContentValues is Android's key-value container for DB operations
        // It maps column names to values (like: "company" → "Google")
        ContentValues values = new ContentValues();
        values.put(COL_COMPANY,   job.getCompany());
        values.put(COL_ROLE,      job.getRole());
        values.put(COL_RECRUITER, job.getRecruiter());
        values.put(COL_DATE,      job.getDate());
        values.put(COL_STATUS,    job.getStatus());
        values.put(COL_NOTES,     job.getNotes());
        values.put(COL_REFERRAL,  job.getReferralPerson());

        // db.insert(tableName, nullColumnHack, values)
        // nullColumnHack = null means: don't insert a row with all NULLs
        // Returns: the new row ID, or -1 if an error occurred
        long newRowId = db.insert(TABLE_JOBS, null, values);

        db.close(); // Always close the connection when done to free resources
        return newRowId;
    }

    // ─────────────────────────────────────────────────────────
    // READ ALL — Fetch every job application from the database
    // ─────────────────────────────────────────────────────────
    /**
     * Retrieves ALL job applications, sorted by most recently added first.
     *
     * HOW IT WORKS:
     *   1. Open DB in read mode (lighter-weight than write mode)
     *   2. Run a SELECT query → get a Cursor (like a pointer to the results)
     *   3. Loop through each row in the Cursor
     *   4. Build JobApplication objects and add them to a List
     *   5. Return the List
     *
     * WHAT IS A CURSOR?
     *   A Cursor is like a spreadsheet pointer. It starts BEFORE the first row.
     *   You call moveToNext() to advance row by row.
     *   You read columns using getString(columnIndex) or getInt(columnIndex).
     *
     * @return  List of all JobApplication objects (empty list if none exist)
     */
    public List<JobApplication> getAllJobs() {
        List<JobApplication> jobList = new ArrayList<>(); // Start with an empty list

        // getReadableDatabase() is sufficient when we only need to read
        SQLiteDatabase db = this.getReadableDatabase();

        // rawQuery(sql, selectionArgs)
        // selectionArgs = null because we have no WHERE clause parameters here
        // ORDER BY id DESC → newest entries appear first
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_JOBS + " ORDER BY " + COL_ID + " DESC",
                null
        );

        // moveToFirst() positions the cursor at the first row.
        // Returns false if the result set is empty, so we check it.
        if (cursor.moveToFirst()) {
            do {
                // For each row, read each column by its name and build a JobApplication object
                // cursor.getColumnIndex("column_name") → returns the column's position number
                // cursor.getString(position) → returns the value at that position as a String
                // cursor.getInt(position) → returns the value as an integer

                JobApplication job = new JobApplication(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_COMPANY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RECRUITER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REFERRAL))
                );

                jobList.add(job); // Add this job to our list

            } while (cursor.moveToNext()); // Move to next row; stop when no more rows
        }

        cursor.close(); // Always close the cursor to release memory
        db.close();
        return jobList;
    }

    // ─────────────────────────────────────────────────────────
    // READ ONE — Fetch a single job by its ID
    // ─────────────────────────────────────────────────────────
    /**
     * Fetches one specific job application by its database ID.
     * Used when the user taps on a job to view its full details.
     *
     * @param id  The database row ID of the job to fetch
     * @return    The JobApplication object, or null if not found
     */
    public JobApplication getJobById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // query() is a structured alternative to rawQuery()
        // Parameters: table, columns[], selection, selectionArgs[], groupBy, having, orderBy
        // "?" in selection is a placeholder; selectionArgs replaces it safely (prevents SQL injection)
        Cursor cursor = db.query(
                TABLE_JOBS,
                null,                          // null = select all columns (SELECT *)
                COL_ID + " = ?",              // WHERE id = ?
                new String[]{String.valueOf(id)}, // Replace "?" with the actual ID
                null, null, null
        );

        JobApplication job = null;
        if (cursor.moveToFirst()) { // If a record was found
            job = new JobApplication(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_COMPANY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_RECRUITER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_REFERRAL))
            );
        }

        cursor.close();
        db.close();
        return job;
    }

    // ─────────────────────────────────────────────────────────
    // UPDATE — Modify an existing job application
    // ─────────────────────────────────────────────────────────
    /**
     * Updates an existing record in the database.
     * The record to update is identified by job.getId().
     *
     * @param job  The JobApplication with updated values (must have a valid ID)
     * @return     Number of rows affected (should be 1 on success)
     */
    public int updateJob(JobApplication job) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_COMPANY,   job.getCompany());
        values.put(COL_ROLE,      job.getRole());
        values.put(COL_RECRUITER, job.getRecruiter());
        values.put(COL_DATE,      job.getDate());
        values.put(COL_STATUS,    job.getStatus());
        values.put(COL_NOTES,     job.getNotes());
        values.put(COL_REFERRAL,  job.getReferralPerson());

        // db.update(table, values, whereClause, whereArgs)
        // This translates to: UPDATE job_applications SET ... WHERE id = ?
        int rowsAffected = db.update(
                TABLE_JOBS,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(job.getId())}
        );

        db.close();
        return rowsAffected;
    }

    // ─────────────────────────────────────────────────────────
    // DELETE — Remove a job application by ID
    // ─────────────────────────────────────────────────────────
    /**
     * Permanently deletes a job application from the database.
     *
     * @param id  The database row ID to delete
     * @return    Number of rows deleted (should be 1 on success)
     */
    public int deleteJob(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // db.delete(table, whereClause, whereArgs)
        // Translates to: DELETE FROM job_applications WHERE id = ?
        int rowsDeleted = db.delete(
                TABLE_JOBS,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return rowsDeleted;
    }

    // ─────────────────────────────────────────────────────────
    // SEARCH — Find jobs by company or role name
    // ─────────────────────────────────────────────────────────
    /**
     * Searches for job applications where company OR role contains the query string.
     * Case-insensitive because SQLite's LIKE is case-insensitive for ASCII.
     *
     * @param query  The search text (e.g., "Google", "Android")
     * @return       List of matching JobApplication objects
     */
    public List<JobApplication> searchJobs(String query) {
        List<JobApplication> jobList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // LIKE '%query%' matches any string containing "query" anywhere
        // The "%" wildcard means "any characters before/after"
        String searchQuery = "%" + query + "%";

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_JOBS
                        + " WHERE " + COL_COMPANY + " LIKE ? OR " + COL_ROLE + " LIKE ?"
                        + " ORDER BY " + COL_ID + " DESC",
                new String[]{searchQuery, searchQuery} // Two "?" in the query, so two values here
        );

        if (cursor.moveToFirst()) {
            do {
                JobApplication job = new JobApplication(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_COMPANY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RECRUITER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REFERRAL))
                );
                jobList.add(job);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return jobList;
    }

    // ─────────────────────────────────────────────────────────
    // FILTER BY STATUS
    // ─────────────────────────────────────────────────────────
    /**
     * Returns only jobs matching a specific status.
     *
     * @param status  One of: "Applied", "Interview", "Rejected", "Selected"
     * @return        Filtered list of JobApplication objects
     */
    public List<JobApplication> getJobsByStatus(String status) {
        List<JobApplication> jobList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_JOBS,
                null,
                COL_STATUS + " = ?",
                new String[]{status},
                null, null,
                COL_ID + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                JobApplication job = new JobApplication(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_COMPANY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RECRUITER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REFERRAL))
                );
                jobList.add(job);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return jobList;
    }

    // ─────────────────────────────────────────────────────────
    // COUNT HELPERS — Used by the Home Dashboard
    // ─────────────────────────────────────────────────────────

    /** Returns the total number of job applications in the database */
    public int getTotalJobCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_JOBS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0); // Column index 0 = the COUNT(*) result
        }
        cursor.close();
        db.close();
        return count;
    }

    /** Returns the count of jobs with a specific status */
    public int getCountByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_JOBS + " WHERE " + COL_STATUS + " = ?",
                new String[]{status}
        );
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // ═══════════════════════════════════════════════════════════
    // ██  INTERVIEW CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════

    /**
     * Inserts a new interview record linked to a job application.
     *
     * @param jobId          The ID of the related job application
     * @param interviewDate  Date of interview (e.g., "2024-06-15")
     * @param interviewTime  Time (e.g., "10:00 AM")
     * @param type           Type: Phone / Video / In-Person
     * @param interviewerName Name of the interviewer
     * @param notes          Any notes
     * @return               New row ID or -1 on failure
     */
    public long insertInterview(int jobId, String interviewDate, String interviewTime,
                                String type, String interviewerName, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_INT_JOB_ID, jobId);
        values.put(COL_INT_DATE,   interviewDate);
        values.put(COL_INT_TIME,   interviewTime);
        values.put(COL_INT_TYPE,   type);
        values.put(COL_INT_INAME,  interviewerName);
        values.put(COL_INT_NOTES,  notes);

        long rowId = db.insert(TABLE_INTERVIEWS, null, values);
        db.close();
        return rowId;
    }

    /**
     * Retrieves all interviews for a specific job application.
     *
     * @param jobId  The job application's ID
     * @return       List of ContentValues maps (each map = one interview row)
     */
    public List<ContentValues> getInterviewsByJobId(int jobId) {
        List<ContentValues> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_INTERVIEWS,
                null,
                COL_INT_JOB_ID + " = ?",
                new String[]{String.valueOf(jobId)},
                null, null,
                COL_INT_DATE + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                ContentValues cv = new ContentValues();
                cv.put(COL_INT_ID,    cursor.getInt(cursor.getColumnIndexOrThrow(COL_INT_ID)));
                cv.put(COL_INT_DATE,  cursor.getString(cursor.getColumnIndexOrThrow(COL_INT_DATE)));
                cv.put(COL_INT_TIME,  cursor.getString(cursor.getColumnIndexOrThrow(COL_INT_TIME)));
                cv.put(COL_INT_TYPE,  cursor.getString(cursor.getColumnIndexOrThrow(COL_INT_TYPE)));
                cv.put(COL_INT_INAME, cursor.getString(cursor.getColumnIndexOrThrow(COL_INT_INAME)));
                cv.put(COL_INT_NOTES, cursor.getString(cursor.getColumnIndexOrThrow(COL_INT_NOTES)));
                list.add(cv);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    /** Returns total interview count (used by dashboard) */
    public int getTotalInterviewCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_INTERVIEWS, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    // ═══════════════════════════════════════════════════════════
    // ██  REFERRAL CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════

    /**
     * Inserts a new referral contact into the database.
     *
     * @param name       Referrer's full name
     * @param company    Their company
     * @param contact    Phone number
     * @param linkedin   LinkedIn URL
     * @param status     Referral status (e.g., "Pending", "Done")
     * @param followUp   Follow-up date
     * @return           New row ID
     */
    public long insertReferral(String name, String company, String contact,
                               String linkedin, String status, String followUp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_REF_NAME,     name);
        values.put(COL_REF_COMPANY,  company);
        values.put(COL_REF_CONTACT,  contact);
        values.put(COL_REF_LINKEDIN, linkedin);
        values.put(COL_REF_STATUS,   status);
        values.put(COL_REF_FOLLOWUP, followUp);

        long rowId = db.insert(TABLE_REFERRALS, null, values);
        db.close();
        return rowId;
    }

    /**
     * Retrieves all referral records.
     *
     * @return  List of ContentValues, one per referral row
     */
    public List<ContentValues> getAllReferrals() {
        List<ContentValues> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_REFERRALS + " ORDER BY " + COL_REF_ID + " DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                ContentValues cv = new ContentValues();
                cv.put(COL_REF_ID,       cursor.getInt(cursor.getColumnIndexOrThrow(COL_REF_ID)));
                cv.put(COL_REF_NAME,     cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_NAME)));
                cv.put(COL_REF_COMPANY,  cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_COMPANY)));
                cv.put(COL_REF_CONTACT,  cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_CONTACT)));
                cv.put(COL_REF_LINKEDIN, cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_LINKEDIN)));
                cv.put(COL_REF_STATUS,   cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_STATUS)));
                cv.put(COL_REF_FOLLOWUP, cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_FOLLOWUP)));
                list.add(cv);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    /** Deletes a referral record by ID */
    public int deleteReferral(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_REFERRALS, COL_REF_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }
}