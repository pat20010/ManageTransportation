package com.project_develop_team.managetransportation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.message_image_view)
    ImageView massageImageView;
    @BindView(R.id.call_image_view)
    ImageView callImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                String address = "managetransportation56@gmail.com";
                mailClient.setType("text/email");
                mailClient.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
                startActivity(Intent.createChooser(mailClient, "Send email with"));
                break;
            case R.id.call_image_view:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "083-832-6098"));
                startActivity(intent);
                break;
        }
    }
}
