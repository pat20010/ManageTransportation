package com.project_develop_team.managetransportation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project_develop_team.managetransportation.models.Tasks;

import butterknife.BindView;

public class SaveListActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    @BindView(R.id.nameTextView)
    TextView nameTextView;
    @BindView(R.id.taskNameTextView)
    TextView taskNameTextView;
    @BindView(R.id.taskAddressTextView)
    TextView taskAddressTextView;
    @BindView(R.id.taskPhoneTextView)
    TextView taskPhoneTextView;

    private ValueEventListener mEventListener;

    String tasksKey;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_list);

        tasksKey = getIntent().getStringExtra("tasks-key");
        if (tasksKey == null) {
            throw new IllegalArgumentException("Must pass tasks-key");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("tasks").child(tasksKey);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tasks tasks = dataSnapshot.getValue(Tasks.class);
                nameTextView.setText(tasks.name);
                taskNameTextView.setText(tasks.taskName);
                taskAddressTextView.setText(tasks.taskAddress);
                taskPhoneTextView.setText(tasks.taskPhone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Fail load task", Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.addValueEventListener(eventListener);

        mEventListener = eventListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mEventListener != null) {
            databaseReference.removeEventListener(mEventListener);
        }
    }
}
