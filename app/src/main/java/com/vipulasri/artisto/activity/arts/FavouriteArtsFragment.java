package com.vipulasri.artisto.activity.arts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vipulasri.artisto.R;
import com.vipulasri.artisto.database.ArtsContract;
import com.vipulasri.artisto.database.ArtsOpenHelper;
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
public class FavouriteArtsFragment extends BaseArtFragment implements FavoriteArtsAdapter.Callbacks, LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private FavoriteArtsAdapter artsAdapter;
    private List<Artwork> mArts = new ArrayList<>();
    @BindView(R.id.layout_no_favorite) View noFavoriteView;
    @BindView(R.id.layout_loading_view) View loadingView;

    private static final int CURSOR_LOADER_ID = 0;

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

    public static FavouriteArtsFragment newInstance() {
        return new FavouriteArtsFragment();
    }

    public FavouriteArtsFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        int columnCount = getResources().getInteger(R.integer.arts_columns);
        int spacing = Utils.dpToPx(5, getActivity()); // 50px

        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacing));
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        initAdapter(mArts);
    }

    private void initAdapter(List<Artwork> movies) {
        artsAdapter = new FavoriteArtsAdapter(movies);
        artsAdapter.setCallbacks(this);
        recyclerView.setAdapter(artsAdapter);
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

            getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

        } else {
            LocalStoreUtil.addToFavorites(getActivity(), artwork.getLongId());
            ViewUtils.showToast(getResources().getString(R.string.added_favorite),getActivity());

            ContentValues values = ArtsOpenHelper.getArtContentValues(artwork);
            getActivity().getContentResolver().insert(ArtsContract.ArtsEntry.CONTENT_URI, values);
        }

        artsAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        showLoadingView(true);
        return new CursorLoader(getActivity(),
                ArtsContract.ArtsEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Loader is finished loading data

        if(data.getCount()>0) {
            showDataView(true);
            initAdapter(getArtsFromCursor(data));
        } else {
            showNoFavorite(true);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Loader is reset, assign data to null
        initAdapter(null);
    }

    private List<Artwork> getArtsFromCursor(Cursor cursor) {

        List<Artwork> artworks = new ArrayList<>();

        if (cursor != null) {
            /*Log.e("cursor length","->"+cursor.getCount());
            Log.e("column length","->"+cursor.getColumnCount());*/

            if (cursor.moveToFirst()){
                do{

                    String art_web_link = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_WEB_LINK));
                    String art_id = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_ID));
                    String art_object_number = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_OBJECT_NUMBER));
                    String art_title = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_TITLE));
                    boolean art_has_image = cursor.getInt(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_HAS_IMAGE))==1;
                    String art_maker = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_MAKER));
                    String art_long_title = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_lONG_TITLE));
                    boolean art_show_image = cursor.getInt(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_SHOW_IMAGE))==1;
                    boolean art_permit_download = cursor.getInt(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_PERMIT_DOWNLOAD))==1;
                    String art_web_image = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_WEB_IMAGE));
                    String art_web_image_height = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_WEB_IMAGE_HEIGHT));
                    String art_web_image_width = cursor.getString(cursor.getColumnIndex(ArtsContract.ArtsEntry.ART_WEB_IMAGE_WIDTH));

                    Artwork.Links links = new Artwork.Links("",art_web_link);
                    Artwork.WebImage webImage = new Artwork.WebImage("", 0L, 0L, Long.valueOf(art_web_image_width), Long.valueOf(art_web_image_height), art_web_image);

                    Artwork artwork = new Artwork(links, art_id, art_object_number, art_title, art_has_image, art_maker, art_long_title
                                    , art_show_image, art_permit_download,webImage, null);


                    artworks.add(artwork);

                }while(cursor.moveToNext());
            }

        }

        return artworks;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final FavoriteChangeEvent event){
        Log.e("onEvent","->"+event.isFavoriteChanged());

        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

    }

    private void showNoFavorite(boolean value){

        int noFavoriteVisibility = value? View.VISIBLE : View.GONE;
        noFavoriteView.setVisibility(noFavoriteVisibility);

        int hiddenViewVisibility = value? View.GONE : View.VISIBLE;
        loadingView.setVisibility(hiddenViewVisibility);
        recyclerView.setVisibility(hiddenViewVisibility);
    }

    private void showLoadingView(boolean value){
        int loadingVisibility = value? View.VISIBLE : View.GONE;
        loadingView.setVisibility(loadingVisibility);

        int hiddenViewVisibility = value? View.GONE : View.VISIBLE;
        noFavoriteView.setVisibility(hiddenViewVisibility);
        recyclerView.setVisibility(hiddenViewVisibility);
    }

    private void showDataView(boolean value){
        int dataVisibility = value? View.VISIBLE : View.GONE;
        recyclerView.setVisibility(dataVisibility);

        int hiddenViewVisibility = value? View.GONE : View.VISIBLE;
        noFavoriteView.setVisibility(hiddenViewVisibility);
        loadingView.setVisibility(hiddenViewVisibility);
    }

}
