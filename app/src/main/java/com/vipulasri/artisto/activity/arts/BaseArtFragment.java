package com.vipulasri.artisto.activity.arts;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;

import com.vipulasri.artisto.utils.NetworkUtils;

/**
 * Created by HP-HP on 06-10-2016.
 */
public class BaseArtFragment extends Fragment {

    public boolean isInternetAvailable() {
        return NetworkUtils.isNetworkConnected(getActivity());
    }

    public void showSnackBar(String value) {
        ((HomeActivity) getActivity()).showSnackBar(value);
    }

    public void showSnackBar(int value) {
        ((HomeActivity) getActivity()).showSnackBar(value);
    }

    public CoordinatorLayout getCoordinatorLayout() {
        return ((HomeActivity) getActivity()).getCoordinatorLayout();
    }

}
