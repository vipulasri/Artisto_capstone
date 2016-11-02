package com.vipulasri.artisto.activity.arts;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.vipulasri.artisto.R;
import com.vipulasri.artisto.core.ArtistoService;
import com.vipulasri.artisto.core.ResponseListener;
import com.vipulasri.artisto.database.ArtsContract;
import com.vipulasri.artisto.database.ArtsOpenHelper;
import com.vipulasri.artisto.dto.ArtworkResponse;
import com.vipulasri.artisto.event.ArtSelectedEvent;
import com.vipulasri.artisto.event.FavoriteChangeEvent;
import com.vipulasri.artisto.model.Artwork;
import com.vipulasri.artisto.utils.LocalStoreUtil;
import com.vipulasri.artisto.utils.Utils;
import com.vipulasri.artisto.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtsFragment extends BaseArtFragment implements ResponseListener<ArtworkResponse>, ArtsAdapter.Callbacks{

    // Test unit ids for Advanced Native Ads as currently its in beta
    public static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/3986624511";
    private static final String ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713";

    public static final int ITEMS_PER_AD = 8;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private ArtsAdapter artsAdapter;
    //private List<Artwork> mArts = new ArrayList<>();
    private List<Object> mArts = new ArrayList<>();
    private int currentPage, totalPages;
    @BindView(R.id.layout_loading_view) View loadingView;
    @BindView(R.id.layout_no_connection) View noConnectionView;
    @BindView(R.id.checkFavorite) TextView mCheckFavoriteText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDetach() {

        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        super.onDetach();
    }


    public static ArtsFragment newInstance() {
        return new ArtsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        //MobileAds.initialize(getActivity(), getResources().getString(R.string.admob_app_id));
        MobileAds.initialize(getActivity(), ADMOB_APP_ID);

        if(!isInternetAvailable()) {
            showNoInternetView(true);
        }

        mCheckFavoriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).selectFavoriteTab();
            }
        });

        int columnCount = getResources().getInteger(R.integer.arts_columns);
        int spacing = Utils.dpToPx(5, getActivity()); // 50px

        /*GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), columnCount);
        boolean includeEdge = false;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(columnCount, spacing, includeEdge));
        recyclerView.setLayoutManager(gridLayoutManager);*/

        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacing));
        recyclerView.setLayoutManager(staggeredGridLayoutManager);


        recyclerView.addOnScrollListener(new EndlessRecyclerView(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {

                Log.e("current_page","->"+current_page);
                Log.e("currentPage","->"+currentPage);
                Log.e("totalPages","->"+totalPages);

                if(currentPage<totalPages) {
                    getMoviesData(current_page);
                }

            }
        });

        artsAdapter = new ArtsAdapter(mArts);
        artsAdapter.setCallbacks(this);
        recyclerView.setAdapter(artsAdapter);
        getMoviesData(1);
    }

    public void getMoviesData(final int currentPage) {
        if(isInternetAvailable()) {

            new ArtistoService().getArtworks(Integer.toString(currentPage), this);

            if(currentPage==1){
                showLoadingView(true);
            }

        } else {

            if(currentPage==1){
                showNoInternetView(true);
            }

            Snackbar snackbar = Snackbar
                    .make(getCoordinatorLayout(), R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getMoviesData(currentPage);
                        }
                    });

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("error", "->" + error);
    }

    @Override
    public void onResponse(ArtworkResponse response) {
        //Log.e("response", "->" + response.getPage());

        showDataView(true);

        if(response==null || response.getArtObjects().isEmpty()) {
            return;
        }

        currentPage++;
        totalPages = response.getCount()/10; //10 objects per page

        List<Artwork> artworks = response.getArtObjects();

        for (int i=0; i<artworks.size();++i) {
            if(artworks.get(i).getWebImage()!=null)
              mArts.add(artworks.get(i));
        }

        addNativeAppInstallAds();

        artsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onArtClick(Artwork artwork) {
        EventBus.getDefault().post(new ArtSelectedEvent(artwork));
    }

    @Override
    public void onFavoriteClick(Artwork artwork) {
        if(artwork.isFavorite()) { // Already added is removed
            LocalStoreUtil.removeFromFavorites(getActivity(), artwork.getLongId());
            ViewUtils.showToast(getResources().getString(R.string.removed_favorite),getActivity());

            getActivity().getContentResolver().delete(ArtsContract.ArtsEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(artwork.getLongId())).build(), null, null);

        } else {
            LocalStoreUtil.addToFavorites(getActivity(), artwork.getLongId());
            ViewUtils.showToast(getResources().getString(R.string.added_favorite),getActivity());

            ContentValues values = ArtsOpenHelper.getArtContentValues(artwork);
            getActivity().getContentResolver().insert(ArtsContract.ArtsEntry.CONTENT_URI, values);
        }

        artsAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final FavoriteChangeEvent event){
        Log.e("onEvent","->"+event.isFavoriteChanged());

        artsAdapter.notifyDataSetChanged();

    }

    private void showNoInternetView(boolean value){
        int noInternetVisibility = value? View.VISIBLE : View.GONE;
        noConnectionView.setVisibility(noInternetVisibility);

        int hiddenViewVisibility = value? View.GONE : View.VISIBLE;
        loadingView.setVisibility(hiddenViewVisibility);
        recyclerView.setVisibility(hiddenViewVisibility);
    }

    private void showLoadingView(boolean value){
        int loadingVisibility = value? View.VISIBLE : View.GONE;
        loadingView.setVisibility(loadingVisibility);

        int hiddenViewVisibility = value? View.GONE : View.VISIBLE;
        noConnectionView.setVisibility(hiddenViewVisibility);
        recyclerView.setVisibility(hiddenViewVisibility);
    }

    private void showDataView(boolean value){
        int dataVisibility = value? View.VISIBLE : View.GONE;
        recyclerView.setVisibility(dataVisibility);

        int hiddenViewVisibility = value? View.GONE : View.VISIBLE;
        noConnectionView.setVisibility(hiddenViewVisibility);
        loadingView.setVisibility(hiddenViewVisibility);
    }

    private void addNativeAppInstallAds() {

        // Loop through the items array and place a new Native Express ad in every ith position in
        // the items List.
        for (int i = 0; i <= mArts.size(); i += ITEMS_PER_AD) {
            final NativeAppInstallAdView adView = new NativeAppInstallAdView(getActivity());
            mArts.add(i, adView);
        }
    }

}
