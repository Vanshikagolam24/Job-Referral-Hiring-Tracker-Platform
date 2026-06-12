package com.example.jobreferralhiringtrackerplatform;


import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * ============================================================
 * CLASS: AnalyticsActivity.java
 * ============================================================
 * PURPOSE:
 *   Displays a comprehensive analytics dashboard showing:
 *     - Total applications, interviews, rejections, selections
 *     - Success rate (Selected / Total × 100)
 *     - Interview conversion rate (Interviews / Total × 100)
 *     - Visual progress bar for success rate
 *
 * CALCULATIONS:
 *   We query the database for counts by status, then compute
 *   percentage metrics using simple arithmetic.
 *
 *   SUCCESS RATE = (Selected / Total) × 100
 *   INTERVIEW RATE = (Interview / Total) × 100
 *
 * NOTE ON DIVISION:
 *   Integer division (5 / 10) = 0 in Java (truncates decimal).
 *   Float division (5f / 10) = 0.5 (keeps decimal).
 *   We cast to float first to get correct percentages.
 * ============================================================
 */
public class AnalyticsActivity extends AppCompatActivity {

    // ─────────────────────────────────────────────────────────
    // UI REFERENCES
    // ─────────────────────────────────────────────────────────
    private TextView tvAnalyticsTotal;
    private TextView tvAnalyticsApplied;
    private TextView tvAnalyticsInterview;
    private TextView tvAnalyticsRejected;
    private TextView tvAnalyticsSelected;
    private TextView tvSuccessRate;
    private TextView tvInterviewRate;
    private TextView tvTotalInterviews;

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
        setContentView(R.layout.activity_analytics);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Analytics Dashboard");
        }

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadAnalytics();
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
        tvAnalyticsTotal     = findViewById(R.id.tvAnalyticsTotal);
        tvAnalyticsApplied   = findViewById(R.id.tvAnalyticsApplied);
        tvAnalyticsInterview = findViewById(R.id.tvAnalyticsInterview);
        tvAnalyticsRejected  = findViewById(R.id.tvAnalyticsRejected);
        tvAnalyticsSelected  = findViewById(R.id.tvAnalyticsSelected);
        tvSuccessRate        = findViewById(R.id.tvSuccessRate);
        tvInterviewRate      = findViewById(R.id.tvInterviewRate);
        tvTotalInterviews    = findViewById(R.id.tvTotalInterviews);
    }

    // ─────────────────────────────────────────────────────────
    // loadAnalytics()
    // ─────────────────────────────────────────────────────────
    /**
     * Fetches all counts from DB and displays them.
     * Computes percentage metrics with safe division (avoids divide-by-zero).
     *
     * String.format("%.1f%%", value) → formats float with 1 decimal place + % sign
     *   Example: String.format("%.1f%%", 33.333f) → "33.3%"
     *   The %% is an escaped % sign (single % would confuse the formatter)
     */
    private void loadAnalytics() {
        // Fetch raw counts from database
        int total     = dbHelper.getTotalJobCount();
        int applied   = dbHelper.getCountByStatus("Applied");
        int interview = dbHelper.getCountByStatus("Interview");
        int rejected  = dbHelper.getCountByStatus("Rejected");
        int selected  = dbHelper.getCountByStatus("Selected");
        int totalInterviews = dbHelper.getTotalInterviewCount();

        // Display raw counts
        tvAnalyticsTotal.setText(String.valueOf(total));
        tvAnalyticsApplied.setText(String.valueOf(applied));
        tvAnalyticsInterview.setText(String.valueOf(interview));
        tvAnalyticsRejected.setText(String.valueOf(rejected));
        tvAnalyticsSelected.setText(String.valueOf(selected));
        tvTotalInterviews.setText(String.valueOf(totalInterviews));

        // Compute and display rates (guard against division by zero)
        if (total > 0) {
            // Cast 'selected' to float BEFORE dividing to avoid integer truncation
            float successRate   = ((float) selected  / total) * 100f;
            float interviewRate = ((float) interview / total) * 100f;

            // "%.1f%%" → format as float with 1 decimal, followed by literal %
            tvSuccessRate.setText(String.format("%.1f%%", successRate));
            tvInterviewRate.setText(String.format("%.1f%%", interviewRate));
        } else {
            // No data yet — show placeholder
            tvSuccessRate.setText("N/A");
            tvInterviewRate.setText("N/A");
        }
    }
}