package com.example.jobreferralhiringtrackerplatform;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * ============================================================
 * CLASS: HomeActivity.java
 * ============================================================
 * PURPOSE:
 *   This is the MAIN SCREEN (Dashboard) of the app. It's the first
 *   thing the user sees after launching. It shows:
 *     - A welcome message
 *     - Summary stats (Total apps, Interviews, Rejections, Selections)
 *     - Navigation buttons to all major features

 * EXTENDS: AppCompatActivity
 *   All screens in Android extend Activity or AppCompatActivity.
 *   AppCompatActivity gives us backward-compatible Material toolbar support.
 *
 * LIFECYCLE:
 *   onCreate()  → Screen is created; set up views
 *   onResume()  → Screen comes back into focus; refresh stats
 *   (We use onResume so stats update after returning from another screen)
 * ============================================================
 */
public class HomeActivity extends AppCompatActivity {

    // ─────────────────────────────────────────────────────────
    // UI REFERENCES
    // ─────────────────────────────────────────────────────────
    // These hold references to Views from activity_home.xml
    // Declared at class level so both onCreate() and onResume() can access them

    private TextView tvTotalApps;       // Shows total application count
    private TextView tvInterviewCount;  // Shows interview count
    private TextView tvRejectedCount;   // Shows rejection count
    private TextView tvSelectedCount;   // Shows selected/offer count

    private Button btnAddJob;           // Navigates to AddJobActivity
    private Button btnViewJobs;         // Navigates to ViewJobsActivity
    private Button btnProfile;          // Navigates to ProfileActivity
    private Button btnReferrals;        // Navigates to ReferralActivity
    private Button btnAnalytics;        // Navigates to AnalyticsActivity

    // ─────────────────────────────────────────────────────────
    // DATABASE REFERENCE
    // ─────────────────────────────────────────────────────────
    private DatabaseHelper dbHelper;    // Our database manager class

    // ─────────────────────────────────────────────────────────
    // onCreate() — Called when the Activity is first created
    // ─────────────────────────────────────────────────────────
    /**
     * Think of onCreate() as the "constructor" for your Activity.
     * It runs once when the screen is first shown.
     *
     * @param savedInstanceState  Bundle of saved state (for screen rotation etc.)
     *                            null on first launch
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call super first!

        // setContentView: tells Android WHICH XML layout file to use for this screen
        // R.layout.activity_home refers to res/layout/activity_home.xml
        setContentView(R.layout.activity_home);

        // Initialize our database helper with the app context
        // 'this' refers to this Activity, which IS a Context
        dbHelper = new DatabaseHelper(this);

        // Connect Java variables to XML Views using their IDs
        initViews();

        // Attach click listeners to all buttons
        setClickListeners();
    }

    // ─────────────────────────────────────────────────────────
    // onResume() — Called every time this screen becomes visible
    // ─────────────────────────────────────────────────────────
    /**
     * onResume() is called:
     *   - Right after onCreate() on first launch
     *   - When user returns from another screen (back button, finish())
     *
     * We refresh stats here so the numbers update when you return
     * from adding a new job application.
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboardStats(); // Always show fresh counts
    }

    // ─────────────────────────────────────────────────────────
    // HELPER: initViews()
    // ─────────────────────────────────────────────────────────
    /**
     * findViewById() searches the inflated layout for a View with the given ID.
     * The ID must match the android:id attribute in the XML file.
     *
     * We cast the result to the correct type (TextView, Button, etc.)
     * because findViewById() returns the generic View type.
     */
    private void initViews() {
        tvTotalApps      = findViewById(R.id.tvTotalApps);
        tvInterviewCount = findViewById(R.id.tvInterviewCount);
        tvRejectedCount  = findViewById(R.id.tvRejectedCount);
        tvSelectedCount  = findViewById(R.id.tvSelectedCount);

        btnAddJob    = findViewById(R.id.btnAddJob);
        btnViewJobs  = findViewById(R.id.btnViewJobs);
        btnProfile   = findViewById(R.id.btnProfile);
        btnReferrals = findViewById(R.id.btnReferrals);
        btnAnalytics = findViewById(R.id.btnAnalytics);
    }

    // ─────────────────────────────────────────────────────────
    // HELPER: setClickListeners()
    // ─────────────────────────────────────────────────────────
    /**
     * An OnClickListener is a callback that fires when the user taps a button.
     *
     * LAMBDA SYNTAX: view -> { ... }
     *   This is Java 8+ shorthand for:
     *     new View.OnClickListener() {
     *         @Override public void onClick(View view) { ... }
     *     }
     *
     * INTENT NAVIGATION:
     *   In Android, you navigate between screens using Intent.
     *   Intent(context, TargetActivity.class) specifies WHERE to go.
     *   startActivity(intent) actually starts the navigation.
     */
    private void setClickListeners() {

        // ── Add Job Button ────────────────────────────────────
        btnAddJob.setOnClickListener(view -> {
            // Create an Intent pointing to AddJobActivity
            Intent intent = new Intent(HomeActivity.this, AddJobActivity.class);
            startActivity(intent); // Launch the screen
        });

        // ── View Applications Button ──────────────────────────
        btnViewJobs.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ViewJobsActivity.class);
            startActivity(intent);
        });

        // ── Profile Button ────────────────────────────────────
        btnProfile.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // ── Referrals Button ──────────────────────────────────
        btnReferrals.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ReferralActivity.class);
            startActivity(intent);
        });

        // ── Analytics Button ──────────────────────────────────
        btnAnalytics.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });
    }

    // ─────────────────────────────────────────────────────────
    // HELPER: refreshDashboardStats()
    // ─────────────────────────────────────────────────────────
    /**
     * Queries the database for fresh counts and updates the dashboard cards.
     *
     * String.valueOf(int) → converts an int like 5 to the String "5"
     * tvTotalApps.setText("5") → displays "5" in that TextView
     */
    private void refreshDashboardStats() {
        int total     = dbHelper.getTotalJobCount();
        int interview = dbHelper.getCountByStatus("Interview");
        int rejected  = dbHelper.getCountByStatus("Rejected");
        int selected  = dbHelper.getCountByStatus("Selected");

        tvTotalApps.setText(String.valueOf(total));
        tvInterviewCount.setText(String.valueOf(interview));
        tvRejectedCount.setText(String.valueOf(rejected));
        tvSelectedCount.setText(String.valueOf(selected));
    }
}