package com.example.jobreferralhiringtrackerplatform;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * ============================================================
 * CLASS: ProfileActivity.java
 * ============================================================
 * PURPOSE:
 *   Lets the user store personal profile information:
 *   Name, Email, Phone, Skills, Resume summary:
 * STORAGE USED: SharedPreferences
 *   Unlike job applications (which need complex querying and
 *   are stored in SQLite), profile data is simple key-value pairs
 *   that rarely change. SharedPreferences is perfect for this.
 *
 * WHAT IS SharedPreferences?
 *   An XML file stored in the app's private storage that maps
 *   String keys to primitive values (String, int, boolean, etc.)
 *
 *   WRITE:
 *     SharedPreferences.Editor editor = prefs.edit();
 *     editor.putString("name", "John Doe");
 *     editor.apply(); // Save asynchronously (recommended)
 *
 *   READ:
 *     String name = prefs.getString("name", ""); // "" = default
 *
 *   BENEFIT: No SQL, no cursor, no adapter — just key-value.
 *   LIMIT: Not suitable for lists or relational data.
 * ============================================================
 */
public class ProfileActivity extends AppCompatActivity {

    // ─────────────────────────────────────────────────────────
    // SharedPreferences CONSTANTS
    // ─────────────────────────────────────────────────────────
    /** The name of the SharedPreferences file (like a filename) */
    private static final String PREFS_NAME = "UserProfile";

    // Keys for each stored field
    private static final String KEY_NAME   = "name";
    private static final String KEY_EMAIL  = "email";
    private static final String KEY_PHONE  = "phone";
    private static final String KEY_SKILLS = "skills";
    private static final String KEY_RESUME = "resume";

    // ─────────────────────────────────────────────────────────
    // UI REFERENCES
    // ─────────────────────────────────────────────────────────
    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etSkills;
    private EditText etResume;
    private Button   btnSaveProfile;

    // ─────────────────────────────────────────────────────────
    // STATE
    // ─────────────────────────────────────────────────────────
    private SharedPreferences sharedPreferences;

    // ─────────────────────────────────────────────────────────
    // onCreate()
    // ─────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        // Initialize SharedPreferences
        // MODE_PRIVATE: only this app can read/write this file
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        loadProfileData(); // Pre-fill fields with saved data

        btnSaveProfile.setOnClickListener(v -> saveProfileData());
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
        etName         = findViewById(R.id.etName);
        etEmail        = findViewById(R.id.etEmail);
        etPhone        = findViewById(R.id.etPhone);
        etSkills       = findViewById(R.id.etSkills);
        etResume       = findViewById(R.id.etResume);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
    }

    // ─────────────────────────────────────────────────────────
    // loadProfileData() — Read from SharedPreferences
    // ─────────────────────────────────────────────────────────
    /**
     * Reads previously saved profile values and displays them in the EditText fields.
     * The second parameter to getString() is the default value (shown if key not found).
     */
    private void loadProfileData() {
        etName.setText(sharedPreferences.getString(KEY_NAME, ""));
        etEmail.setText(sharedPreferences.getString(KEY_EMAIL, ""));
        etPhone.setText(sharedPreferences.getString(KEY_PHONE, ""));
        etSkills.setText(sharedPreferences.getString(KEY_SKILLS, ""));
        etResume.setText(sharedPreferences.getString(KEY_RESUME, ""));
    }

    // ─────────────────────────────────────────────────────────
    // saveProfileData() — Write to SharedPreferences
    // ─────────────────────────────────────────────────────────
    /**
     * Reads values from the form and persists them to SharedPreferences.
     *
     * editor.apply() → saves asynchronously (non-blocking, recommended)
     * editor.commit() → saves synchronously (blocks the UI thread, use sparingly)
     */
    private void saveProfileData() {
        String name   = etName.getText().toString().trim();
        String email  = etEmail.getText().toString().trim();
        String phone  = etPhone.getText().toString().trim();
        String skills = etSkills.getText().toString().trim();
        String resume = etResume.getText().toString().trim();

        // Simple validation: name should not be empty
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }

        // Get an Editor object to make changes
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME,   name);
        editor.putString(KEY_EMAIL,  email);
        editor.putString(KEY_PHONE,  phone);
        editor.putString(KEY_SKILLS, skills);
        editor.putString(KEY_RESUME, resume);

        editor.apply(); // Commit the changes

        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
    }
}