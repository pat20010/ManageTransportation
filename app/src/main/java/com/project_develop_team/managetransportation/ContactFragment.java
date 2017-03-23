package com.project_develop_team.managetransportation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
                alertDialog();
                break;
            case R.id.call_image_view:
                alertDialog();
                break;
        }
    }

    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.not_available);
        builder.setMessage(R.string.not_available);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();
    }
}
