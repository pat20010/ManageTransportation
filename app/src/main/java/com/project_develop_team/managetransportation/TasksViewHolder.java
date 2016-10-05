package com.project_develop_team.managetransportation;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project_develop_team.managetransportation.models.Tasks;

import butterknife.BindView;
import butterknife.ButterKnife;

class TasksViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.taskNameTextView)
    TextView taskNameTextView;
    @BindView(R.id.taskAddressTextView)
    TextView taskAddressTextView;
    @BindView(R.id.taskPhoneTextView)
    TextView taskPhoneTextView;

    public TasksViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bindToTasks(Tasks tasks) {

        taskNameTextView.setText(tasks.taskName);
        taskAddressTextView.setText(tasks.taskAddress);
        taskPhoneTextView.setText("โทร" + " " + tasks.taskPhone);
    }
}