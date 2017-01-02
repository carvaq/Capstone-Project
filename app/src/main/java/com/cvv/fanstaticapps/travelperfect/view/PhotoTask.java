package com.cvv.fanstaticapps.travelperfect.view;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Followed suggestions from https://developers.google.com/places/android-api/photos#asyncTask
 */
public abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

    private static final String DIR_PLACES = "places";

    private int mHeight;
    private int mWidth;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;

    public PhotoTask(Context context, GoogleApiClient apiClient, int width) {
        mContext = context;
        mGoogleApiClient = apiClient;
        mWidth = width;
        mHeight = UiUtils.getProportionalHeight(width);
    }

    /**
     * Loads the first photo for a place id from the Geo Data API.
     * The place id must be the first (and only) parameter.
     */
    @Override
    protected AttributedPhoto doInBackground(String... params) {
        if (params.length != 1) {
            return null;
        }
        final String placeId = params[0];
        AttributedPhoto attributedPhoto = null;

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId).await();

        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                // Get the first bitmap and its attributions.
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                CharSequence attribution = photo.getAttributions();
                Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                        .getBitmap();
                String path = saveToInternalStorage(image, placeId);
                attributedPhoto = new AttributedPhoto(attribution, path);
                image.recycle();
            }
            photoMetadataBuffer.release();
        }
        return attributedPhoto;
    }

    //source: http://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
    private String saveToInternalStorage(Bitmap bitmapImage, String placeId) {
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir(DIR_PLACES, Context.MODE_PRIVATE);
        File file = new File(directory, placeId + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSilently(fos);
        }
        return file.getPath();
    }

    private void closeSilently(FileOutputStream fos) {
        try {
            if (fos != null) {
                fos.close();
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Holder for an image and its attribution.
     */
    public class AttributedPhoto {
        public final CharSequence attribution;
        public final String path;

        public AttributedPhoto(CharSequence attribution, String path) {
            this.attribution = attribution;
            this.path = path;
        }
    }
}