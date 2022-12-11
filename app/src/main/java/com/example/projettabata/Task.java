package com.example.projettabata;

import java.util.Locale;

public class Task {

    // Temps (en secondes)
    private final long time;
    // Nom
    private final String name;
    // Position parmis toute les tâches
    private final int iteration;

    /**
     * Constructeur de la tâche.
     * @param time Temps (en secondes).
     * @param name Nom.
     * @param iteration Position.
     */
    public Task(long time, String name, int iteration) {
        this.time = time;
        this.name = name;
        this.iteration = iteration;
    }

    /**
     * Obtient le temps (tel que mm:ss).
     * @return Temps.
     */
    public String getTime() {
        long minutes = (long)Math.floor(this.time / 60);
        long seconds = this.time - (minutes * 60);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    /**
     * Obtient le temps (en secondes).
     * @return Temps.
     */
    public long getSeconds() {
        return this.time;
    }

    /**
     * Obtient le nom.
     * @return Nom.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Obtient la position.
     * @return Position.
     */
    public String getIteration() {
        return String.format(Locale.getDefault(), "%d", this.iteration);
    }
}
