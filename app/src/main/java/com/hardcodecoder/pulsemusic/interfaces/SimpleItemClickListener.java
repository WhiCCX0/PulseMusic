package com.hardcodecoder.pulsemusic.interfaces;


public interface SimpleItemClickListener {
    /**
     * called upon recycler view item click
     *
     * @param position passes the adapter position of the clicked item
     */
    void onItemClick(int position);

    /**
     * Called when the options button of a recycler view is clicked
     *
     * @param position passes the adapter position of the clicked item
     */
    void onOptionsClick(int position);
}
