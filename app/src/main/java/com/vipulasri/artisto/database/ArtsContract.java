package com.vipulasri.artisto.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ArtsContract {

	public static final String CONTENT_AUTHORITY = "com.vipulasri.artisto";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


	public static final class ArtsEntry implements BaseColumns {

		// table name
		public static final String TABLE_ARTS = "TABLE_ARTS";

		// columns
		public static final String ID="id";
		public static final String ART_WEB_LINK = "art_web_link";
		public static final String ART_ID = "art_id";
		public static final String ART_LONG_ID = "art_long_id";
		public static final String ART_OBJECT_NUMBER = "art_object_number";
		public static final String ART_TITLE = "art_title";
		public static final String ART_HAS_IMAGE = "art_has_image";
		public static final String ART_MAKER = "art_principalOrFirstMaker";
		public static final String ART_lONG_TITLE = "art_long_title";
		public static final String ART_SHOW_IMAGE = "art_show_image";
		public static final String ART_PERMIT_DOWNLOAD = "art_permit_download";
		public static final String ART_WEB_IMAGE = "art_web_image";
		public static final String ART_WEB_IMAGE_WIDTH = "art_web_image_width";
		public static final String ART_WEB_IMAGE_HEIGHT = "art_web_image_height";

		// create content uri
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_ARTS).build();

		// create cursor of base type directory for multiple entries
		public static final String CONTENT_DIR_TYPE =
		ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_ARTS;

		// create cursor of base type item for single entry
		public static final String CONTENT_ITEM_TYPE =
			ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_ARTS;

		// for building URIs on insertion
		public static Uri buildUri(long id){
        		return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
}
