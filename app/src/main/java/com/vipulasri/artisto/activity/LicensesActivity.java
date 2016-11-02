package com.vipulasri.artisto.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vipulasri.artisto.R;
import com.vipulasri.artisto.activity.base.BaseActivity;

import net.yslibrary.licenseadapter.LicenseAdapter;
import net.yslibrary.licenseadapter.LicenseEntry;
import net.yslibrary.licenseadapter.Licenses;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by HP-HP on 23-10-2016.
 */

public class LicensesActivity extends BaseActivity {

    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);

        if(getToolbar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        List<LicenseEntry> licenses = new ArrayList<>();

        licenses.add(Licenses.noContent("Android SDK", "Google Inc.", "https://developer.android.com/sdk/terms.html"));
        licenses.add(Licenses.fromGitHub("jakewharton/butterknife", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("square/okhttp", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("facebook/fresco", Licenses.NAME_BSD));
        licenses.add(Licenses.fromGitHub("greenrobot/EventBus", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("pnikosis/materialish-progress", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("umano/AndroidSlidingUpPanel", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("google/gson", Licenses.FILE_NO_EXTENSION));
        licenses.add(Licenses.fromGitHub("roughike/BottomBar", Licenses.LICENSE_APACHE_V2));
        licenses.add(Licenses.fromGitHub("googlesamples/easypermissions", Licenses.LICENSE_APACHE_V2));

        LicenseAdapter adapter = new LicenseAdapter(licenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        Licenses.load(licenses);

    }
}
