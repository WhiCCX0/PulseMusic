package com.hardcodecoder.pulsemusic.shortcuts;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hardcodecoder.pulsemusic.PMS;
import com.hardcodecoder.pulsemusic.shortcuts.types.LatestShortcutType;
import com.hardcodecoder.pulsemusic.shortcuts.types.ShuffleShortcutType;
import com.hardcodecoder.pulsemusic.shortcuts.types.SuggestedShortcutType;

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class ShortcutsLauncher extends Activity {

    public static final String KEY_SHORTCUT_TYPE = "com.hardcodecoder.pulsemusic.shortcuts.Type";
    public static final int SHORTCUT_TYPE_NONE = -1;
    public static final int SHORTCUT_TYPE_SHUFFLE = 0;
    public static final int SHORTCUT_TYPE_LATEST = 1;
    public static final int SHORTCUT_TYPE_SUGGESTED = 2;
    private static final String TAG = "ShortcutsLauncher";
    private static final int REQUEST_CODE = 6900;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermission();
    }

    private void startShortcutAction() {
        int shortcutType;
        if (null != getIntent() && (shortcutType = getIntent().getIntExtra(KEY_SHORTCUT_TYPE, SHORTCUT_TYPE_NONE)) != SHORTCUT_TYPE_NONE) {
            switch (shortcutType) {
                case SHORTCUT_TYPE_SHUFFLE:
                    AppShortcutsManager.reportShortcutUsed(this, ShuffleShortcutType.getId());
                    startServiceWithAction(PMS.PLAY_SHUFFLE);
                    break;
                case SHORTCUT_TYPE_LATEST:
                    AppShortcutsManager.reportShortcutUsed(this, LatestShortcutType.getId());
                    startServiceWithAction(PMS.PLAY_LATEST);
                    break;
                case SHORTCUT_TYPE_SUGGESTED:
                    AppShortcutsManager.reportShortcutUsed(this, SuggestedShortcutType.getId());
                    startServiceWithAction(PMS.PLAY_SUGGESTED);
                    break;
                default:
                    Log.e(TAG, "Unknown shortcut");
            }
        }
        finish();
    }

    private void startServiceWithAction(int playCode) {
        Intent intent = new Intent(this, PMS.class);
        intent.putExtra(PMS.PLAY_KEY, playCode);
        startService(intent);
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startShortcutAction();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startShortcutAction();
            } else {
                // Permission was not granted
                Toast.makeText(this, "App needs to access device storage to work", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
