package com.example.jobreferralhiringtrackerplatform;

/**
 * ============================================================
 * CLASS: JobApplication.java
 * ============================================================
 * PURPOSE:
 *   This is a "Model" class — it represents a single job application
 *   as a Java object. Think of it like a row in a spreadsheet where
 *   each column (id, company, role, etc.) is a field in this class.
 *
 * WHY WE NEED THIS:
 *   Instead of passing 7 separate variables around the app, we wrap
 *   all job application data into ONE object. This makes our code
 *   cleaner, easier to read, and easier to maintain.
 *
 * PATTERN USED: POJO (Plain Old Java Object)
 *   - private fields (data is protected)
 *   - public getters/setters (controlled access)
 * ============================================================
 */
public class JobApplication {

    // ─────────────────────────────────────────────────────────
    // FIELDS (instance variables)
    // ─────────────────────────────────────────────────────────
    // 'private' means only THIS class can directly touch these variables.
    // All other classes must use the getter/setter methods below.

    private int id;           // Unique database row ID (auto-assigned by SQLite)
    private String company;   // Name of the company (e.g., "Google")
    private String role;      // Job title (e.g., "Android Developer")
    private String recruiter; // Name of the recruiter who contacted you
    private String date;      // Date you applied (e.g., "2024-06-01")
    private String status;    // Current status: Applied / Interview / Rejected / Selected
    private String notes;     // Any extra notes about this application
    private String referralPerson; // Name of person who referred you (optional)

    // ─────────────────────────────────────────────────────────
    // CONSTRUCTOR 1: Full constructor (used when loading from DB)
    // ─────────────────────────────────────────────────────────
    /**
     * A constructor is a special method called when you create a new object.
     * This one accepts ALL fields including the database ID.
     *
     * USAGE: When fetching existing records from SQLite, we already
     * have the ID, so we use this constructor.
     *
     * Example:
     *   JobApplication job = new JobApplication(1, "Google", "Dev", "John", "2024-06-01", "Applied", "Great role", "Alice");
     */
    public JobApplication(int id, String company, String role, String recruiter,
                          String date, String status, String notes, String referralPerson) {
        this.id = id;               // 'this.id' refers to the field above; 'id' is the parameter
        this.company = company;
        this.role = role;
        this.recruiter = recruiter;
        this.date = date;
        this.status = status;
        this.notes = notes;
        this.referralPerson = referralPerson;
    }

    // ─────────────────────────────────────────────────────────
    // CONSTRUCTOR 2: Without ID (used when inserting NEW record)
    // ─────────────────────────────────────────────────────────
    /**
     * When adding a brand-new job application, we don't have an ID yet
     * because SQLite hasn't assigned one. So we skip the 'id' parameter.
     *
     * USAGE: When the user fills the form and taps "Save".
     */
    public JobApplication(String company, String role, String recruiter,
                          String date, String status, String notes, String referralPerson) {
        this.company = company;
        this.role = role;
        this.recruiter = recruiter;
        this.date = date;
        this.status = status;
        this.notes = notes;
        this.referralPerson = referralPerson;
    }

    // ─────────────────────────────────────────────────────────
    // GETTERS — These let other classes READ the private fields
    // ─────────────────────────────────────────────────────────

    /** Returns the database row ID of this application */
    public int getId() { return id; }

    /** Returns the company name */
    public String getCompany() { return company; }

    /** Returns the job role/title */
    public String getRole() { return role; }

    /** Returns the recruiter's name */
    public String getRecruiter() { return recruiter; }

    /** Returns the application date string */
    public String getDate() { return date; }

    /** Returns the current status (Applied, Interview, Rejected, Selected) */
    public String getStatus() { return status; }

    /** Returns any notes the user saved */
    public String getNotes() { return notes; }

    /** Returns the referral person's name */
    public String getReferralPerson() { return referralPerson; }

    // ─────────────────────────────────────────────────────────
    // SETTERS — These let other classes WRITE to private fields
    // ─────────────────────────────────────────────────────────

    public void setId(int id) { this.id = id; }
    public void setCompany(String company) { this.company = company; }
    public void setRole(String role) { this.role = role; }
    public void setRecruiter(String recruiter) { this.recruiter = recruiter; }
    public void setDate(String date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setReferralPerson(String referralPerson) { this.referralPerson = referralPerson; }
}