package com.project_develop_team.managetransportation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.project_develop_team.managetransportation.models.Users;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class ListFragment extends Fragment {

    @BindView(R.id.datesTaskTextView)
    TextView datesTaskTextView;

    FirebaseRecyclerAdapter<Users, UsersViewHolder> recyclerAdapter;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;

    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        ButterKnife.bind(this, view);

        recyclerView = (RecyclerView) view.findViewById(R.id.users_list);
        recyclerView.setHasFixedSize(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE d MMMM", Locale.getDefault());
        datesTaskTextView.setText(simpleDateFormat.format(new Date()));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        Query usersQuery = getQuery(databaseReference);
        recyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(Users.class, R.layout.users_layout,
                UsersViewHolder.class, usersQuery) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, final Users model, final int position) {
            }

        };

        recyclerView.setAdapter(recyclerAdapter);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);
}