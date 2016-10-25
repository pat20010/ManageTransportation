package com.project_develop_team.managetransportation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project_develop_team.managetransportation.models.Tasks;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SaveListActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    public static final String EXTRA_TASKS_KEY = "tasks-key";

    @BindView(R.id.nameTextView)
    TextView nameTextView;
    @BindView(R.id.taskNameTextView)
    TextView taskNameTextView;
    @BindView(R.id.taskAddressTextView)
    TextView taskAddressTextView;
    @BindView(R.id.taskPhoneTextView)
    TextView taskPhoneTextView;

    private ValueEventListener eventListener;

    String tasksKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_list);
        ButterKnife.bind(this);

        tasksKey = getIntent().getStringExtra(EXTRA_TASKS_KEY);
        if (tasksKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_TASKS_KEY");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tasks tasks = dataSnapshot.getValue(Tasks.class);
                nameTextView.setText(tasks.name);
                taskNameTextView.setText(tasks.taskName);
                taskAddressTextView.setText(tasks.taskAddress);
                taskPhoneTextView.setText("โทร" + " " + tasks.taskPhone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Fail load task", Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.child("tasks").child(tasksKey).addValueEventListener(eventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }

    @OnClick(R.id.saveListButton)
    public void saveList() {
        databaseReference.child("tasks").child(tasksKey).child("status").setValue("เสร็จเรียบร้อย");
        databaseReference.child("users-tasks").child(getUid()).child(tasksKey).removeValue();
        finish();
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
