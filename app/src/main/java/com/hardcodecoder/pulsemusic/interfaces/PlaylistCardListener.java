package com.hardcodecoder.pulsemusic.interfaces;

public interface PlaylistCardListener {

    /**
     * called upon recycler view item click
     *
     * @param pos passes the adapter position of the clicked item
     */
    void onItemClick(int pos);

    /**
     * called when user clicks on edit button
     *
     * @param pos passes the adapter position
     */
    void onItemEdit(int pos);
}

