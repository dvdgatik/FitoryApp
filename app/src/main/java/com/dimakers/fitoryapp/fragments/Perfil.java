package com.dimakers.fitoryapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dimakers.fitoryapp.R;

public class Perfil extends Fragment {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        tab.setIcon(R.drawable.icono_perfil_datos_activo);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.icono_perfil_favoritos_activo);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.icono_suscripcion_activo);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.icono_perfil_pago_activo);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        tab.setIcon(R.drawable.icono_perfil_datos_inactivo);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.icono_perfil_favoritos_inactivo);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.icono_suscripcion_inactivo);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.icono_perfil_pago_inactivo);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil,container,false);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Informacion.newInstance();
                case 1:
                    return Favoritos.newInstance();
                case 2:
//                    return SuscripcionMensualDetalle.newInstance();
                    return MisSuscripciones.newInstance();
                case 3:
                    return MisTarjetas.newInstance();
                default:
                    return Informacion.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
