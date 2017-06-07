package com.project_develop_team.managetransportation.viewholder;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project_develop_team.managetransportation.R;
import com.project_develop_team.managetransportation.models.Tasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TasksViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.task_name_collect_text_view)
    TextView taskNameCollectTextView;
    @BindView(R.id.task_name_deliver_text_view)
    TextView taskNameDeliverTextView;
    @BindView(R.id.task_time_text_view)
    TextView taskTimeTextView;
    @BindView(R.id.task_type_text_view)
    TextView taskTypeTextView;

    public CardView cardView;

    public RelativeLayout relativeLayout;

    public LinearLayout linearLayout;

    public TasksViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        cardView = (CardView) itemView.findViewById(R.id.cardview_layout);
        relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_layout);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout);
    }

    public void bindToTasks(Tasks tasks, Context context) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.date_tasks_format), Locale.getDefault());
        String taskDate = simpleDateFormat.format(today);

        String taskDateTomorrow = simpleDateFormat.format(tomorrow);

        handleDateTasks(taskDate, taskDateTomorrow, tasks, context);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void handleDateTasks(String taskDate, String taskDateTomorrow, Tasks tasks, Context context) {
        double taskTime = Double.parseDouble((tasks.task_time));

        if (taskDate.equals(tasks.task_date)) {
            taskTypeTextView.setText(R.string.today);
            taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_green_today));
            setDataTasks(tasks);

            if (taskTime <= 12.00) {
                taskTypeTextView.setText(R.string.express);
                taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_red_express));
                setDataTasks(tasks);
            }
        }
        if (taskDateTomorrow.equals(tasks.task_date)) {
            taskTypeTextView.setText(R.string.tomorrow);
            taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_yellow_tomorrow));
            setDataTasks(tasks);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void bindToTaskComplete(Tasks tasks, Context context) {
        taskTypeTextView.setText(R.string.complete);
        taskTypeTextView.setBackgroundColor(context.getColor(R.color.color_gray_complete));
        setDataTasks(tasks);
    }

    private void setDataTasks(Tasks tasks) {
        String time = tasks.task_time + " น.";

        taskNameCollectTextView.setText(tasks.task_name_collect);
        taskNameDeliverTextView.setText(tasks.task_name_deliver);
        taskTimeTextView.setText(time);
    }
}