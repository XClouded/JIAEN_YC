package com.androids.photoalbum.provider;

import android.graphics.Bitmap;
import android.net.Uri;

public abstract class AlbumContent {
    public static final String AUTHORITY = AlbumProvider.ALBUM_AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String RECORD_ID = "_id";

    private static final String[] COUNT_COLUMNS = new String[]{"count(*)"};

    /**
     * no public constructor since this is a utility class
     */
    private AlbumContent() {
    }

    public interface AlbumColumns {
        public static final String ID = "_id";
        public final static String PHOTO_ADDRESS = "photo_address";
        public final static String PHOTO_BITMAP = "photo_bitmap";
        public final static String PRODUCT_ID = "product_id";
        public final static String PHOTO_BYTE = "stream_byte";
        
        public static final int CONTENT_ID_COLUMN = 0;
        public static final int CONTENT_PHOTO_ADDRESS_COLUMN = 1;
        public static final int CONTENT_PHOTO_BITMAP_COLUMN = 2;
        public static final int CONTENT_PRODUCT_ID_COLUMN = 3;
        public static final int CONTENT_STREAM_BYTE_COLUMN = 4;
    }
    
    public static final class Album extends AlbumContent implements AlbumColumns {
        public static final Uri ALBUM_CONTENT_URI = Uri.parse(AlbumContent.CONTENT_URI + "/album");

        public static final String[] CONTENT_PROJECTION = new String[] {
        	AlbumColumns.ID, AlbumColumns.PHOTO_ADDRESS, AlbumColumns.PHOTO_BITMAP, 
        			AlbumColumns.PRODUCT_ID, AlbumColumns.PHOTO_BYTE
        };
    }
}