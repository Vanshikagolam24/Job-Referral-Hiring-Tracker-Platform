package com.example.jobreferralhiringtrackerplatform;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * ============================================================
 * CLASS: JobDetailActivity.java
 * ============================================================
 * PURPOSE:
 *   Shows the complete details of a single job application.
 *   Allows the user to Edit, Delete, or go Back.
 *
 * HOW IT RECEIVES DATA:
 *   The previous screen (ViewJobsActivity) passes the job's ID
 *   via Intent extras. We retrieve it here and use it to
 *   fetch the full job object from the database.
 *
 * KEY CONCEPT: Intent Extras
 *   Extras are key-value pairs attached to an Intent, used to
 *   pass data between Activities — like URL parameters for screens.
 *
 *   SENDING:   intent.putExtra("job_id", 5);
 *   RECEIVING: int id = getIntent().getIntExtra("job_id", -1);
 *              (-1 is the default value if "job_id" wasn't found)
 * ============================================================
 */
public class JobDetailActivity extends AppCompatActivity {

    // ─────────────────────────────────────────────────────────
    // CONSTANT: Intent Extra Key
    // ─────────────────────────────────────────────────────────
    /** The key string used to pass the job ID between Activities */
    public static final String EXTRA_JOB_ID = "job_id";

    // ─────────────────────────────────────────────────────────
    // UI REFERENCES
    // ─────────────────────────────────────────────────────────
    private TextView tvDetailCompany;
    private TextView tvDetailRole;
    private TextView tvDetailRecruiter;
    private TextView tvDetailDate;
    private TextView tvDetailStatus;
    private TextView tvDetailNotes;
    private TextView tvDetailReferral;
    private Button   btnEdit;
    private Button   btnDelete;

    // ─────────────────────────────────────────────────────────
    // STATE
    // ─────────────────────────────────────────────────────────
    private DatabaseHelper dbHelper;
    private JobApplication currentJob; // The job being displayed
    private int jobId;                 // Retrieved from Intent

    // ─────────────────────────────────────────────────────────
    // onCreate()
    // ─────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Job Details");
        }

        dbHelper = new DatabaseHelper(this);

        // ── Retrieve the passed job ID from Intent extras ─────
        // getIntent() gets the Intent that started this Activity
        // getIntExtra(key, defaultValue) reads an int extra
        jobId = getIntent().getIntExtra(EXTRA_JOB_ID, -1);

        // Safety check: if no valid ID was passed, close this screen
        if (jobId == -1) {
            Toast.makeText(this, "Error: Job not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadJobDetails();
        setClickListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // ─────────────────────────────────────────────────────────
    // onResume() — Reload data if returning from EditActivity
    // ─────────────────────────────────────────────────────────
    @Override
    protected void onResume() {
        super.onResume();
        loadJobDetails(); // Refresh in case job was edited
    }

    // ─────────────────────────────────────────────────────────
    // initViews()
    // ─────────────────────────────────────────────────────────
    private void initViews() {
        tvDetailCompany   = findViewById(R.id.tvDetailCompany);
        tvDetailRole      = findViewById(R.id.tvDetailRole);
        tvDetailRecruiter = findViewById(R.id.tvDetailRecruiter);
        tvDetailDate      = findViewById(R.id.tvDetailDate);
        tvDetailStatus    = findViewById(R.id.tvDetailStatus);
        tvDetailNotes     = findViewById(R.id.tvDetailNotes);
        tvDetailReferral  = findViewById(R.id.tvDetailReferral);
        btnEdit           = findViewById(R.id.btnEdit);
        btnDelete         = findViewById(R.id.btnDelete);
    }

    // ─────────────────────────────────────────────────────────
    // loadJobDetails() — Fetch from DB and populate UI
    // ─────────────────────────────────────────────────────────
    /**
     * Fetches the JobApplication from the database using the ID
     * and populates all the TextViews with its data.
     *
     * We use helper method safeText() to handle null values gracefully.
     */
    private void loadJobDetails() {
        currentJob = dbHelper.getJobById(jobId);

        if (currentJob == null) {
            Toast.makeText(this, "Job record not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailCompany.setText(currentJob.getCompany());
        tvDetailRole.setText(currentJob.getRole());
        tvDetailRecruiter.setText(safeText(currentJob.getRecruiter(), "Not specified"));
        tvDetailDate.setText(currentJob.getDate());
        tvDetailStatus.setText(currentJob.getStatus());
        tvDetailNotes.setText(safeText(currentJob.getNotes(), "No notes added"));
        tvDetailReferral.setText(safeText(currentJob.getReferralPerson(), "No referral"));
    }

    // ─────────────────────────────────────────────────────────
    // setClickListeners()
    // ─────────────────────────────────────────────────────────
    private void setClickListeners() {

        // ── Edit Button ───────────────────────────────────────
        btnEdit.setOnClickListener(v -> {
            // Navigate to EditJobActivity, passing the job ID
            Intent intent = new Intent(JobDetailActivity.this, EditJobActivity.class);
            intent.putExtra(EXTRA_JOB_ID, jobId);
            startActivity(intent);
        });

        // ── Delete Button ─────────────────────────────────────
        btnDelete.setOnClickListener(v -> {
            // Show a confirmation dialog before deleting
            // This prevents accidental data loss
            showDeleteConfirmationDialog();
        });
    }

    // ─────────────────────────────────────────────────────────
    // showDeleteConfirmationDialog()
    // ─────────────────────────────────────────────────────────
    /**
     * AlertDialog is a popup with buttons for user confirmation.
     * Here we show a "Are you sure?" prompt before deleting.
     *
     * AlertDialog.Builder pattern:
     *   1. Create a Builder
     *   2. Set properties (title, message, buttons)
     *   3. Call .show() to display it
     */
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Application")
                .setMessage("Are you sure you want to delete this job application? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // User confirmed deletion
                    int rowsDeleted = dbHelper.deleteJob(jobId);

                    if (rowsDeleted > 0) {
                        Toast.makeText(this, "Application deleted", Toast.LENGTH_SHORT).show();
                        finish(); // Return to list screen
                    } else {
                        Toast.makeText(this, "Error deleting record", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null) // null = dismiss dialog, do nothing
                .show();
    }

    // ─────────────────────────────────────────────────────────
    // HELPER: safeText()
    // ─────────────────────────────────────────────────────────
    /**
     * Returns the value if non-null and non-empty, otherwise the fallback.
     * Prevents displaying "null" in TextViews.
     *
     * @param value     The string to check
     * @param fallback  What to show if value is null/empty
     */
    private String safeText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }
}