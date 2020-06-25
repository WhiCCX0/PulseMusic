package com.hardcodecoder.pulsemusic.shortcuts.types;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hardcodecoder.pulsemusic.shortcuts.ShortcutsLauncher;

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public abstract class BaseShortcutType {

    static final String ID_PREFIX = "com.hardcodecoder.pulsemusic.shortcuts.types.id.";
    private Context mContext;

    BaseShortcutType(Context context) {
        mContext = context;
    }

    public static String getId() {
        return ID_PREFIX.concat("invalid");
    }

    Intent getShortcutIntent(int shortcutType) {
        Intent intent = new Intent(mContext, ShortcutsLauncher.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra(ShortcutsLauncher.KEY_SHORTCUT_TYPE, shortcutType);
        return intent;
    }

    public abstract ShortcutInfo getShortcutInfo();
}
