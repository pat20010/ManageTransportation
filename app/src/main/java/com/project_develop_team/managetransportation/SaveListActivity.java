package com.project_develop_team.managetransportation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project_develop_team.managetransportation.models.Tasks;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SaveListActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    public static final String EXTRA_TASKS_KEY = "tasks-key";

    private static final String MUST_PASS = "Must pass" + EXTRA_TASKS_KEY;

    @BindView(R.id.nameTextView)
    TextView nameTextView;
    @BindView(R.id.task_name_text_view)
    TextView taskNameTextView;
    @BindView(R.id.task_address_text_view)
    TextView taskAddressTextView;
    @BindView(R.id.task_phone_text_view)
    TextView taskPhoneTextView;

    private ValueEventListener mEventListener;

    String tasksKey;

    private String call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_list);
        ButterKnife.bind(this);

        tasksKey = getIntent().getStringExtra(EXTRA_TASKS_KEY);
        if (tasksKey == null) {
            throw new IllegalArgumentException(MUST_PASS);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.firebase_tasks)).child(tasksKey);

        call = getString(R.string.call);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tasks tasks = dataSnapshot.getValue(Tasks.class);

                String telephone = call + tasks.task_phone;

                nameTextView.setText(tasks.name);
                taskNameTextView.setText(tasks.task_name);
                taskAddressTextView.setText(tasks.task_address);
                taskPhoneTextView.setText(telephone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), R.string.fail_load_task, Toast.LENGTH_SHORT).show();
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
