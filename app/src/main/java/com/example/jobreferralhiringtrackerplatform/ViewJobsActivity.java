package com.example.jobreferralhiringtrackerplatform;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * ============================================================
 * CLASS: ViewJobsActivity.java
 * ============================================================
 * PURPOSE:
 *   Displays all saved job applications in a scrollable list.
 *   Features: Search by text, Filter by status, Tap to view details.
 *
 * KEY COMPONENTS:
 *   RecyclerView → scrollable list of job cards
 *   JobAdapter   → our custom adapter feeding data to RecyclerView
 *   EditText     → real-time search field
 *   Spinner      → filter dropdown (All / Applied / Interview / etc.)
 *   LinearLayoutManager → arranges RecyclerView items in a vertical list
 *
 * DATA FLOW:
 *   Database → List<JobApplication> → JobAdapter → RecyclerView (UI)
 * ============================================================
 */
public class ViewJobsActivity extends AppCompatActivity {

    // ─────────────────────────────────────────────────────────
    // UI REFERENCES
    // ─────────────────────────────────────────────────────────
    private RecyclerView recyclerView;      // The scrollable list container
    private JobAdapter   jobAdapter;        // Our custom adapter
    private EditText     etSearch;          // Live search input
    private Spinner      spFilter;          // Status filter dropdown
    private TextView     tvEmpty;           // Shown when list is empty

    // ─────────────────────────────────────────────────────────
    // STATE
    // ─────────────────────────────────────────────────────────
    private DatabaseHelper dbHelper;
    private List<JobApplication> allJobs;   // Full unfiltered list (for resetting filters)

    // ─────────────────────────────────────────────────────────
    // onCreate()
    // ─────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jobs);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Applications");
        }

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupRecyclerView();
        setupFilterSpinner();
        setupSearchListener();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // ─────────────────────────────────────────────────────────
    // onResume() — Refresh list when returning from detail screen
    // ─────────────────────────────────────────────────────────
    @Override
    protected void onResume() {
        super.onResume();
        loadAllJobs(); // Reload in case a job was edited/deleted on detail screen
    }

    // ─────────────────────────────────────────────────────────
    // initViews()
    // ─────────────────────────────────────────────────────────
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        etSearch     = findViewById(R.id.etSearch);
        spFilter     = findViewById(R.id.spFilter);
        tvEmpty      = findViewById(R.id.tvEmpty);
    }

    // ─────────────────────────────────────────────────────────
    // setupRecyclerView()
    // ─────────────────────────────────────────────────────────
    /**
     * Connects the RecyclerView with:
     *   1. A LayoutManager (how to arrange items)
     *   2. Our custom JobAdapter (provides item views)
     *   3. A click listener (what happens when an item is tapped)
     *
     * LinearLayoutManager → vertical scrolling list (like a ListView)
     * Other options: GridLayoutManager (grid), StaggeredGridLayoutManager (Pinterest-style)
     */
    private void setupRecyclerView() {
        // Load initial data from database
        allJobs = dbHelper.getAllJobs();

        // Create adapter with context and data
        jobAdapter = new JobAdapter(this, allJobs);

        // LinearLayoutManager arranges items top-to-bottom
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Connect the adapter to the RecyclerView
        recyclerView.setAdapter(jobAdapter);

        // Set what happens when a job card is clicked
        jobAdapter.setOnJobClickListener(job -> {
            // Navigate to detail screen, passing the job's ID
            Intent intent = new Intent(ViewJobsActivity.this, JobDetailActivity.class);
            // putExtra: pass data between Activities (like function parameters)
            // "job_id" is the key; job.getId() is the value
            intent.putExtra("job_id", job.getId());
            startActivity(intent);
        });

        updateEmptyState(); // Show/hide empty message
    }

    // ─────────────────────────────────────────────────────────
    // setupFilterSpinner()
    // ─────────────────────────────────────────────────────────
    /**
     * The filter spinner lets users narrow the list by status.
     * "All" option shows everything; others filter by status string.
     *
     * OnItemSelectedListener fires whenever the selection changes.
     * We implement it inline using an anonymous class.
     */
    private void setupFilterSpinner() {
        String[] filterOptions = {"All", "Applied", "Interview", "Rejected", "Selected"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filterOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(adapter);

        // OnItemSelectedListener: fires when user picks a different option
        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = filterOptions[position]; // Which option was picked

                List<JobApplication> filteredList;

                if (selected.equals("All")) {
                    // Show everything
                    filteredList = dbHelper.getAllJobs();
                } else {
                    // Show only jobs with this status
                    filteredList = dbHelper.getJobsByStatus(selected);
                }

                jobAdapter.updateList(filteredList); // Update RecyclerView
                updateEmptyState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Required override — nothing to do here
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    // setupSearchListener()
    // ─────────────────────────────────────────────────────────
    /**
     * TextWatcher fires on every keystroke, allowing real-time search.
     *
     * We only need afterTextChanged() — the other two methods are
     * required by the interface but we leave them empty.
     *
     * HOW IT WORKS:
     *   User types → afterTextChanged() fires → we query DB → update list
     */
    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Called before text is changed — not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Called as text is changing — not needed (we act after)
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Called AFTER text finishes changing
                String query = s.toString().trim();

                List<JobApplication> results;

                if (query.isEmpty()) {
                    // Empty search → show all jobs (respecting current filter)
                    results = dbHelper.getAllJobs();
                } else {
                    // Search database for matching company or role
                    results = dbHelper.searchJobs(query);
                }

                jobAdapter.updateList(results);
                updateEmptyState();
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    // loadAllJobs() — Refresh full list from DB
    // ─────────────────────────────────────────────────────────
    private void loadAllJobs() {
        allJobs = dbHelper.getAllJobs();
        jobAdapter.updateList(allJobs);
        updateEmptyState();
    }

    // ─────────────────────────────────────────────────────────
    // updateEmptyState() — Show message when no results
    // ─────────────────────────────────────────────────────────
    /**
     * If the list is empty, we show a "No applications yet" TextView.
     * If the list has items, we hide it and show the RecyclerView.
     *
     * View.VISIBLE → the view is shown and takes up space
     * View.GONE    → the view is hidden AND takes up NO space (collapsed)
     */
    private void updateEmptyState() {
        if (jobAdapter.getItemCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}