package com.project_develop_team.managetransportation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project_develop_team.managetransportation.models.Tasks;

import butterknife.BindView;
import butterknife.ButterKnife;

class TasksViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.task_name_text_view)
    TextView taskNameTextView;
    @BindView(R.id.task_address_text_view)
    TextView taskAddressTextView;
    @BindView(R.id.task_phone_text_view)
    TextView taskPhoneTextView;
    @BindView(R.id.title_text_view)
    TextView titleTextView;


    public TasksViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bindToTasks(Tasks tasks) {

        taskNameTextView.setText(tasks.task_name);
        taskAddressTextView.setText(tasks.task_address);
        taskPhoneTextView.setText("โทร" + " " + tasks.task_phone);
        titleTextView.setText(R.string.text_title);
    }
}