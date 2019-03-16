package com.example.agastiyan.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.example.agastiyan.adapters.GridViewAdapter;
import com.example.agastiyan.R;

public class ProductScreen extends Fragment {

    GridView androidGridView;

    String[] gridViewString = {
            "home", "residence", "commercial"
    } ;

    int[] gridViewImageId = {

            R.drawable.agas_white, R.drawable.agas_white, R.drawable.agas_white
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_product, container, false);
        GridViewAdapter adapterViewAndroid = new GridViewAdapter(getActivity(), gridViewString, gridViewImageId);

        androidGridView=(GridView)rootView.findViewById(R.id.grid_view_item);
        androidGridView.setAdapter(adapterViewAndroid);

        //returning our layout file
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Product");
    }
}
