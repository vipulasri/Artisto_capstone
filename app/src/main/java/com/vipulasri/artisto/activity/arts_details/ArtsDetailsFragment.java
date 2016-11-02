package com.vipulasri.artisto.activity.arts_details;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.vipulasri.artisto.R;
import com.vipulasri.artisto.activity.arts.BaseArtFragment;
import com.vipulasri.artisto.database.ArtsContract;
import com.vipulasri.artisto.database.ArtsOpenHelper;
import com.vipulasri.artisto.event.FavoriteChangeEvent;
import com.vipulasri.artisto.model.Artwork;
import com.vipulasri.artisto.utils.ImageSaveAsync;
import com.vipulasri.artisto.utils.LocalStoreUtil;
import com.vipulasri.artisto.utils.ViewUtils;
import com.vipulasri.artisto.widget.fresco_custom.BlurPostProcessor;
import com.vipulasri.artisto.widget.fresco_custom.FrescoCircularProgressDrawable;
import com.vipulasri.artisto.widget.fresco_custom.frescozoomable.AnimatedZoomableController;
import com.vipulasri.artisto.widget.fresco_custom.frescozoomable.DefaultZoomableController;
import com.vipulasri.artisto.widget.fresco_custom.frescozoomable.ZoomableDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class ArtsDetailsFragment extends BaseArtFragment implements EasyPermissions.PermissionCallbacks {

    private static final int RC_EXTERNAL_STORAGE_PERM = 123;
    private static final String TAG_ARTWORK = "artwork";
    private Artwork artwork;

    @BindView(R.id.artDetailImageView)
    ZoomableDraweeView artDetailImageView;

    @BindView(R.id.layout_bottom_sheet)
    View bottomSheetLayout;

    @BindView(R.id.artTitle)
    TextView artTitle;

    @BindView(R.id.artSubTitle)
    TextView artSubTitle;

    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;

    @BindView(R.id.saveButton)
    FloatingActionButton mFavoriteButton;

    @BindView(R.id.downloadButton)
    ImageView downloadButton;

    @BindView(R.id.shareButton)
    ImageView shareButton;

    private boolean isFavoriteChanged = false;

    public static ArtsDetailsFragment newInstance(@NonNull Artwork artwork) {
        Bundle args = new Bundle();
        args.putParcelable(TAG_ARTWORK, artwork);

        ArtsDetailsFragment fragment = new ArtsDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_arts_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        
        artwork = (Artwork) getArguments().getParcelable(TAG_ARTWORK);

        inflateData();
    }


    private void inflateData() {
        loadArtImage();

        artTitle.setText(artwork.getTitle());
        artSubTitle.setText("by "+artwork.getPrincipalOrFirstMaker());
        mFavoriteButton.setSelected(artwork.isFavorite());

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("onClick","-> floating action button");
                //as soon as user click on save button, open panel in order to make user see other options available

                if(artwork.isFavorite()) { // Already added is removed
                    LocalStoreUtil.removeFromFavorites(getActivity(), artwork.getLongId());
                    getActivity().getContentResolver().delete(ArtsContract.ArtsEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(artwork.getLongId())).build(), null, null);

                    ViewUtils.showToast(getResources().getString(R.string.removed_favorite), getActivity());
                    artwork.setFavorite(false);

                } else {
                    LocalStoreUtil.addToFavorites(getActivity(), artwork.getLongId());
                    ContentValues values = ArtsOpenHelper.getArtContentValues(artwork);
                    getActivity().getContentResolver().insert(ArtsContract.ArtsEntry.CONTENT_URI, values);

                    ViewUtils.showToast(getResources().getString(R.string.added_favorite), getActivity());
                    artwork.setFavorite(true);
                }

                isFavoriteChanged = true;
                mFavoriteButton.setSelected(artwork.isFavorite());

                EventBus.getDefault().post(new FavoriteChangeEvent(isFavoriteChanged));

                showPanel();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("onClick","-> download button");
            }
        });

        final String shareBody = "Checkout this Awesome Art!!\n\n"+artwork.getTitle()+"\n\nby "+artwork.getPrincipalOrFirstMaker()+"\n\n"+artwork.getWebLink();
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("onClick","-> share button");
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(shareBody)
                        .getIntent(), getString(R.string.action_share)));
            }
        });

    }

    private boolean isPanelCollapsed() {
        return slidingUpPanelLayout.getPanelState()==SlidingUpPanelLayout.PanelState.COLLAPSED;
    }

    private void collapsePanel() {
        if(!isPanelCollapsed()) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    private void hidePanel() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    private void showPanel() {
        if(isPanelCollapsed()){
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
    }

    private void loadArtImage() {
        FrescoCircularProgressDrawable progressDrawable = new FrescoCircularProgressDrawable.Builder()
                .setRingWidth(getResources().getDimensionPixelSize(R.dimen.progress_ring_width))
                .setOutlineColor(getResources().getColor(R.color.colorGrey500))
                .setRingColor(getResources().getColor(R.color.colorWhite))
                .setSize(getResources().getDimensionPixelSize(R.dimen.progress_size))
                .create();

        final AnimatedZoomableController zc = (AnimatedZoomableController) artDetailImageView.getZoomableController();
        artDetailImageView.setTapListener(new GestureDetector.SimpleOnGestureListener() {
            private final PointF mDoubleTapViewPoint = new PointF();
            private final PointF mDoubleTapImagePoint = new PointF();
            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                PointF vp = new PointF(e.getX(), e.getY());
                PointF ip = zc.mapViewToImage(vp);
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDoubleTapViewPoint.set(vp);
                        mDoubleTapImagePoint.set(ip);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (zc.getScaleFactor() < 1.5f) {
                            zc.zoomToPoint(2.0f, ip, vp, DefaultZoomableController.LIMIT_ALL, 300, null);
                        } else {
                            zc.zoomToPoint(1.0f, ip, vp, DefaultZoomableController.LIMIT_ALL, 300, null);
                        }
                        break;
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                if(!isPanelCollapsed()){
                    collapsePanel();
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        RenderScript renderScript = RenderScript.create(getActivity());

        Uri blurUri = Uri.parse(artwork.getThumbnailImage());

        ImageRequest blurRequest = ImageRequestBuilder.newBuilderWithSource(blurUri)
                .setPostprocessor(new BlurPostProcessor(renderScript))
                .setProgressiveRenderingEnabled(true)
                .build();

        DraweeController ctrl = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(blurRequest)
                .setImageRequest(ImageRequest.fromUri(artwork.getOriginalImage()))
                .setTapToRetryEnabled(true)
                .build();

        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(progressDrawable)
                .build();

        artDetailImageView.setController(ctrl);
        artDetailImageView.setHierarchy(hierarchy);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_EXTERNAL_STORAGE_PERM)
    public void donwloadTask() {
        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Have permission, do the thing!

            ImageSaveAsync imageSaveAsync = new ImageSaveAsync(getActivity()) {
                @Override
                protected void onPreExecute() {
                    ViewUtils.showToast("Saving Image...", getActivity());
                }

                @Override
                protected void onPostExecute(String message) {
                    ViewUtils.showToast(message, getActivity());
                }
            };
            imageSaveAsync.execute(artwork.getOriginalImage());

        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.permission_external_storage_rationale),
                    RC_EXTERNAL_STORAGE_PERM, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d("", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("", "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.rationale_ask_again))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.button_allow))
                    .setNegativeButton(getString(R.string.button_deny), null /* click listener */)
                    .setRequestCode(RC_EXTERNAL_STORAGE_PERM)
                    .build()
                    .show();
        }
    }

}
