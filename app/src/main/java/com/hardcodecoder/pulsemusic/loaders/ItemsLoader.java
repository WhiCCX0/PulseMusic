package com.hardcodecoder.pulsemusic.loaders;

import android.util.Log;

import com.hardcodecoder.pulsemusic.activities.DetailsActivity;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ItemsLoader implements Callable<List<MusicModel>> {

    private String title;
    private int choice;


    public ItemsLoader(String itemToSearch, int choice) {
        this.title = itemToSearch;
        this.choice = choice;
    }

    @Override
    public List<MusicModel> call() {
        List<MusicModel> listToWorkOn = LoaderCache.getAllTracksList();
        List<MusicModel> listToReturn = null;
        if (null != listToWorkOn) {
            listToReturn = new ArrayList<>();
            if (choice == DetailsActivity.CATEGORY_ALBUM) {
                for (MusicModel md : listToWorkOn) {
                    if (md.getAlbum().contains(title) || md.getAlbum().equals(title))
                        listToReturn.add(md);
                }
            } else if (choice == DetailsActivity.CATEGORY_ARTIST) {
                for (MusicModel md : listToWorkOn)
                    if (/*md.getArtist().contains(title) ||*/ md.getArtist().equals(title))
                        listToReturn.add(md);

            }
            if (listToReturn.size() == 0)
                Log.e("ItemsLoader", "Zero item found");
        }
        return listToReturn;
    }
}
