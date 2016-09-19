package com.project_develop_team.managetransportation;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.firstNameTextView)
    TextView firstNameTextView;
    @BindView(R.id.lastNameTextView)
    TextView lastNameTextView;
    @BindView(R.id.addressTextView)
    TextView addressTextView;
    @BindView(R.id.genderTextView)
    TextView genderTextView;

    public UsersViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
