package com.hardcodecoder.pulsemusic.loaders;


import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class AlbumsLoader implements Callable<List<AlbumModel>> {

    private SortOrder.ALBUMS mSortOrder;

    AlbumsLoader(SortOrder.ALBUMS sortOrder) {
        mSortOrder = sortOrder;
    }

    @Override
    public List<AlbumModel> call() {
        List<MusicModel> musicModelList = LoaderCache.getAllTracksList();
        List<AlbumModel> albumsList = null;
        if (null != musicModelList && musicModelList.size() > 0) {
            albumsList = new ArrayList<>();
            Set<String> albumSet = new HashSet<>();
            for (MusicModel md : musicModelList) {
                String albumName = md.getAlbum();
                if (!albumSet.contains(albumName)) {
                    int f = 0;
                    for (String setAlbumName : albumSet) {
                        if (albumName.contains(setAlbumName) || setAlbumName.contains(albumName)) {
                            albumSet.add(albumName);
                            f = 1;
                            break;
                        }
                    }
                    if (f == 0) {
                        albumSet.add(albumName);
                        albumsList.add(new AlbumModel(md.getId(), 0, md.getAlbumId(), albumName, md.getAlbumArtUrl()));
                    }
                }
            }
            Collections.sort(albumsList, (o1, o2) -> o1.getAlbumName().compareTo(o2.getAlbumName()));
            if (mSortOrder == SortOrder.ALBUMS.TITLE_DESC)
                Collections.reverse(albumsList);
        }
        return albumsList;
    }
}
