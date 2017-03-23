package com.project_develop_team.managetransportation;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project_develop_team.managetransportation.models.Tasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

class TasksViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.task_name_collect_text_view)
    TextView taskNameCollectTextView;
    @BindView(R.id.task_name_deliver_text_view)
    TextView taskNameDeliverTextView;
    @BindView(R.id.task_time_text_view)
    TextView taskTimeTextView;
    @BindView(R.id.task_type_text_view)
    TextView taskTypeTextView;

    public TasksViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void bindToTasks(Tasks tasks, Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        int taskDate = Integer.parseInt(simpleDateFormat.format(new Date()));

        int taskDateTomorrow = 20170324;

        String time = String.valueOf(tasks.task_time) + "0";

        if (taskDate == tasks.task_date) {
            taskTypeTextView.setText(R.string.today);
            taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_green_today));

            if (tasks.task_time <= 12) {
                taskTypeTextView.setText(R.string.express);
                taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_red_express));
            }
        }
        if (taskDateTomorrow == tasks.task_date) {
            taskTypeTextView.setText("Tomorrow");
            taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_yellow_tomorrow));
        }
        taskNameCollectTextView.setText(tasks.task_name_collect);
        taskNameDeliverTextView.setText(tasks.task_name_deliver);
        taskTimeTextView.setText(time);
    }
}