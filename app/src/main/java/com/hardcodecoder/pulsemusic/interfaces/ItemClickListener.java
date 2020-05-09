package com.hardcodecoder.pulsemusic.interfaces;

import android.view.View;

public interface ItemClickListener {

    interface Simple {

        /**
         * called upon recycler view item click
         *
         * @param pos passes the adapter position of the clicked item
         */
        void onItemClick(int pos);

        /**
         * Called when the options button of a recycler view is clicked
         *
         * @param view pases the view for popup menu to work
         * @param pos  passes the position
         */
        void onOptionsClick(View view, int pos);
    }

}
