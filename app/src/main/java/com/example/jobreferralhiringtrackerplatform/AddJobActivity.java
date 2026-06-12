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
 * CLASS: AddJobActivity.java
 * ============================================================
 * PURPOSE:
 *   This screen presents a form for the user to add a new job
 *   application. On "Save", it validates the inputs and inserts
 *   the record into SQLite via DatabaseHelper.
 *
 * KEY CONCEPTS:
 *   EditText   → text input field
 *   Spinner    → dropdown selection widget
 *   DatePickerDialog → system calendar popup for picking dates
 *   Toast      → small pop-up notification at screen bottom
 *   TextUtils.isEmpty() → checks if a String is null or ""
 *
 * INPUT VALIDATION:
 *   We check required fields before saving. If invalid, we show
 *   an error on the field itself using setError().
 * ============================================================
 */
public class AddJobActivity extends AppCompatActivity {

    // ─────────────────────────────────────────────────────────
    // UI REFERENCES
    // ─────────────────────────────────────────────────────────
    private EditText etCompany;        // Company name input
    private EditText etRole;           // Job role input
    private EditText etRecruiter;      // Recruiter name input
    private EditText etDate;           // Application date (tap to open date picker)
    private EditText etNotes;          // Optional notes
    private EditText etReferralPerson; // Referral person name
    private Spinner  spStatus;         // Status dropdown
    private Button   btnSave;          // Save button
    private Button   btnCancel;        // Cancel / go back button

    // ─────────────────────────────────────────────────────────
    // STATE
    // ─────────────────────────────────────────────────────────
    private DatabaseHelper dbHelper;

    // ─────────────────────────────────────────────────────────
    // onCreate()
    // ─────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        // Add a back arrow to the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Job Application");
        }

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupStatusSpinner();
        setupDatePicker();
        setClickListeners();
    }

    // ─────────────────────────────────────────────────────────
    // Handle toolbar back arrow
    // ─────────────────────────────────────────────────────────
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Close this screen and return to previous
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
    }

    // ─────────────────────────────────────────────────────────
    // setupStatusSpinner()
    // ─────────────────────────────────────────────────────────
    /**
     * A Spinner is Android's dropdown selector.
     * We populate it with an ArrayAdapter wrapping a String array.
     *
     * ArrayAdapter<String>:
     *   - context: needed to access resources
     *   - android.R.layout.simple_spinner_item: built-in Android layout for each item
     *   - statusOptions: our data array
     *
     * setDropDownViewResource: sets the layout used for the expanded dropdown list
     */
    private void setupStatusSpinner() {
        String[] statusOptions = {"Applied", "Interview", "Rejected", "Selected"};

        // ArrayAdapter wraps data + layout into a format Spinner understands
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item, // Layout for selected item display
                statusOptions
        );

        // simple_spinner_dropdown_item: the layout for items in the dropdown list
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spStatus.setAdapter(adapter);
    }

    // ─────────────────────────────────────────────────────────
    // setupDatePicker()
    // ─────────────────────────────────────────────────────────
    /**
     * Instead of typing a date, the user taps the date field
     * and a calendar dialog appears. We pre-populate it with today's date.
     *
     * Calendar.getInstance() → gets the current date/time
     * DatePickerDialog → shows a system calendar UI
     * The callback (OnDateSetListener) fires when user confirms a date.
     */
    private void setupDatePicker() {
        // Make the field non-editable directly (force use of date picker)
        etDate.setFocusable(false);
        etDate.setClickable(true);

        etDate.setOnClickListener(v -> {
            // Get today's date to pre-select in the calendar
            Calendar calendar = Calendar.getInstance();
            int year  = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);  // 0-indexed (Jan=0)
            int day   = calendar.get(Calendar.DAY_OF_MONTH);

            // DatePickerDialog: context, listener, year, month, day
            DatePickerDialog datePicker = new DatePickerDialog(
                    AddJobActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format: YYYY-MM-DD (e.g., "2024-06-01")
                        // selectedMonth+1 because months are 0-indexed
                        String formattedDate = selectedYear + "-"
                                + String.format("%02d", selectedMonth + 1) + "-"  // %02d pads with leading zero
                                + String.format("%02d", selectedDay);
                        etDate.setText(formattedDate);
                    },
                    year, month, day
            );

            datePicker.show(); // Display the calendar popup
        });
    }

    // ─────────────────────────────────────────────────────────
    // setClickListeners()
    // ─────────────────────────────────────────────────────────
    private void setClickListeners() {
        btnSave.setOnClickListener(v -> saveJobApplication());
        btnCancel.setOnClickListener(v -> finish()); // finish() = go back
    }

    // ─────────────────────────────────────────────────────────
    // saveJobApplication() — Validate and Save
    // ─────────────────────────────────────────────────────────
    /**
     * 1. Read values from all input fields
     * 2. Validate required fields
     * 3. Build a JobApplication object
     * 4. Insert into database
     * 5. Show success/failure message
     * 6. Close screen on success
     */
    private void saveJobApplication() {

        // ── Step 1: Read field values ─────────────────────────
        // .getText() returns an Editable; .toString() converts to String
        // .trim() removes leading/trailing whitespace
        String company        = etCompany.getText().toString().trim();
        String role           = etRole.getText().toString().trim();
        String recruiter      = etRecruiter.getText().toString().trim();
        String date           = etDate.getText().toString().trim();
        String notes          = etNotes.getText().toString().trim();
        String referralPerson = etReferralPerson.getText().toString().trim();

        // spStatus.getSelectedItem() returns the currently chosen Spinner item as Object
        // We cast it to String since our spinner contains Strings
        String status = spStatus.getSelectedItem().toString();

        // ── Step 2: Validate required fields ─────────────────
        boolean valid = true;

        if (TextUtils.isEmpty(company)) {
            etCompany.setError("Company name is required"); // Shows red error under field
            valid = false;
        }

        if (TextUtils.isEmpty(role)) {
            etRole.setError("Job role is required");
            valid = false;
        }

        if (TextUtils.isEmpty(date)) {
            etDate.setError("Application date is required");
            valid = false;
        }

        // If validation failed, stop here — don't save
        if (!valid) return;

        // ── Step 3: Build the JobApplication object ───────────
        // Use the constructor WITHOUT id (SQLite will assign the id)
        JobApplication job = new JobApplication(
                company, role, recruiter, date, status, notes, referralPerson
        );

        // ── Step 4: Insert into database ──────────────────────
        long result = dbHelper.insertJob(job);

        // ── Step 5: Feedback & navigation ─────────────────────
        if (result != -1) {
            // result is the new row ID (positive number) → success
            Toast.makeText(this, "Job application saved!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to the previous screen (HomeActivity)
        } else {
            // result == -1 → SQLite insert failed
            Toast.makeText(this, "Error saving. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}