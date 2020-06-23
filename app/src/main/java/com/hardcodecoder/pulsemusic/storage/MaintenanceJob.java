package com.hardcodecoder.pulsemusic.storage;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.hardcodecoder.pulsemusic.TaskRunner;

import java.io.File;

public class MaintenanceJob extends JobService {

    private static final String TAG = "MaintenanceJob";

    @Override
    public boolean onStartJob(JobParameters params) {
        TaskRunner.executeAsync(() -> {
            String hisToryDir = StorageStructure.getAbsoluteHistoryPath(getFilesDir().getAbsolutePath());
            File[] files = new File(hisToryDir).listFiles();
            int size;
            if (null != files && (size = files.length) > 20) {
                // Sorts in descending order by modified date
                StorageUtils.sortFiles(files);
                File[] deleteFiles = new File[size - 20];
                System.arraycopy(files, 20, deleteFiles, 0, size - 20);
                for (File deleteFile : deleteFiles) {
                    if (!deleteFile.delete())
                        Log.e(TAG, "Unable to delete file: " + deleteFile.getAbsolutePath());
                }
            }
            jobFinished(params, false);
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
