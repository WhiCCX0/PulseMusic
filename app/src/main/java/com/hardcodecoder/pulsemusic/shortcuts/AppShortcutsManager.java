package com.hardcodecoder.pulsemusic.shortcuts;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.hardcodecoder.pulsemusic.shortcuts.types.LatestShortcutType;
import com.hardcodecoder.pulsemusic.shortcuts.types.ShuffleShortcutType;
import com.hardcodecoder.pulsemusic.shortcuts.types.SuggestedShortcutType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiresApi(Build.VERSION_CODES.N_MR1)
public class AppShortcutsManager {

    private Context mContext;
    private ShortcutManager shortcutManager;

    public AppShortcutsManager(Context context) {
        mContext = context;
        shortcutManager = mContext.getSystemService(ShortcutManager.class);
    }

    static void reportShortcutUsed(Context context, String shortcutId) {
        Objects.requireNonNull(context.getSystemService(ShortcutManager.class)).reportShortcutUsed(shortcutId);
    }

    public void initDynamicShortcuts(boolean recreate) {
        if (recreate)
            shortcutManager.removeAllDynamicShortcuts();
        if (shortcutManager.getDynamicShortcuts().size() == 0)
            shortcutManager.setDynamicShortcuts(getDefaultShortcuts());
    }

    private List<ShortcutInfo> getDefaultShortcuts() {
        return (Arrays.asList(
                new LatestShortcutType(mContext).getShortcutInfo(),
                new SuggestedShortcutType(mContext).getShortcutInfo(),
                new ShuffleShortcutType(mContext).getShortcutInfo()
        ));
    }
}
