package com.vipulasri.artisto.activity.arts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vipulasri.artisto.R;
import com.vipulasri.artisto.activity.LicensesActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP-HP on 22-10-2016.
 */

public class AboutFragment extends BaseArtFragment {

    @BindView(R.id.cardLicenses)
    CardView cardLicenses;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        cardLicenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), LicensesActivity.class));
            }
        });
    }
}
