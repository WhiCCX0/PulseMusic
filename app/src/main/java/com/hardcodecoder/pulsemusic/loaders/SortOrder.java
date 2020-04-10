package com.hardcodecoder.pulsemusic.loaders;

public enum SortOrder {

    TITLE_ASC,
    TITLE_DESC,
    DATE_MODIFIED_ASC,
    DATE_MODIFIED_DESC;

    public enum ALBUMS {
        TITLE_ASC,
        TITLE_DESC,
        ALBUM_DATE_FIRST_YEAR_ASC,
        ALBUM_DATE_FIRST_YEAR_DESC,
        ALBUM_DATE_LAST_YEAR_ASC,
        ALBUM_DATE_LAST_YEAR_DESC,
    }

    public enum ARTIST {
        TITLE_ASC,
        TITLE_DESC,
        NUM_OF_TRACKS_ASC,
        NUM_OF_TRACKS_DESC,
    }
}
