package com.dimakers.fitoryapp.fragments;

import com.dimakers.fitoryapp.adapters.CaldroidSampleCustomAdapter;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

public class CaldroidSampleCustomFragment extends CaldroidFragment {

	@Override
	public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
		// TODO Auto-generated method stub
		return new CaldroidSampleCustomAdapter(getActivity(), month, year,
				getCaldroidData(), extraData);
	}

}
