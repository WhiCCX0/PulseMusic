package com.hardcodecoder.pulsemusic.shortcuts.types;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.shortcuts.ShortcutIconGenerator;
import com.hardcodecoder.pulsemusic.shortcuts.ShortcutsLauncher;

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class LatestShortcutType extends BaseShortcutType {

    private Context mContext;

    public LatestShortcutType(Context context) {
        super(context);
        mContext = context;
    }

    public static String getId() {
        return ID_PREFIX.concat("latest");
    }

    @Override
    public ShortcutInfo getShortcutInfo() {
        return new ShortcutInfo.Builder(mContext, getId())
                .setShortLabel(mContext.getString(R.string.shortcut_latest_label))
                .setLongLabel(mContext.getString(R.string.shortcut_latest_label_long))
                .setIcon(ShortcutIconGenerator.getThemedIcon(mContext, R.drawable.ic_app_shortcut_latest))
                .setIntent(getShortcutIntent(ShortcutsLauncher.SHORTCUT_TYPE_LATEST))
                .build();
    }
}
