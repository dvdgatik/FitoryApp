package com.dimakers.fitoryapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dimakers.fitoryapp.R;
import com.dimakers.fitoryapp.api.API;
import com.dimakers.fitoryapp.api.apiServices.FitoryService;
import com.dimakers.fitoryapp.fragments.FragmentIntro2;
import com.dimakers.fitoryapp.fragments.FragmentIntro3;
import com.dimakers.fitoryapp.services.BuscarBeacon;

import org.apache.commons.io.FileUtils;

import java.util.Timer;
import java.util.TimerTask;

public class Intro extends AppCompatActivity {
    FitoryService service = API.getApi().create(FitoryService.class);
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager viewPager;
    Timer timer;
//    private SwippeableViewPager mViewPager;

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true); //getItem(-1) for previous
                    }
                });
            }
        }, 4000,4000);
    }

    //    private GestureDetector mGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
//        Intent serviceIntent = new Intent(this, BuscarBeacon.class);
//        ContextCompat.startForegroundService(this, serviceIntent);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.container);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true); //getItem(-1) for previous
                            }
                        });
                    }
                }, 4000,4000);
                return false;
            }
        });
        viewPager.setAdapter(mSectionsPagerAdapter);
//        mGestureDetector = new GestureDetector(this, new CustomGestureListener(mViewPager) {
//            @Override
//            public boolean onSwipeRight() {
//                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true); //getItem(-1) for previous
//                return false;
//            }
//
//            @Override
//            public boolean onSwipeLeft() {
//                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true); //getItem(-1) for previous
//                return false;
//            }
//
//            @Override
//            public boolean onTouch() {
//                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true); //getItem(-1) for previous
//                return false;
//            }
//        });

//        mViewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return !mGestureDetector.onTouchEvent(event);
//            }
//        });

//        mViewPager.setGestureDetector(mGestureDetector);

    }

    public void iniciarSesion(View view) {
        Intent intent = new Intent(Intro.this,Login.class);
        startActivity(intent);
    }

    public void registrarme(View view) {
        Intent intent = new Intent(Intro.this,SignUp.class);
        startActivity(intent);
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_intro1, container, false);
            ImageView imageView = rootView.findViewById(R.id.section_image);
            Glide.with(getActivity()).load(R.drawable.foto_intro1).into(imageView);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_text);
//            TextView textView2 = (TextView) rootView.findViewById(R.id.section_sub_text);
//            ImageView imageView = (ImageView) rootView.findViewById(R.id.section_image);
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:
                    Toast.makeText(getActivity(), "intro 1", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getActivity(), "intro 2", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getActivity(), "intro 3", Toast.LENGTH_SHORT).show();
                    break;
            }
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0 :
                    return PlaceholderFragment.newInstance(position);
                case 1:
                    return FragmentIntro2.newInstance();
                case 2:
                    return FragmentIntro3.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }
}
