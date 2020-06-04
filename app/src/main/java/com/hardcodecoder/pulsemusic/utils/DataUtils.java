package com.hardcodecoder.pulsemusic.utils;

import java.text.DecimalFormat;

public class DataUtils {

    public static String getFormattedFileSize(long fileSize) {
        if (fileSize <= 0)
            return "0";
        final String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroup = (int) (Math.log10(fileSize) / Math.log10(1024));
        return new DecimalFormat("#,###.#").format(fileSize / Math.pow(1024, digitGroup)) + " " + units[digitGroup];
    }

    public static String getFormattedBitRate(int bitrate) {
        if (bitrate <= 0)
            return "0";
        return bitrate / 1000 + " Kbps";
    }

    public static String getFormattedSampleRate(int sampleRate) {
        if (sampleRate <= 0)
            return "0";
        return sampleRate + " Hz";
    }
}
