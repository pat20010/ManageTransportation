package com.project_develop_team.managetransportation;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project_develop_team.managetransportation.models.Tasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SaveListActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    public static final String EXTRA_TASKS_KEY = "tasks-key";

    private static final String MUST_PASS = "Must pass" + R.string.empty + EXTRA_TASKS_KEY;

    @BindView(R.id.task_type_text_view)
    TextView taskTypeTextView;
    @BindView(R.id.task_time_text_view)
    TextView taskTimeTextView;
    @BindView(R.id.task_name_collect_text_view)
    TextView taskNameCollectTextView;
    @BindView(R.id.task_address_collect_text_view)
    TextView taskAddressCollectTextView;
    @BindView(R.id.task_phone_collect_text_view)
    TextView taskPhoneCollectTextView;
    @BindView(R.id.task_name_deliver_text_view)
    TextView taskNameDeliverTextView;
    @BindView(R.id.task_address_deliver_text_view)
    TextView taskAddressDeliverTextView;
    @BindView(R.id.task_phone_deliver_text_view)
    TextView getTaskPhoneDeliverTextView;

    private ValueEventListener mEventListener;

    private ProgressDialog progressDialog;

    private AlertDialog.Builder alertDialog;

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

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tasks tasks = dataSnapshot.getValue(Tasks.class);

                setLayoutData(tasks, getApplicationContext());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), R.string.fail_load_task, Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.child(getString(R.string.firebase_tasks)).child(tasksKey).addValueEventListener(eventListener);

        mEventListener = eventListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mEventListener != null) {
            databaseReference.removeEventListener(mEventListener);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setLayoutData(final Tasks tasks, Context context) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_tasks_format), Locale.getDefault());
        int taskDate = Integer.parseInt(simpleDateFormat.format(today));

        int taskDateTomorrow = Integer.parseInt(simpleDateFormat.format(tomorrow));

        handleDateTasks(taskDate, taskDateTomorrow, tasks, context);

        setClickNavigation(tasks);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void handleDateTasks(int taskDate, int taskDateTomorrow, Tasks tasks, Context context) {
        String time = String.valueOf(tasks.task_time) + "0 น.";

        if (taskDate == tasks.task_date) {
            taskTypeTextView.setText(R.string.today);
            taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_green_today));

            if (tasks.task_time <= 12) {
                taskTypeTextView.setText(R.string.express);
                taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_red_express));
            }
        }
        if (taskDateTomorrow == tasks.task_date) {
            taskTypeTextView.setText(R.string.tomorrow);
            taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_yellow_tomorrow));
        }
        taskTimeTextView.setText(time);

        taskAddressCollectTextView.setPaintFlags(taskAddressCollectTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        taskNameCollectTextView.setText(tasks.task_name_collect);
        taskAddressCollectTextView.setText(tasks.task_address_collect);
        taskPhoneCollectTextView.setText(tasks.task_phone_collect);

        taskAddressDeliverTextView.setPaintFlags(taskAddressDeliverTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        taskNameDeliverTextView.setText(tasks.task_name_deliver);
        taskAddressDeliverTextView.setText(tasks.task_address_deliver);
        getTaskPhoneDeliverTextView.setText(tasks.task_phone_deliver);
    }

    private void setClickNavigation(final Tasks tasks) {
        taskAddressCollectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri intentCollectUri = Uri.parse("google.navigation:q=" + tasks.task_latitude_collect + "," +
                        tasks.task_longitude_collect + "&mod=d&avoid=thf");
                Intent intent = new Intent(Intent.ACTION_VIEW, intentCollectUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
        taskAddressDeliverTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri intentDeliverUri = Uri.parse("google.navigation:q=" + tasks.task_latitude_deliver + "," +
                        tasks.task_longitude_deliver + "&mod=d&avoid=thf");
                Intent intent = new Intent(Intent.ACTION_VIEW, intentDeliverUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @OnClick(R.id.transport_completed_button)
    public void saveList() {
        alertDialogConfirmation();
    }

    private void alertDialogCompleted() {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.transport_completed);
        alertDialog.setMessage(R.string.complete_transport);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.show();
    }

    private void alertDialogConfirmation() {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.transport_confirmation);
        alertDialog.setMessage(R.string.record_transport);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child(getString(R.string.firebase_tasks)).child(tasksKey).child(getString(R.string.firebase_status)).setValue(getString(R.string.transport_success));
                databaseReference.child(getString(R.string.firebase_users_tasks)).child(getUid()).child(tasksKey).removeValue();
                showProgressDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                    }
                }, 2000);
            }
        });
        alertDialog.setNegativeButton(android.R.string.cancel, null);
        alertDialog.show();
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2500);
            alertDialogCompleted();
        }
    }
}
