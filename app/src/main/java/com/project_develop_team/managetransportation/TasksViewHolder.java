package com.project_develop_team.managetransportation;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project_develop_team.managetransportation.models.Tasks;

import butterknife.BindView;
import butterknife.ButterKnife;

class TasksViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.task_name_text_view)
    TextView taskNameTextView;
    @BindView(R.id.task_address_phone_text_view)
    TextView taskAddressPhoneTextView;
    @BindView(R.id.title_text_view)
    TextView titleTextView;

    public TasksViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bindToTasks(Tasks tasks, Context context) {
        String taskAddressPhone = tasks.task_address + context.getString(R.string.call) + tasks.task_phone;

        taskNameTextView.setText(tasks.task_name);
        taskAddressPhoneTextView.setText(taskAddressPhone);
        titleTextView.setText(context.getString(R.string.text_title));

    }
}