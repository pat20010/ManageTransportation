package com.project_develop_team.managetransportation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project_develop_team.managetransportation.models.Users;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeFragment extends Fragment {

    @BindView(R.id.account_image_view)
    ImageView accountImageView;
    @BindView(R.id.account_name_text_view)
    TextView accountNameTextView;
    @BindView(R.id.account_email_text_view)
    TextView accountEmailTextView;
    @BindView(R.id.account_gender_text_view)
    TextView accountGenderTextView;
    @BindView(R.id.account_age_text_view)
    TextView accountAgeTextView;
    @BindView(R.id.account_address_text_view)
    TextView accountAddressTextView;
    @BindView(R.id.account_phone_text_view)
    TextView accountPhoneTextView;

    DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, view);

        setDataLayout();
        return view;
    }

    @OnClick(R.id.sign_out_button)
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void setDataLayout() {
        databaseReference.child(getString(R.string.firebase_users)).child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                accountNameTextView.setText(users.name);
                accountEmailTextView.setText(users.email);
                accountGenderTextView.setText(users.gender);
                accountAgeTextView.setText(users.age);
                accountAddressTextView.setText(users.address);
                accountPhoneTextView.setText(users.phone);
                Glide.with(getActivity())
                        .load(users.image)
                        .into(accountImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), R.string.loading_list_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
