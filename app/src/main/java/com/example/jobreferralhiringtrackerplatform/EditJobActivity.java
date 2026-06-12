package com.example.jobreferralhiringtrackerplatform;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

/**
 * ============================================================
 * CLASS: EditJobActivity.java
 * ============================================================
 * PURPOSE:
 *   Allows the user to edit all fields of an existing job application.
 *   Pre-populates all form fields with current data, then saves updates.
 *
 * KEY DIFFERENCE FROM AddJobActivity:
 *   - Receives an existing job ID
 *   - Pre-fills all input fields with current values
 *   - Calls dbHelper.updateJob() instead of insertJob()
 *
 * SPINNER PRE-SELECTION:
 *   To show the current status as selected in the Spinner, we must
 *   find the position of that string in the options array and call
 *   spinner.setSelection(position).
 * ============================================================
 */
public class EditJobActivity extends AppCompatActivity {

    // ─────────────────────────────────────────────────────────
    // UI REFERENCES
    // ─────────────────────────────────────────────────────────
    private EditText etCompany;
    private EditText etRole;
    private EditText etRecruiter;
    private EditText etDate;
    private EditText etNotes;
    private EditText etReferralPerson;
    private Spinner  spStatus;
    private Button   btnUpdate;
    private Button   btnCancel;

    // ─────────────────────────────────────────────────────────
    // STATE
    // ─────────────────────────────────────────────────────────
    private DatabaseHelper dbHelper;
    private JobApplication currentJob;
    private int jobId;

    // The status options — same as AddJobActivity
    private final String[] STATUS_OPTIONS = {"Applied", "Interview", "Rejected", "Selected"};

    // ─────────────────────────────────────────────────────────
    // onCreate()
    // ─────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job); // Reuses same layout as AddJob (DRY principle)

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Application");
        }

        dbHelper = new DatabaseHelper(this);

        // Retrieve the job ID passed from JobDetailActivity
        jobId = getIntent().getIntExtra(JobDetailActivity.EXTRA_JOB_ID, -1);
        if (jobId == -1) {
            Toast.makeText(this, "Error: Invalid job ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupStatusSpinner();
        setupDatePicker();

        // Load existing data into the form
        loadJobData();

        setClickListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // ─────────────────────────────────────────────────────────
    // initViews()
    // ─────────────────────────────────────────────────────────
    private void initViews() {
        etCompany        = findViewById(R.id.etCompany);
        etRole           = findViewById(R.id.etRole);
        etRecruiter      = findViewById(R.id.etRecruiter);
        etDate           = findViewById(R.id.etDate);
        etNotes          = findViewById(R.id.etNotes);
        etReferralPerson = findViewById(R.id.etReferralPerson);
        spStatus         = findViewById(R.id.spStatus);
        btnSave          = findViewById(R.id.btnSave);
        btnCancel        = findViewById(R.id.btnCancel);

        // Change the save button label to "Update" for clarity
        btnSave.setText("Update");
    }

    // Field reference for the save/update button (needs to be accessible in initViews)
    private Button btnSave;

    // ─────────────────────────────────────────────────────────
    // setupStatusSpinner()
    // ─────────────────────────────────────────────────────────
    private void setupStatusSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                STATUS_OPTIONS
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(adapter);
    }

    // ─────────────────────────────────────────────────────────
    // loadJobData() — Pre-fill form with existing values
    // ─────────────────────────────────────────────────────────
    /**
     * Fetches the current job from DB and fills every input field
     * so the user sees what they're editing.
     *
     * SPINNER PRE-SELECTION:
     *   We loop through STATUS_OPTIONS to find the index of the
     *   current status, then call spStatus.setSelection(index).
     */
    private void loadJobData() {
        currentJob = dbHelper.getJobById(jobId);

        if (currentJob == null) {
            Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fill text fields
        etCompany.setText(currentJob.getCompany());
        etRole.setText(currentJob.getRole());
        etRecruiter.setText(currentJob.getRecruiter());
        etDate.setText(currentJob.getDate());
        etNotes.setText(currentJob.getNotes());
        etReferralPerson.setText(currentJob.getReferralPerson());

        // Pre-select the correct status in the Spinner
        String currentStatus = currentJob.getStatus();
        for (int i = 0; i < STATUS_OPTIONS.length; i++) {
            if (STATUS_OPTIONS[i].equals(currentStatus)) {
                spStatus.setSelection(i); // Set the dropdown to show current status
                break;
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // setupDatePicker()
    // ─────────────────────────────────────────────────────────
    private void setupDatePicker() {
        etDate.setFocusable(false);
        etDate.setClickable(true);

        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year  = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day   = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(
                    EditJobActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = selectedYear + "-"
                                + String.format("%02d", selectedMonth + 1) + "-"
                                + String.format("%02d", selectedDay);
                        etDate.setText(formattedDate);
                    },
                    year, month, day
            ).show();
        });
    }

    // ─────────────────────────────────────────────────────────
    // setClickListeners()
    // ─────────────────────────────────────────────────────────
    private void setClickListeners() {
        btnSave.setOnClickListener(v -> updateJobApplication());
        btnCancel.setOnClickListener(v -> finish());
    }

    // ─────────────────────────────────────────────────────────
    // updateJobApplication()
    // ─────────────────────────────────────────────────────────
    /**
     * Reads updated values, validates, then calls dbHelper.updateJob().
     * Very similar to AddJobActivity.saveJobApplication() but uses UPDATE.
     */
    private void updateJobApplication() {
        String company        = etCompany.getText().toString().trim();
        String role           = etRole.getText().toString().trim();
        String recruiter      = etRecruiter.getText().toString().trim();
        String date           = etDate.getText().toString().trim();
        String notes          = etNotes.getText().toString().trim();
        String referralPerson = etReferralPerson.getText().toString().trim();
        String status         = spStatus.getSelectedItem().toString();

        // Validate required fields
        if (TextUtils.isEmpty(company)) {
            etCompany.setError("Company name is required");
            return;
        }
        if (TextUtils.isEmpty(role)) {
            etRole.setError("Job role is required");
            return;
        }
        if (TextUtils.isEmpty(date)) {
            etDate.setError("Date is required");
            return;
        }

        // Build updated JobApplication object WITH the existing ID
        JobApplication updatedJob = new JobApplication(
                jobId, company, role, recruiter, date, status, notes, referralPerson
        );

        int result = dbHelper.updateJob(updatedJob);

        if (result > 0) {
            Toast.makeText(this, "Application updated!", Toast.LENGTH_SHORT).show();
            finish(); // Return to detail screen (which will reload data in onResume)
        } else {
            Toast.makeText(this, "Update failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}