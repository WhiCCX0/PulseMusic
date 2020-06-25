package com.hardcodecoder.pulsemusic.shortcuts.types;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.shortcuts.ShortcutIconGenerator;
import com.hardcodecoder.pulsemusic.shortcuts.ShortcutsLauncher;

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class ShuffleShortcutType extends BaseShortcutType {

    private Context mContext;

    public ShuffleShortcutType(Context context) {
        super(context);
        mContext = context;
    }


    public static String getId() {
        return ID_PREFIX.concat("shuffle");
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    public ShortcutInfo getShortcutInfo() {
        return new ShortcutInfo.Builder(mContext, getId())
                .setShortLabel(mContext.getString(R.string.shortcut_shuffle_label))
                .setLongLabel(mContext.getString(R.string.shortcut_shuffle_label_long))
                .setIcon(ShortcutIconGenerator.getThemedIcon(mContext, R.drawable.ic_app_shortcut_shuffle))
                .setIntent(getShortcutIntent(ShortcutsLauncher.SHORTCUT_TYPE_SHUFFLE))
                .build();
    }
}
