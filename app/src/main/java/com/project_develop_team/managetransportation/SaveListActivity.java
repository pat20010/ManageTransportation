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
import android.widget.Button;
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

    private Button transportCompletedButton;

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

        transportCompletedButton = (Button) findViewById(R.id.transport_completed_button);
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
        String taskDate = simpleDateFormat.format(today);

        String taskDateTomorrow = simpleDateFormat.format(tomorrow);

        handleDateTasks(taskDate, taskDateTomorrow, tasks, context);

        setClickNavigation(tasks);

        transportCompletedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogConfirmation(tasks);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void handleDateTasks(String taskDate, String taskDateTomorrow, Tasks tasks, Context context) {
        double taskTime = Double.parseDouble(tasks.task_time);

        if (taskDate.equals(tasks.task_date)) {
            taskTypeTextView.setText(R.string.today);
            taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_green_today));

            if (taskTime <= 12) {
                taskTypeTextView.setText(R.string.express);
                taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_red_express));
            }
        }
        if (taskDateTomorrow.equals(tasks.task_date)) {
            taskTypeTextView.setText(R.string.tomorrow);
            taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_yellow_tomorrow));
        }

        String time = tasks.task_time + " น.";

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
        final double tasksLatCollect = Double.parseDouble(tasks.task_latitude_collect);
        final double tasksLongCollect = Double.parseDouble(tasks.task_longitude_collect);

        final double tasksLatDeliver = Double.parseDouble(tasks.task_latitude_deliver);
        final double tasksLongDeliver = Double.parseDouble(tasks.task_longitude_deliver);

        taskAddressCollectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(tasks.task_latitude_collect.equals("0") && tasks.task_longitude_collect.equals("0"))) {
                    Uri intentCollectUri = Uri.parse("google.navigation:q=" + tasksLatCollect + "," +
                            tasksLongCollect + "&mod=d&avoid=thf");
                    Intent intent = new Intent(Intent.ACTION_VIEW, intentCollectUri);
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                } else {
                    alertDialogNavigation();
                }
            }
        });
        taskAddressDeliverTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri intentDeliverUri = Uri.parse("google.navigation:q=" + tasksLatDeliver + "," +
                        tasksLongDeliver + "&mod=d&avoid=thf");
                Intent intent = new Intent(Intent.ACTION_VIEW, intentDeliverUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
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

    private void alertDialogUnsuccessful() {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.transport_unsuccessful);
        alertDialog.setMessage(R.string.unsuccessful_transport);
        alertDialog.setPositiveButton(R.string.ok, null);

        alertDialog.show();
    }


    private void alertDialogConfirmation(final Tasks tasks) {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.transport_confirmation);
        alertDialog.setMessage(R.string.record_transport);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (tasks.task_latitude_collect.equals("0") && tasks.task_longitude_collect.equals("0")) {

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
                } else {
                    alertDialogUnsuccessful();
                }
            }
        });
        alertDialog.setNegativeButton(android.R.string.cancel, null);
        alertDialog.show();
    }

    private void alertDialogNavigation() {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.location_completed);
        alertDialog.setMessage(R.string.complete_location);
        alertDialog.setPositiveButton(R.string.ok, null);

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
