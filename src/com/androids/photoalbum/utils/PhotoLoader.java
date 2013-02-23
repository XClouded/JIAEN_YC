/*********************************************************************
 *  ____                      _____      _                           *
 * / ___|  ___  _ __  _   _  | ____|_ __(_) ___ ___ ___  ___  _ __   *
 * \___ \ / _ \| '_ \| | | | |  _| | '__| |/ __/ __/ __|/ _ \| '_ \  *
 *  ___) | (_) | | | | |_| | | |___| |  | | (__\__ \__ \ (_) | | | | *
 * |____/ \___/|_| |_|\__, | |_____|_|  |_|\___|___/___/\___/|_| |_| *
 *                    |___/                                          *
 *                                                                   *
 *********************************************************************
 * Copyright 2010 Sony Ericsson Mobile Communications Japan, Inc.    *
 * All rights, including trade secret rights, reserved.              *
 *********************************************************************/
package com.androids.photoalbum.utils;

import android.widget.ImageView;

public interface PhotoLoader {
    /**
     * Gets the cached photo of the specified id and sets to
     * <code>aImageView</code>.
     * @param view The ImageView to set photo
     * @param photoId photo id
     * @return true if photo exists in cache.
     */
    public boolean getCachedPhoto(ImageView view, long photoId);

    /**
     * Sends the message to fetch photo on background thread.
     * @param view the image view to set fetched photo
     */
    public void sendFetchImageMessage(ImageView view);

    /**
     * Sends the message to fetch photo for all image views missing photo.
     */
    public void processMissingImageItems();

    /**
     * Turn on the force refresh flags of photo cache.
     * the cached photos will be reloaded next time.
     * @since eDream 2.0 [DMS00885341] 2010/08/12
     */
    public void setUpForceRefreshPhotos();

    /**
     * Clear cache of images.
     */
    public void clearImageCache();

    /**
     * Stop the image fetching for all contacts, if one is in progress we'll
     * not query the database.
     */
    public void clearImageFetching();
}
