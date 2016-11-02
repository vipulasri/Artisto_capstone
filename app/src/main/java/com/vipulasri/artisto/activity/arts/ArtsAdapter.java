package com.vipulasri.artisto.activity.arts;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.vipulasri.artisto.R;
import com.vipulasri.artisto.model.Artwork;
import com.vipulasri.artisto.utils.ImageLoadingUtils;
import com.vipulasri.artisto.utils.LocalStoreUtil;
import com.vipulasri.artisto.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP-HP on 29-03-2016.
 */
public class ArtsAdapter extends RecyclerView.Adapter {

    public interface Callbacks {
        public void onArtClick(Artwork artwork);
        public void onFavoriteClick(Artwork artwork);
    }

    private static final int TYPE_AD = 0;
    private static final int TYPE_ITEM = 1;

    private Callbacks mCallbacks;
    private Context context;
    private List<Object> mFeedList;

    public ArtsAdapter(List<Object> feedList) {
        this.mFeedList = feedList;
    }

    @Override
    public int getItemViewType(int position) {
        return (position % ArtsFragment.ITEMS_PER_AD == 0) ? TYPE_AD : TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        switch (viewType) {
            case TYPE_ITEM: View view = View.inflate(parent.getContext(), R.layout.item_art, null);
                            return new ArtViewHolder(view);
            case TYPE_AD:

            default: View adView = View.inflate(parent.getContext(), R.layout.item_native_ad, null);
                return new AdViewHolder(adView);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        Log.e("position","->"+position);

        switch (viewType) {
            case TYPE_ITEM:
                final ArtViewHolder artViewHolder = (ArtViewHolder) holder;

                final Artwork artwork;
                try {
                    artwork = (Artwork) mFeedList.get(position);

                    artViewHolder.mArtName.setText(artwork.getTitle());
                    artViewHolder.mArtBy.setText("by "+artwork.getPrincipalOrFirstMaker());
                    ImageLoadingUtils.load(artViewHolder.mArtImage, artwork.getThumbnailImage());
                    artViewHolder.mArtImage.setAspectRatio(artwork.getAspectRatio());

                    if(LocalStoreUtil.hasInFavorites(context, artwork.getLongId())) {
                        artViewHolder.mFavoriteButton.setSelected(true);
                        artwork.setFavorite(true);
                    } else {
                        artViewHolder.mFavoriteButton.setSelected(false);
                        artwork.setFavorite(false);
                    }

                    artViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mCallbacks!=null){
                                mCallbacks.onArtClick(artwork);
                            }
                        }
                    });

                    artViewHolder.mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mCallbacks!=null) {
                                artViewHolder.mFavoriteButton.setSelected(!artwork.isFavorite());
                                mCallbacks.onFavoriteClick(artwork);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

            case TYPE_AD:

            default:

                /*StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);
                holder.itemView.setLayoutParams(layoutParams);*/

                final AdViewHolder adViewHolder = (AdViewHolder) holder;

                //AdLoader.Builder builder = new AdLoader.Builder(context, context.getResources().getString(R.string.admob_ad_unit_id));
                AdLoader.Builder builder = new AdLoader.Builder(context, ArtsFragment.ADMOB_AD_UNIT_ID);

                builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {

                        adViewHolder.mNativeAppInstallAdView.setImageView(adViewHolder.mAdImage);
                        adViewHolder.mNativeAppInstallAdView.setIconView(adViewHolder.mAdIcon);
                        adViewHolder.mNativeAppInstallAdView.setHeadlineView(adViewHolder.mAdHeadline);
                        adViewHolder.mNativeAppInstallAdView.setBodyView(adViewHolder.mAdBody);
                        adViewHolder.mNativeAppInstallAdView.setCallToActionView(adViewHolder.mAdButton);

                        ((TextView) adViewHolder.mNativeAppInstallAdView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
                        ((TextView) adViewHolder.mNativeAppInstallAdView.getBodyView()).setText(nativeAppInstallAd.getBody());
                        ((Button) adViewHolder.mNativeAppInstallAdView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
                        ((ImageView) adViewHolder.mNativeAppInstallAdView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon().getDrawable());

                        List<NativeAd.Image> images = nativeAppInstallAd.getImages();

                        if (images.size() > 0) {
                            ((ImageView) adViewHolder.mNativeAppInstallAdView.getImageView()).setImageDrawable(images.get(0).getDrawable());
                        }

                        // Assign native ad object to the native view.
                        adViewHolder.mNativeAppInstallAdView.setNativeAd(nativeAppInstallAd);

                        adViewHolder.mAdParentView.removeAllViews();
                        adViewHolder.mAdParentView.addView(adViewHolder.mNativeAppInstallAdView);
                    }
                });

                adViewHolder.mNativeAppInstallAdView.setVisibility(View.INVISIBLE);

                AdLoader adLoader = builder.withAdListener(new AdListener() {

                    @Override
                    public void onAdLoaded() {
                        Log.e("Ads","loaded native ad");
                        adViewHolder.mNativeAppInstallAdView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Toast.makeText(context, "Failed to load native ad: "+ errorCode, Toast.LENGTH_SHORT).show();
                    }
                }).build();

                adLoader.loadAd(new AdRequest.Builder().build());

        }

    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

    public void setCallbacks(Callbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    public class ArtViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.artTextView) TextView mArtName;
        @BindView(R.id.artBy) TextView mArtBy;
        @BindView(R.id.artImage) SimpleDraweeView mArtImage;
        @BindView(R.id.art_item_btn_favorite) ImageButton mFavoriteButton;

        public ArtViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adCardView) CardView mAdParentView;
        @BindView(R.id.nativeAppInstallAdView) NativeAppInstallAdView mNativeAppInstallAdView;
        @BindView(R.id.appinstall_image) ImageView mAdImage;
        @BindView(R.id.appinstall_app_icon) ImageView mAdIcon;
        @BindView(R.id.appinstall_headline) TextView mAdHeadline;
        @BindView(R.id.appinstall_body) TextView mAdBody;
        @BindView(R.id.appinstall_call_to_action) Button mAdButton;

        public AdViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
