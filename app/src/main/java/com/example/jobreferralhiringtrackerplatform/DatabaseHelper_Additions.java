package com.example.jobreferralhiringtrackerplatform;

// ============================================================
// FILE: DatabaseHelper_Additions.java
// ============================================================
// PURPOSE:
//   This file documents the NEW methods that must be added
//   to your existing DatabaseHelper.java.
//
//   ADD THESE METHODS directly inside DatabaseHelper.java,
//   at the end of the class body (before the closing brace).
//
//   Why are they separate here?
//     To keep the diff minimal and easy to copy-paste without
//     breaking the existing working methods.
// ============================================================
//
// ── PASTE THE FOLLOWING METHODS INTO DatabaseHelper.java ────
//
// 1. insertReferralObject(Referral)       — accepts Referral model
// 2. getAllReferralObjects()              — returns List<Referral>
// 3. getReferralById(int)                — fetch single Referral
// 4. updateReferral(Referral)            — update existing referral
// 5. getTotalReferralCount()             — for dashboard
//
// All existing methods remain intact.
// ============================================================

/*

    // ══════════════════════════════════════════════════════════
    //  REFERRAL METHODS (Referral model-based — add these)
    // ══════════════════════════════════════════════════════════

    *//**
 * Inserts a new Referral object into the referrals table.
 *
 * DIFFERENCE FROM insertReferral(String...):
 *   This accepts a Referral model object rather than loose strings,
 *   which is cleaner and less error-prone (no arg-order mistakes).
 *
 * @param referral  The Referral to save (id field is ignored)
 * @return          New row ID, or -1 on failure
 *//*
    public long insertReferralObject(Referral referral) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_REF_NAME,     referral.getName());
        values.put(COL_REF_COMPANY,  referral.getCompany());
        values.put(COL_REF_CONTACT,  referral.getContactNumber());
        values.put(COL_REF_LINKEDIN, referral.getLinkedIn());
        values.put(COL_REF_STATUS,   referral.getStatus());
        values.put(COL_REF_FOLLOWUP, referral.getFollowUpDate());

        long rowId = db.insert(TABLE_REFERRALS, null, values);
        db.close();
        return rowId;
    }

    *//**
 * Returns all referral records as a typed List<Referral>.
 *
 * Ordered newest-first (DESC by id).
 *
 * @return  List of Referral objects (empty list if none)
 *//*
    public List<Referral> getAllReferralObjects() {
        List<Referral> list = new ArrayList<>();
        SQLiteDatabase db   = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_REFERRALS + " ORDER BY " + COL_REF_ID + " DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Referral r = new Referral(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_REF_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_COMPANY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_CONTACT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_LINKEDIN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_FOLLOWUP))
                );
                list.add(r);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    *//**
 * Fetches a single Referral by its database ID.
 *
 * @param id  The row ID to look up
 * @return    Referral object, or null if not found
 *//*
    public Referral getReferralById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_REFERRALS,
                null,
                COL_REF_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        Referral r = null;
        if (cursor.moveToFirst()) {
            r = new Referral(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_REF_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_COMPANY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_CONTACT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_LINKEDIN)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_STATUS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_REF_FOLLOWUP))
            );
        }

        cursor.close();
        db.close();
        return r;
    }

    *//**
 * Updates an existing referral row.
 * The row to update is identified by referral.getId().
 *
 * @param referral  Referral with updated field values (must have valid id)
 * @return          Number of rows affected (1 = success, 0 = not found)
 *//*
    public int updateReferral(Referral referral) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_REF_NAME,     referral.getName());
        values.put(COL_REF_COMPANY,  referral.getCompany());
        values.put(COL_REF_CONTACT,  referral.getContactNumber());
        values.put(COL_REF_LINKEDIN, referral.getLinkedIn());
        values.put(COL_REF_STATUS,   referral.getStatus());
        values.put(COL_REF_FOLLOWUP, referral.getFollowUpDate());

        int rows = db.update(
                TABLE_REFERRALS,
                values,
                COL_REF_ID + " = ?",
                new String[]{String.valueOf(referral.getId())}
        );

        db.close();
        return rows;
    }

    *//**
 * Returns the total number of referral records.
 * Used by the Dashboard Analytics screen.
 *
 * @return  Row count of the referrals table
 *//*
    public int getTotalReferralCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REFERRALS, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

*/

// ============================================================
// END OF ADDITIONS — paste the above block into DatabaseHelper.java
// ============================================================

public class DatabaseHelper_Additions {
    // This class is a documentation placeholder only.
    // It will not compile if included directly — copy the
    // method bodies above into your DatabaseHelper.java instead.
}