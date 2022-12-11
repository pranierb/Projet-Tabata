package com.example.projettabata;

import java.util.Locale;

public class TrainingFormat {

    /**
     * Convertit un temps en secondes par un format mm:ss.
     * @param toFormat Temps à formater.
     * @return Temps formaté.
     */
    public static String secondsFormat(long toFormat) {
        long minutes = (long)Math.floor(toFormat / 60);
        long seconds = toFormat - (minutes * 60);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
