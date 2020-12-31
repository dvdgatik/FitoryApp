package com.dimakers.fitoryapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dimakers.fitoryapp.R;

public class FragmentIntro2 extends Fragment {

    public static FragmentIntro2 newInstance() {
        FragmentIntro2 fragment = new FragmentIntro2();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = view.findViewById(R.id.section_image);
        Glide.with(getActivity()).load(R.drawable.foto_intro2).into(imageView);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intro2,container,false);
    }
}
