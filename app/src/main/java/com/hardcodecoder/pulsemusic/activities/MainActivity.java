package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.HomeBottomSheetFragment;
import com.hardcodecoder.pulsemusic.fragments.main.AlbumsFragment;
import com.hardcodecoder.pulsemusic.fragments.main.ArtistFragment;
import com.hardcodecoder.pulsemusic.fragments.main.ControlsFragment;
import com.hardcodecoder.pulsemusic.fragments.main.HomeFragment;
import com.hardcodecoder.pulsemusic.fragments.main.LibraryFragment;
import com.hardcodecoder.pulsemusic.fragments.main.PlaylistFragment;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;

public class MainActivity extends MediaSessionActivity {

    public static final String TAG = "MainActivity";
    private static final String HOME = "HomeFragment";
    private static final String LIBRARY = "LibraryFragment";
    private static final String ALBUMS = "AlbumsFragment";
    private static final String PLAYLIST_CARDS = "PlaylistCardFragment";
    private static final String ARTIST = "ArtistFragment";
    private static final String ACTIVE = "ActiveFragment";
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment homeFrag = null;
    private Fragment libraryFrag = null;
    private Fragment artistFrag = null;
    private Fragment albumsFrag = null;
    private Fragment activeFrag = null;
    private Fragment playlistCardFrag = null;
    private Fragment controlsFrag = null;
    private final MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onMetadataChanged(@Nullable MediaMetadata metadata) {
            showControlsFragment();
        }
    };
    private AppBarLayout mAppBar;
    private MediaController mController;
    @StyleRes
    private int mCurrentTheme;
    @StyleRes
    private int mCurrentAccent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCurrentTheme = ThemeManagerUtils.getThemeToApply();
        mCurrentAccent = ThemeManagerUtils.getAccentStyleToApply();

        super.onCreate(null); // Pass null to prevent restoration of fragments on activity recreate
        setContentView(R.layout.activity_main);
        setUpToolbar();
        if (LoaderCache.getAllTracksList() == null) {
            LoaderHelper.loadAllTracks(getContentResolver(), result -> setUpMainContents(savedInstanceState));
        } else setUpMainContents(savedInstanceState);
    }


    private void setUpToolbar() {
        mAppBar = findViewById(R.id.main_app_bar);
        MaterialToolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            HomeBottomSheetFragment homeBottomSheetFragment = HomeBottomSheetFragment.newInstance();
            homeBottomSheetFragment.show(getSupportFragmentManager(), HomeBottomSheetFragment.TAG);
        });
        toolbar.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
    }

    private void setUpMainContents(Bundle savedInstanceState) {
        if (savedInstanceState == null) switchFragment(homeFrag, HOME);
        else switchFragment(activeFrag, savedInstanceState.getString(ACTIVE, HOME));
        setUpBottomNavigationView();
    }

    private void setUpBottomNavigationView() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_nav_bar);
        bottomNavigation.setOnNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.nav_home:
                    if (activeFrag != homeFrag) {
                        switchFragment(homeFrag, HOME);
                    }
                    break;
                case R.id.nav_library:
                    if (activeFrag != libraryFrag) {
                        switchFragment(libraryFrag, LIBRARY);
                    }
                    break;
                case R.id.nav_playlist:
                    if (activeFrag != playlistCardFrag) {
                        switchFragment(playlistCardFrag, PLAYLIST_CARDS);
                    }
                    break;
                case R.id.nav_album:
                    if (activeFrag != albumsFrag) {
                        switchFragment(albumsFrag, ALBUMS);
                    }
                    break;
                case R.id.nav_artist:
                    if (activeFrag != artistFrag) {
                        switchFragment(artistFrag, ARTIST);
                    }
                    break;
            }
            return true;
        });
    }

    private void switchFragment(Fragment switchTo, String tag) {
        if (null == switchTo) {
            switch (tag) {

                case HOME:
                    homeFrag = HomeFragment.getInstance();
                    switchTo = homeFrag;
                    break;

                case LIBRARY:
                    libraryFrag = LibraryFragment.getInstance();
                    switchTo = libraryFrag;
                    break;

                case PLAYLIST_CARDS:
                    playlistCardFrag = PlaylistFragment.getInstance();
                    switchTo = playlistCardFrag;
                    break;

                case ALBUMS:
                    albumsFrag = AlbumsFragment.getInstance();
                    switchTo = albumsFrag;
                    break;

                case ARTIST:
                    artistFrag = ArtistFragment.getInstance();
                    switchTo = artistFrag;
                    break;

                default:
                    Log.e(TAG, "SwitchTo fragment is not a member of defined fragments");
            }

            if (switchTo != null && activeFrag != null) {
                fm.beginTransaction()
                        .add(R.id.fragment_container, switchTo, tag)
                        .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit)
                        .hide(activeFrag)
                        .show(switchTo)
                        .commit();
            } else if (switchTo != null) {
                fm.beginTransaction()
                        .add(R.id.fragment_container, switchTo, tag)
                        .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit)
                        .show(switchTo)
                        .commit();
            }
        } else
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit)
                    .hide(activeFrag)
                    .show(switchTo)
                    .commit();
        activeFrag = switchTo;
        mAppBar.setExpanded(true);
    }

    private void showControlsFragment() {
        if (controlsFrag == null) {
            controlsFrag = new ControlsFragment();
            findViewById(R.id.controls_fragment_container).setVisibility(View.VISIBLE);
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up_enter, R.anim.slide_down_exit)
                    .replace(R.id.controls_fragment_container, controlsFrag)
                    .commit();
        }
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
        mController = controller;
        mController.registerCallback(mCallback);

        if (mController.getMetadata() != null)
            showControlsFragment();
    }

    @Override
    protected void onStart() {
        if ((mCurrentTheme != ThemeManagerUtils.getThemeToApply()) || (mCurrentAccent != ThemeManagerUtils.getAccentStyleToApply())) {
            supportInvalidateOptionsMenu();
            recreate();
        }
        super.onStart();
        if (null != mController) {
            mController.registerCallback(mCallback);
            if (null != mController.getMetadata())
                showControlsFragment();
        }
    }

    @Override
    protected void onStop() {
        if (null != mController)
            mController.unregisterCallback(mCallback);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACTIVE, activeFrag.getTag());
    }
}
