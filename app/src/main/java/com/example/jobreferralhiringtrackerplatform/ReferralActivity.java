package com.example.jobreferralhiringtrackerplatform;

/**
 * ============================================================
 * CLASS: Referral.java
 * ============================================================
 * PURPOSE:
 *   Model class representing a single referral contact record.
 *   Mirrors one row in the SQLite "referrals" table.
 *
 * WHY A DEDICATED MODEL INSTEAD OF ContentValues?
 *   The existing DatabaseHelper returns referrals as ContentValues
 *   (key-value maps). A proper model class gives us:
 *     - Type safety (getString vs getAsString — compiler enforces types)
 *     - IDE autocomplete on field names
 *     - Cleaner adapter/ViewHolder code
 *     - Easy equality checks and sorting
 *
 * PATTERN: POJO (Plain Old Java Object) with getters/setters.
 * ============================================================
 */
public class ReferralActivity {

    // ─────────────────────────────────────────────────────────
    // FIELDS — match columns in the "referrals" SQLite table
    // ─────────────────────────────────────────────────────────

    private int    id;           // Primary key (SQLite auto-assigns on insert)
    private String name;         // Referral contact's full name  (required)
    private String company;      // Company where they work        (optional)
    private String contactNumber;// Phone / WhatsApp number        (optional)
    private String linkedIn;     // LinkedIn profile URL           (optional)
    private String status;       // e.g. "Pending", "Done", "Awaiting" (optional)
    private String followUpDate; // ISO date string "YYYY-MM-DD"   (optional)

    // ─────────────────────────────────────────────────────────
    // CONSTRUCTOR 1 — used when loading from database (has id)
    // ─────────────────────────────────────────────────────────
    /**
     * Full constructor — all fields including the DB-assigned id.
     * Call this when reading rows back from SQLite.
     *
     * Example:
     *   Referral r = new Referral(3, "Alice", "Google", "+91-9999", "linkedin.com/in/alice", "Pending", "2024-07-01");
     */
    public ReferralActivity(int id, String name, String company, String contactNumber,
                    String linkedIn, String status, String followUpDate) {
        this.id            = id;
        this.name          = name;
        this.company       = company;
        this.contactNumber = contactNumber;
        this.linkedIn      = linkedIn;
        this.status        = status;
        this.followUpDate  = followUpDate;
    }

    // ─────────────────────────────────────────────────────────
    // CONSTRUCTOR 2 — used when inserting a new record (no id yet)
    // ─────────────────────────────────────────────────────────
    /**
     * Insert constructor — no id because SQLite hasn't assigned one yet.
     * Call this from AddReferralActivity before calling insertReferral().
     */
    public ReferralActivity(String name, String company, String contactNumber,
                    String linkedIn, String status, String followUpDate) {
        this.name          = name;
        this.company       = company;
        this.contactNumber = contactNumber;
        this.linkedIn      = linkedIn;
        this.status        = status;
        this.followUpDate  = followUpDate;
    }

    // ─────────────────────────────────────────────────────────
    // GETTERS
    // ─────────────────────────────────────────────────────────

    public int    getId()            { return id; }
    public String getName()          { return name; }
    public String getCompany()       { return company; }
    public String getContactNumber() { return contactNumber; }
    public String getLinkedIn()      { return linkedIn; }
    public String getStatus()        { return status; }
    public String getFollowUpDate()  { return followUpDate; }

    // ─────────────────────────────────────────────────────────
    // SETTERS
    // ─────────────────────────────────────────────────────────

    public void setId(int id)                       { this.id = id; }
    public void setName(String name)                { this.name = name; }
    public void setCompany(String company)          { this.company = company; }
    public void setContactNumber(String n)          { this.contactNumber = n; }
    public void setLinkedIn(String linkedIn)        { this.linkedIn = linkedIn; }
    public void setStatus(String status)            { this.status = status; }
    public void setFollowUpDate(String followUpDate){ this.followUpDate = followUpDate; }

    // ─────────────────────────────────────────────────────────
    // UTILITY
    // ─────────────────────────────────────────────────────────

    /**
     * Safe display helper — returns value if non-null/non-empty, else fallback.
     * Usage: Referral.safe(r.getCompany(), "N/A")
     */
    public static String safe(String value, String fallback) {
        return (value == null || value.trim().isEmpty()) ? fallback : value.trim();
    }
}