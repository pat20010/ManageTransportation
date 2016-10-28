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
    private static final String MUST_PASS = "Must pass EXTRA_TASKS_KEY";

    @BindView(R.id.name_text_view)
    TextView nameTextView;
    @BindView(R.id.task_name_text_view)
    TextView taskNameTextView;
    @BindView(R.id.task_address_phone_text_view)
    TextView taskAddressPhoneTextView;

    private ValueEventListener eventListener;

    String tasksKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_list);
        ButterKnife.bind(this);

        tasksKey = getIntent().getStringExtra(EXTRA_TASKS_KEY);
        if (tasksKey == null) {
            throw new IllegalArgumentException(MUST_PASS);
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

                String taskAddressPhone = tasks.task_address + getString(R.string.call) + tasks.task_phone;
                nameTextView.setText(tasks.name);
                taskNameTextView.setText(tasks.task_name);
                taskAddressPhoneTextView.setText(taskAddressPhone);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), R.string.loading_list_fail, Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.child(getString(R.string.firebase_tasks)).child(tasksKey).addValueEventListener(eventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }

    @OnClick(R.id.save_button)
    public void saveList() {
        databaseReference.child(getString(R.string.firebase_tasks)).child(tasksKey).child(getString(R.string.firebase_status)).setValue(getString(R.string.transport_success));
        databaseReference.child(getString(R.string.firebase_users_tasks)).child(getUid()).child(tasksKey).removeValue();
        finish();
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
