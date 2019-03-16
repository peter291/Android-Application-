package com.example.agastiyan.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.agastiyan.R;
import com.example.agastiyan.activities.MainActivity;
import com.example.agastiyan.activities.NFCReadActivity;

public class HomeScreen extends Fragment {

    private ImageView nfcview,qrview;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview  = inflater.inflate(R.layout.fragment_home, container, false);

        nfcview = (ImageView) rootview.findViewById(R.id.imageNfcView);
        qrview = (ImageView) rootview.findViewById(R.id.imageQrView);

        nfcview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), NFCReadActivity.class);
                startActivity(it);
            }
        });

        qrview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), com.example.agastiyan.scan.activities.MainActivity.class);
                startActivity(it);
            }
        });

        //returning our layout file
        return rootview;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Home");
    }
}
