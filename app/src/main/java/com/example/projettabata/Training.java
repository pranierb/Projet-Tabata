package com.example.projettabata;

import static com.example.projettabata.TrainingFormat.secondsFormat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Training extends AppCompatActivity {

    // Éléments de l'interface graphique
    private Button btnBack;
    private TextView txtTotal;
    private Button btnPause;
    private TextView txtInProgress;
    private ListView lstTask;
    private Button btnPrevious;
    private TextView txtProgression;
    private Button btnNext;

    // Popup pour les erreurs
    private Popup popup;
    // Adapteur pour les liste des tâches
    private TasksAdapter adapter;
    // Temps total écoulé
    long totalTime = -1;
    // Temps total de la somme de toute les tâches
    long totalTimeMax;
    // Temps restant de la tâche en cours
    long inProgressTimeLeft;
    // Pour savoir si on est en pause
    boolean paused = false;
    // Liste des tâches
    List<Task> taskList = new ArrayList<Task>();
    // Index de la tâche en cours
    int taskInProgress = 0;
    // Handler pour le compteur total
    final Handler totalHandler = new Handler();
    // Handler pour le compteur en progression
    final Handler inProgressHandler = new Handler();
    // Pour savoir si on commence le compteur
    boolean isFirst = true;

    /**
     * Initialisation de l'entrainement.
     * @param savedInstanceState Activité.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        loadElements();

        adapter = new TasksAdapter(this, new ArrayList<>());
        lstTask.setAdapter(adapter);

        fillTaskLisk();
        adapter.addAll(taskList);

        totalTimeMax = totalTimeFrom(0);
        startTotalTimer();
        startInProgressTimer(taskList.get(0).getSeconds());

        popup = new Popup((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE));
    }

    /**
     * Lors de la sauvegarde de l'instance.
     * @param savedInstanceState Activité.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("totalTime", totalTime);
        savedInstanceState.putLong("inProgressTimeLeft", inProgressTimeLeft);
        savedInstanceState.putBoolean("paused", paused);
        savedInstanceState.putInt("taskInProgress", taskInProgress);
        savedInstanceState.putBoolean("isFirst", isFirst);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        totalTime = savedInstanceState.getLong("totalTime");
        inProgressTimeLeft = savedInstanceState.getLong("inProgressTimeLeft");
        paused = savedInstanceState.getBoolean("paused");
        taskInProgress = savedInstanceState.getInt("taskInProgress");
        isFirst = savedInstanceState.getBoolean("isFirst");
    }

    /**
     * Assigne les variables globales dans la vue.
     */
    public void loadElements() {
        btnBack = findViewById(R.id.btnBack);
        txtTotal = findViewById(R.id.txtTotal);
        btnPause = findViewById(R.id.btnPause);
        txtInProgress = findViewById(R.id.txtInProgress);
        lstTask = findViewById(R.id.lstTask);
        btnPrevious = findViewById(R.id.btnPrevious);
        txtProgression = findViewById(R.id.txtProgression);
        btnNext = findViewById(R.id.btnNext);
    }

    /**
     * Remplit la liste des tâches.
     */
    public void fillTaskLisk() {
        EntityTraining entityTraining = (EntityTraining)getIntent().getSerializableExtra("ENTITY");

        int iteration = 1;

        taskList.add(new Task(entityTraining.preparation, "Préparation", iteration++));
        for (int i = 0; i < entityTraining.mainCycle; i++) {
            for (int j = 0; j < entityTraining.subCycle; j++) {
                taskList.add(new Task(entityTraining.work, "Travail", iteration++));
                if (j != (entityTraining.subCycle - 1)) {
                    taskList.add(new Task(entityTraining.rest, "Repos", iteration++));
                }
            }
            taskList.add(new Task(entityTraining.longRest, "Repos Long", iteration++));
        }
    }

    /**
     * Met à jour le nombre tâches finits.
     */
    public void updateTxtTotal() {
        txtTotal.setText(String.format("%s/%s", secondsFormat(totalTime), secondsFormat(totalTimeMax)));
    }

    /**
     * Commence le timer de l'activité.
     */
    public void startTotalTimer() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                totalTime++;
                updateTxtTotal();
                if (totalTime < totalTimeMax) {
                    totalHandler.postDelayed(this, 1000);
                }
            }
        };
        totalHandler.postDelayed(runnable, 1000);
    }

    /**
     * Assigne une couleur à la tâche actuelle.
     * @param idColor Couleur à assigner.
     */
    public void colorItem(int idColor) {
        if (lstTask.getChildCount() >= lstTask.getCount()) {
            lstTask.getChildAt(taskInProgress).setBackgroundColor(getResources().getColor(idColor));
        }
    }

    /**
     * Définit le texte en rapport avec la progression de l'activité.
     * @param nbFinishedTasks Nombre de tâches finits.
     */
    public void setTxtProgression(int nbFinishedTasks) {
        txtProgression.setText(String.format(Locale.getDefault(), "%d/%d", nbFinishedTasks, taskList.size()));
    }

    /**
     * Définit le texte en rapport avec la progression de la tâche actuelle.
     * @param value Nouvelle valeur du texte.
     */
    public void setTxtInProgress(String value) {
        txtInProgress.setText(value);
        if (taskInProgress < taskList.size()) {
            handleIndications();
        }
        else {
            txtInProgress.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
            playSound(R.raw.win);
        }
    }

    /**
     * Indique si une valeur est entre deux autres valeurs (borned incluses).
     * @param val Valeur à vérifier.
     * @param min Borne inférieur.
     * @param max Born supérieure.
     * @return Booléen répondant au nom de la fonction.
     */
    public boolean isBetween(long val, int min, int max) {
        return (val >= min) && (val <= max);
    }

    /**
     * Joue un son.
     * @param soundId Identifiant du son.
     */
    public void playSound(int soundId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, soundId);
        mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
            mediaPlayer1.reset();
            mediaPlayer1.release();
        });
        mediaPlayer.start();
    }

    /**
     * Gère les indications de couleurs et de sons.
     */
    public void handleIndications() {
        if (inProgressTimeLeft > 5) {
            txtInProgress.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
        }
        if (isBetween(inProgressTimeLeft, 3, 5)) {
            txtInProgress.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
            if (inProgressTimeLeft == 3) {
                playSound(R.raw.low);
            }
        }
        else if (inProgressTimeLeft == 2) {
            txtInProgress.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            playSound(R.raw.low);
        }
        else if (inProgressTimeLeft == 1) {
            txtInProgress.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            playSound(R.raw.high);
        }
    }

    /**
     * Lance le timer de la progression de l'activité.
     * @param inProgressTime Temps jusqu'à la finition.
     */
    public void startInProgressTimer(long inProgressTime) {
        if (isFirst) {
            inProgressTimeLeft = inProgressTime + 1;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isFirst) {
                    colorItem(android.R.color.holo_blue_light);
                    setTxtProgression(0);
                    isFirst = false;
                }
                inProgressTimeLeft--;
                if (inProgressTimeLeft == 0) {
                    colorItem(android.R.color.white);
                    taskInProgress++;
                    setTxtProgression(taskInProgress);
                    if (taskInProgress < taskList.size()) {
                        colorItem(android.R.color.holo_blue_light);
                        inProgressTimeLeft = taskList.get(taskInProgress).getSeconds();
                    }
                    else {
                        setTxtInProgress(getResources().getString(R.string.training_stop));
                        return;
                    }
                }
                setTxtInProgress(secondsFormat(inProgressTimeLeft));

                inProgressHandler.postDelayed(this, 1000);
            }
        };
        inProgressHandler.postDelayed(runnable, 1000);
    }

    /**
     * Calcul le temps total des tâches à partir d'un index.
     * @param start Index de départ.
     * @return Temps total.
     */
    public long totalTimeFrom(int start) {
        long totalTime = 0;
        for (int i = start; i < taskList.size(); i++) {
            totalTime += taskList.get(i).getSeconds();
        }
        return totalTime;
    }

    /**
     * Calcul le temps total des tâches jusqu'à un certain index.
     * @param end Index de fin.
     * @return Temps total.
     */
    public long totalTimeTo(int end) {
        long totalTime = 0;
        end++;
        for (int i = 0; i < end; i++) {
            totalTime += taskList.get(i).getSeconds();
        }
        return totalTime;
    }

    /**
     * Change les timers par rapport au nombre donné.
     * @param view Vue.
     * @param offset Valeur de décalage (+1 aller à la tâche suivant, -1 pour aller à la tâche précédente).
     */
    public void changeTimers(View view, int offset) {
        taskInProgress += offset;
        if (taskInProgress < 0) {
            popup.popupWindow(view, "Pas assez de tâches précédentes !");
            taskInProgress -= offset;
            return;
        }
        else if (taskInProgress == taskList.size()) {
            totalHandler.removeCallbacksAndMessages(null);
            inProgressHandler.removeCallbacksAndMessages(null);
            taskInProgress -= offset;
            colorItem(android.R.color.white);
            taskInProgress += offset;
            inProgressTimeLeft = 0;
            totalTime = totalTimeMax;
            setTxtProgression(taskList.size());
            updateTxtTotal();
            setTxtInProgress(getResources().getString(R.string.training_stop));
            return;
        }
        else if (taskInProgress > taskList.size()) {
            popup.popupWindow(view, "Pas assez de tâches suivantes !");
            taskInProgress -= offset;
            return;
        }
        if (totalTime != totalTimeMax) {
            totalHandler.removeCallbacksAndMessages(null);
            inProgressHandler.removeCallbacksAndMessages(null);
            taskInProgress -= offset;
            colorItem(android.R.color.white);
            taskInProgress += offset;
        }
        else {
            taskInProgress -= offset + 1;
            colorItem(android.R.color.white);
            taskInProgress += offset + 1;
        }
        colorItem(android.R.color.holo_blue_light);
        inProgressTimeLeft = taskList.get(taskInProgress).getSeconds();
        totalTime = totalTimeTo(taskInProgress - 1);
        setTxtProgression(taskInProgress);
        setTxtInProgress(secondsFormat(inProgressTimeLeft));
        updateTxtTotal();
        if (!paused) {
            startTotalTimer();
            startInProgressTimer(inProgressTimeLeft);
        }
    }

    /**
     * Revenir à la liste des entrainements
     * @param view Vue.
     */
    public void btnBackOnClick(View view) {
        finish();
    }

    /**
     * Mise en pause ou continuation de l'entrainement en cours.
     * @param view Vue.
     */
    public void btnPauseOnClick(View view) {
        if (!paused) {
            btnPause.setText(getResources().getString(R.string.training_pause_on));
            paused = true;
            totalHandler.removeCallbacksAndMessages(null);
            inProgressHandler.removeCallbacksAndMessages(null);
        }
        else {
            btnPause.setText(getResources().getString(R.string.training_pause_off));
            paused = false;
            startTotalTimer();
            startInProgressTimer(inProgressTimeLeft);
        }
    }

    /**
     * Retour à la partie précédente de l'entrainement en cours
     * @param view Vue.
     */
    public void btnPreviousOnClick(View view) {
        changeTimers(view, -1);
    }

    /**
     * Passage à la partie suivante de l'entrainement en cours
     * @param view Vue.
     */
    public void btnNextOnClick(View view) {
        changeTimers(view, +1);
    }
}
