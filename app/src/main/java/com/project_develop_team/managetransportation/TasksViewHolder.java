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

    @Override
    public String toString() {
        return super.toString();

    }

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