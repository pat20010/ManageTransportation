package com.project_develop_team.managetransportation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project_develop_team.managetransportation.models.Contact;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactFragment extends Fragment implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @BindView(R.id.message_image_view)
    ImageView massageImageView;
    @BindView(R.id.call_image_view)
    ImageView callImageView;

    DatabaseReference databaseReference;

    String phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(getString(R.string.firebase_contact)).child(getString(R.string.firebase_call)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contact contact = dataSnapshot.getValue(Contact.class);

                phoneNumber = contact.call;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), R.string.loading_list_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.bind(this, view);

        massageImageView.setOnClickListener(this);
        callImageView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_image_view:
                Intent mailClient = new Intent(Intent.ACTION_SEND);
                String address = getString(R.string.email_address);
                mailClient.setType(getString(R.string.mail_type));
                mailClient.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
                startActivity(Intent.createChooser(mailClient, getString(R.string.send_mail)));
                break;
            case R.id.call_image_view:
                requestPermissionPhone();
                break;
        }
    }

    private void requestPermissionPhone() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(getString(R.string.tel) + phoneNumber));
            startActivity(intent);
        }
    }
}
