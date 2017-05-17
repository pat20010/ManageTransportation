package com.project_develop_team.managetransportation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.project_develop_team.managetransportation.models.Tasks;
import com.project_develop_team.managetransportation.viewholder.TasksViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskTypeCompleteFragment extends Fragment {

    @BindView(R.id.dates_task_text_view)
    TextView datesTaskTextView;

    FirebaseRecyclerAdapter<Tasks, TasksViewHolder> recyclerAdapter;

    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;

    private String currentDate;

    private String transportSuccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_type_complete, container, false);
        ButterKnife.bind(this, view);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = (RecyclerView) view.findViewById(R.id.tasks_list);
        recyclerView.setHasFixedSize(true);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
        datesTaskTextView.setText(simpleDateFormat.format(new Date()));

        SimpleDateFormat currentDateFormat = new SimpleDateFormat(getString(R.string.date_tasks_format), Locale.getDefault());
        currentDate = currentDateFormat.format(new Date());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);

        transportSuccess = getString(R.string.transport_success);

        Query tasksQuery = getQuery(databaseReference);
        recyclerAdapter = new FirebaseRecyclerAdapter<Tasks, TasksViewHolder>(Tasks.class, R.layout.tasks_layout,
                TasksViewHolder.class, tasksQuery) {
            @Override
            protected void populateViewHolder(TasksViewHolder viewHolder, Tasks model, int position) {

                if (model.task_date.equals(currentDate) && model.status.equals(transportSuccess)) {
                    viewHolder.bindToTaskComplete(model, getActivity());
                } else {
                    viewHolder.cardView.setVisibility(View.GONE);
                    viewHolder.relativeLayout.setVisibility(View.GONE);
                    viewHolder.linearLayout.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recyclerAdapter != null) {
            recyclerAdapter.cleanup();
        }
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child(getString(R.string.firebase_tasks)).orderByChild("uid").equalTo(getUid());
    }
}
