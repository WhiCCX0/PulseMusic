package com.hardcodecoder.pulsemusic.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;

import com.hardcodecoder.pulsemusic.activities.DetailsActivity;

public class NavigationUtil {

    public static void goToAlbum(@NonNull Activity activity, @Nullable View sharedView, @NonNull String albumName, long albumId, @NonNull String albumArt) {
        Intent i = new Intent(activity, DetailsActivity.class);
        i.putExtra(DetailsActivity.ALBUM_ID, albumId);
        i.putExtra(DetailsActivity.KEY_ITEM_CATEGORY, DetailsActivity.CATEGORY_ALBUM);
        i.putExtra(DetailsActivity.KEY_TITLE, albumName);
        i.putExtra(DetailsActivity.KEY_ART_URL, albumArt);
        if (null != sharedView) {
            String transitionName = sharedView.getTransitionName();
            i.putExtra(DetailsActivity.KEY_TRANSITION_NAME, transitionName);
            Bundle b = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedView, transitionName).toBundle();
            activity.startActivity(i, b);
        }
    }

    public static void goToArtist(@NonNull Activity activity, @Nullable View sharedView, @NonNull String artistName) {
        Intent i = new Intent(activity, DetailsActivity.class);
        i.putExtra(DetailsActivity.KEY_ITEM_CATEGORY, DetailsActivity.CATEGORY_ARTIST);
        i.putExtra(DetailsActivity.KEY_TITLE, artistName);
        if (null != sharedView) {
            String transitionName = sharedView.getTransitionName();
            i.putExtra(DetailsActivity.KEY_TRANSITION_NAME, transitionName);
            Bundle b = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedView, transitionName).toBundle();
            activity.startActivity(i, b);
        }
    }
}
