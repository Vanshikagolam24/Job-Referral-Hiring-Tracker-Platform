package com.example.jobreferralhiringtrackerplatform;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    // ─────────────────────────────────────────────────────────
    // INTERFACE: Click Callback
    // ─────────────────────────────────────────────────────────
    /**
     * Any class that wants to know when a job card is clicked
     * must implement this interface.
     *
     * USAGE in ViewJobsActivity:
     *   adapter.setOnJobClickListener(job -> {
     *       // Navigate to detail screen with job.getId()
     *   });
     */
    public interface OnJobClickListener {
        void onJobClick(JobApplication job);
    }

    // ─────────────────────────────────────────────────────────
    // FIELDS
    // ─────────────────────────────────────────────────────────

    /** The app context — needed for inflating layouts */
    private final Context context;

    /** The data list this adapter displays */
    private List<JobApplication> jobList;

    /** The click callback (set by the Activity) */
    private OnJobClickListener clickListener;

    // ─────────────────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────────────────
    /**
     * @param context  Android context (pass 'this' from an Activity)
     * @param jobList  The list of jobs to display
     */
    public JobAdapter(Context context, List<JobApplication> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    /** Setter so Activity can register the click listener */
    public void setOnJobClickListener(OnJobClickListener listener) {
        this.clickListener = listener;
    }

    // ─────────────────────────────────────────────────────────
    // METHOD: onCreateViewHolder
    // ─────────────────────────────────────────────────────────
    /**
     * Called when RecyclerView needs a NEW ViewHolder.
     * This happens a limited number of times (just enough to fill the screen).
     *
     * We INFLATE the item layout (item_job.xml) and wrap it in a ViewHolder.
     *
     * INFLATE means: convert the XML layout file into actual View objects in memory.
     *
     * @param parent    The RecyclerView itself (used as the parent for layout params)
     * @param viewType  Useful if you have multiple item types (we don't here)
     * @return          A new JobViewHolder wrapping the inflated view
     */
    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater converts XML → View
        // inflate(layoutResourceId, parentView, attachToParent)
        // attachToParent = false means we attach manually (RecyclerView handles this)
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_job, parent, false);

        return new JobViewHolder(itemView);
    }

    // ─────────────────────────────────────────────────────────
    // METHOD: onBindViewHolder
    // ─────────────────────────────────────────────────────────
    /**
     * Called every time a ViewHolder is RECYCLED to show a new item.
     * This is where we set the actual data into the Views.
     *
     * @param holder    The recycled ViewHolder (its Views are ready to be filled)
     * @param position  The index of the item in jobList to display
     */
    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        // Get the job at this position in the list
        JobApplication job = jobList.get(position);

        // Set text content into the TextViews
        holder.tvCompany.setText(job.getCompany());
        holder.tvRole.setText(job.getRole());
        holder.tvDate.setText("Applied: " + job.getDate());

        // ── Status Badge: Color-coded text ───────────────────
        String status = job.getStatus();
        holder.tvStatus.setText(status);

        // Change background color based on status for visual clarity
        // These colors match our status_bg.xml drawable definitions
        switch (status) {
            case "Interview":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
                break;
            case "Selected":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
                break;
            case "Rejected":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#F44336")); // Red
                break;
            default: // "Applied"
                holder.tvStatus.setBackgroundColor(Color.parseColor("#2196F3")); // Blue
                break;
        }

        // ── Recruiter (show only if not empty) ───────────────
        if (job.getRecruiter() != null && !job.getRecruiter().isEmpty()) {
            holder.tvRecruiter.setVisibility(View.VISIBLE);
            holder.tvRecruiter.setText("Recruiter: " + job.getRecruiter());
        } else {
            holder.tvRecruiter.setVisibility(View.GONE); // Hide if no recruiter
        }

        // ── Click Listener ───────────────────────────────────
        // When the card is tapped, invoke the callback (if set)
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onJobClick(job);
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    // METHOD: getItemCount
    // ─────────────────────────────────────────────────────────
    /**
     * RecyclerView calls this to know how many items exist.
     * It won't ask for position 5 if count is 3, for example.
     */
    @Override
    public int getItemCount() {
        return jobList.size();
    }

    // ─────────────────────────────────────────────────────────
    // METHOD: updateList — Refresh the displayed data
    // ─────────────────────────────────────────────────────────
    /**
     * Replaces the current job list with a new one and tells
     * RecyclerView to redraw everything.
     *
     * WHEN TO CALL THIS:
     *   - After a search query returns filtered results
     *   - After adding/deleting a job
     *   - After filtering by status
     *
     * @param newList  The updated list of jobs to display
     */
    public void updateList(List<JobApplication> newList) {
        this.jobList = newList;
        // notifyDataSetChanged() tells RecyclerView: "the data changed, redraw all items"
        // For production, use DiffUtil for better performance on large lists
        notifyDataSetChanged();
    }

    // ═══════════════════════════════════════════════════════════
    // INNER CLASS: JobViewHolder
    // ═══════════════════════════════════════════════════════════
    /**
     * A ViewHolder caches references to the Views inside one item card.
     *
     * WHY CACHE?
     *   Without caching, RecyclerView would call findViewById() on every
     *   bind — which is slow because it walks the View tree each time.
     *   ViewHolder stores these references so we look them up just ONCE.
     *
     * EXTENDS: RecyclerView.ViewHolder
     *   Requires us to pass the root view to super().
     */
    static class JobViewHolder extends RecyclerView.ViewHolder {

        // References to the Views inside item_job.xml
        // These are filled in the constructor and reused in onBindViewHolder()
        TextView tvCompany;    // Company name (large text)
        TextView tvRole;       // Job role/title
        TextView tvStatus;     // Status badge (color-coded)
        TextView tvDate;       // Application date
        TextView tvRecruiter;  // Recruiter name (optional)

        /**
         * Constructor: finds and stores references to all Views in the item layout.
         *
         * @param itemView  The inflated item_job.xml root view
         */
        JobViewHolder(@NonNull View itemView) {
            super(itemView); // Must call super() with the root view

            // findViewById looks up Views by their XML id
            // The R class is auto-generated by Android Studio from your XML
            tvCompany   = itemView.findViewById(R.id.tvCompany);
            tvRole      = itemView.findViewById(R.id.tvRole);
            tvStatus    = itemView.findViewById(R.id.tvStatus);
            tvDate      = itemView.findViewById(R.id.tvDate);
            tvRecruiter = itemView.findViewById(R.id.tvRecruiter);
        }
    }
}