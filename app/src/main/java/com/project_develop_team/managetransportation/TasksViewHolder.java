package com.project_develop_team.managetransportation;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project_develop_team.managetransportation.models.Tasks;

class TasksViewHolder extends RecyclerView.ViewHolder {

    private TextView nameTextView;
    private TextView addressTextView;

    public TasksViewHolder(View itemView) {
        super(itemView);
        nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
        addressTextView = (TextView) itemView.findViewById(R.id.addressTextView);
    }
    void bindToTasks(Tasks tasks){

        nameTextView.setText(tasks.taskName);
        addressTextView.setText(tasks.taskAddress);
    }
}
