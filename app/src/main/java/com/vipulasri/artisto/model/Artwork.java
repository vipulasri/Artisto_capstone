package com.vipulasri.artisto.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP-HP on 06-10-2016.
 */

public class Artwork implements Parcelable {

    private Links links;
    private String id;
    private String objectNumber;
    private String title;
    private boolean hasImage;
    private String principalOrFirstMaker;
    private String longTitle;
    private boolean showImage;
    private boolean permitDownload;
    private WebImage webImage;
    private HeaderImage headerImage;
    private boolean favorite = false;

    public String getWebLink(){
        return links.getWeb();
    }

    public String getId() {
        return id;
    }

    public String getObjectNumber() {
        return objectNumber;
    }

    public String getTitle() {
        return title;
    }

    public boolean getHasImage() {
        return hasImage;
    }

    public String getPrincipalOrFirstMaker() {
        return principalOrFirstMaker;
    }

    public String getLongTitle() {
        return longTitle;
    }

    public boolean getShowImage() {
        return showImage;
    }

    public boolean getPermitDownload() {
        return permitDownload;
    }

    public WebImage getWebImage() {
        return webImage;
    }

    public HeaderImage getHeaderImage() {
        return headerImage;
    }

    public String getOriginalImage() {
        return webImage.getUrl();
    }

    public String getThumbnailImage() {
        return webImage.getUrl().replace("=s0","=s400");
    }

    public float getAspectRatio() {
        return (float)(Math.floor(((double) webImage.getWidth()/webImage.getHeight()) * 1000 +.5)/1000);
    }

    public long getLongId(){
        return Long.parseLong(id.replace("-",""), 36);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Artwork(Links links, String id, String objectNumber, String title, boolean hasImage, String principalOrFirstMaker, String longTitle, boolean showImage, boolean permitDownload, WebImage webImage, HeaderImage headerImage){
        this.links = links;
        this.id = id;
        this.objectNumber = objectNumber;
        this.title = title;
        this.hasImage = hasImage;
        this.principalOrFirstMaker = principalOrFirstMaker;
        this.longTitle = longTitle;
        this.showImage = showImage;
        this.permitDownload = permitDownload;
        this.webImage = webImage;
        this.headerImage = headerImage;
    }

    public static class Links implements Parcelable {
        private String self;
        private String web;

        public Links(String self, String web){
            this.self = self;
            this.web = web;
        }

        public String getSelf() {
            return self;
        }

        public String getWeb() {
            return web;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.self);
            dest.writeString(this.web);
        }

        protected Links(Parcel in) {
            this.self = in.readString();
            this.web = in.readString();
        }

        public static final Creator<Links> CREATOR = new Creator<Links>() {
            @Override
            public Links createFromParcel(Parcel source) {
                return new Links(source);
            }

            @Override
            public Links[] newArray(int size) {
                return new Links[size];
            }
        };
    }

    public static class WebImage implements Parcelable {
        private String guid;
        private long offsetPercentageX;
        private long offsetPercentageY;
        private long width;
        private long height;
        private String url;

        public WebImage(String guid, long offsetPercentageX, long offsetPercentageY, long width, long height, String url){
            this.guid = guid;
            this.offsetPercentageX = offsetPercentageX;
            this.offsetPercentageY = offsetPercentageY;
            this.width = width;
            this.height = height;
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public long getWidth() {
            return width;
        }

        public long getHeight() {
            return height;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.guid);
            dest.writeLong(this.offsetPercentageX);
            dest.writeLong(this.offsetPercentageY);
            dest.writeLong(this.width);
            dest.writeLong(this.height);
            dest.writeString(this.url);
        }

        protected WebImage(Parcel in) {
            this.guid = in.readString();
            this.offsetPercentageX = in.readLong();
            this.offsetPercentageY = in.readLong();
            this.width = in.readLong();
            this.height = in.readLong();
            this.url = in.readString();
        }

        public static final Creator<WebImage> CREATOR = new Creator<WebImage>() {
            @Override
            public WebImage createFromParcel(Parcel source) {
                return new WebImage(source);
            }

            @Override
            public WebImage[] newArray(int size) {
                return new WebImage[size];
            }
        };
    }

    public static class HeaderImage implements Parcelable {
        private String guid;
        private long offsetPercentageX;
        private long offsetPercentageY;
        private long width;
        private long height;
        private String url;

        public HeaderImage(String guid, long offsetPercentageX, long offsetPercentageY, long width, long height, String url){
            this.guid = guid;
            this.offsetPercentageX = offsetPercentageX;
            this.offsetPercentageY = offsetPercentageY;
            this.width = width;
            this.height = height;
            this.url = url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.guid);
            dest.writeLong(this.offsetPercentageX);
            dest.writeLong(this.offsetPercentageY);
            dest.writeLong(this.width);
            dest.writeLong(this.height);
            dest.writeString(this.url);
        }

        protected HeaderImage(Parcel in) {
            this.guid = in.readString();
            this.offsetPercentageX = in.readLong();
            this.offsetPercentageY = in.readLong();
            this.width = in.readLong();
            this.height = in.readLong();
            this.url = in.readString();
        }

        public static final Creator<HeaderImage> CREATOR = new Creator<HeaderImage>() {
            @Override
            public HeaderImage createFromParcel(Parcel source) {
                return new HeaderImage(source);
            }

            @Override
            public HeaderImage[] newArray(int size) {
                return new HeaderImage[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.links, flags);
        dest.writeString(this.id);
        dest.writeString(this.objectNumber);
        dest.writeString(this.title);
        dest.writeByte(this.hasImage ? (byte) 1 : (byte) 0);
        dest.writeString(this.principalOrFirstMaker);
        dest.writeString(this.longTitle);
        dest.writeByte(this.showImage ? (byte) 1 : (byte) 0);
        dest.writeByte(this.permitDownload ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.webImage, flags);
        dest.writeParcelable(this.headerImage, flags);
        dest.writeByte((byte) (favorite ? 1 : 0));
    }

    protected Artwork(Parcel in) {
        this.links = in.readParcelable(Links.class.getClassLoader());
        this.id = in.readString();
        this.objectNumber = in.readString();
        this.title = in.readString();
        this.hasImage = in.readByte() != 0;
        this.principalOrFirstMaker = in.readString();
        this.longTitle = in.readString();
        this.showImage = in.readByte() != 0;
        this.permitDownload = in.readByte() != 0;
        this.webImage = in.readParcelable(WebImage.class.getClassLoader());
        this.headerImage = in.readParcelable(HeaderImage.class.getClassLoader());
        this.favorite = in.readByte()!=0;
    }

    public static final Parcelable.Creator<Artwork> CREATOR = new Parcelable.Creator<Artwork>() {
        @Override
        public Artwork createFromParcel(Parcel source) {
            return new Artwork(source);
        }

        @Override
        public Artwork[] newArray(int size) {
            return new Artwork[size];
        }
    };
}
