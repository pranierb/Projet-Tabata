package com.example.projettabata;

import static com.example.projettabata.TrainingFormat.secondsFormat;
import static java.lang.Math.floor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Modify extends AppCompatActivity {

    // Popup pour les erreurs
    private Popup popup;
    // Identifiant de l'entrainement édité (null si on est en mode création)
    private Long editedId = null;

    /**
     * Initialisation de la modification de l'entrainement.
     * @param savedInstanceState Activité.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        popup = new Popup((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE));

        EntityTraining entityTraining = (EntityTraining)getIntent().getSerializableExtra("ENTITY");
        if (entityTraining != null) {
            ((TextView)findViewById(R.id.txtTrainingName)).setText(entityTraining.name);
            editedId = entityTraining.id;
            ((EditText)findViewById(R.id.inpName)).setText(entityTraining.name);
            ((EditText)findViewById(R.id.inpPreparation)).setText(secondsFormat(entityTraining.preparation));
            ((EditText)findViewById(R.id.inpMainCycle)).setText(String.format(Locale.getDefault(), "%02d", entityTraining.mainCycle));
            ((EditText)findViewById(R.id.inpSubCycle)).setText(String.format(Locale.getDefault(), "%02d", entityTraining.subCycle));
            ((EditText)findViewById(R.id.inpWork)).setText(secondsFormat(entityTraining.work));
            ((EditText)findViewById(R.id.inpRest)).setText(secondsFormat(entityTraining.rest));
            ((EditText)findViewById(R.id.inpLongRest)).setText(secondsFormat(entityTraining.longRest));
        }
        else {
            ((TextView)findViewById(R.id.txtTrainingName)).setText(getResources().getString(R.string.modify_new));
        }
    }

    /**
     * Compte un nombre de caractères dans une chaîne de caractères.
     * @param target Chaîne de caractères.
     * @param toFind Caractère.
     * @return Nombre de caractères.
     */
    public int countInStr(String target, char toFind) {
        int result = 0;

        for (int i = 0; i < target.length(); i++) {
            if (target.charAt(i) == toFind) {
                result++;
            }
        }

        return result;
    }

    /**
     * Indique si la chaîne de caractères est uniquement composé de chiffres.
     * @param target Chaîne de caractères.
     * @return Booléen indiquant la réponse au nom de la fonction.
     */
    public boolean isNotFullNumbers(String target) {
        return !target.matches("^[0-9]*$");
    }

    /**
     * Corrige et vérifie un temps mm:ss ou ssss.
     * @param time Temps.
     * getCorrectTime("12:34")  // Retourne "12:34"
     * getCorrectTime("130")    // Retourne "02:10"
     * getCorrectTime(":42")    // Retourne "00:42"
     * getCorrectTime("12:")    // Retourne "12:00"
     * getCorrectTime("99:59")  // Retourne "99:59" car le maximum en minutes et secondes
     * getCorrectTime("5999")   // Retourne "99:59" car le maximum en secondes
     * getCorrectTime("ABC")    // Retourne null car ne peut contenir de lettres
     * getCorrectTime(":")      // Retourne null car les minutes et secondes ne sont pas spécifiées
     * getCorrectTime("00:00")  // Retourne null car il faut un minimum de durée
     * getCorrectTime("100:60") // Retourne null car ne peut dépasser à partir 100 minutes et/ou 60 secondes
     * getCorrectTime("6000")   // Retourne null car ne peut dépasser 6000 secondes lorsqu'on demande uniquement des secondes
     * @return Temps possiblement inchangé ou null en cas d'erreur.
     */
    public String getCorrectTime(String time) {
        time = time.trim();
        // On vérifie si on peut séparer tel que minutes:secondes
        int nbSeparators = countInStr(time, ':');
        if (nbSeparators != 1) {
            if (isNotFullNumbers(time)) {
                return null;
            }
            // L'utilisateur n'a entré que des secondes
            int seconds = Integer.parseInt(time);
            if (seconds == 0) {
                return null;
            }
            int minutes = seconds / 60;
            seconds = (int) (seconds - (floor(minutes) * 60));
            if (minutes > 99) {
                return null;
            }
            // Format correcte du temps
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
        else if (time.length() == 1) {
            return null;
        }
        String[] timeScalesStr = time.split(":");
        if (timeScalesStr[0].isEmpty()) { // Si on ne spécifie pas les minutes
            timeScalesStr[0] = "0";
        }
        // On vérifie si on peut convertir en nombres
        for (String timeScaleStr : timeScalesStr) {
            if (isNotFullNumbers(timeScaleStr)) {
                return null;
            }
        }
        // Vérification des valeurs
        int[] timeScalesInt = new int[2];
        if (time.charAt(time.length() - 1) != ':') {
            timeScalesInt[0] = Integer.parseInt(timeScalesStr[0]);
            timeScalesInt[1] = Integer.parseInt(timeScalesStr[1]);
        }
        else { // Si on ne spécifie pas les secondes
            timeScalesInt[0] = Integer.parseInt(timeScalesStr[0]);
        }
        if (timeScalesInt[0] > 99) {
            // Vérification de la limite supérieur des minutes
            return null;
        }
        else if ((timeScalesInt[1] > 59)) {
            // Vérification de la limite supérieur des secondes
            return null;
        }
        else if ((timeScalesInt[1] < 1) && (timeScalesInt[0] == 0)) {
            // Vérification de la limite inférieure des minutes
            return null;
        }
        // Format correcte du temps
        return String.format(Locale.getDefault(), "%02d:%02d", timeScalesInt[0], timeScalesInt[1]);
    }

    /**
     * Convertit un temps mm:ss en secondes.
     * @param time Temps.
     * @return Secondes.
     */
    public long timeToSeconds(String time) {
        String[] timeStr = time.split(":");
        return (Integer.parseInt(timeStr[0]) * 60L) + Integer.parseInt(timeStr[1]);
    }

    /**
     * Corrige et vérifie un nombre de cycles allant de 1 à 99.
     * @param cycle Nombre de cycles.
     * getCorrectTime("12")    // Retourne "12"
     * getCorrectTime("1")     // Retourne "01"
     * getCorrectTime("99")    // Retourne "99"
     * getCorrectTime(" 012 ") // Retourne "12"
     * getCorrectTime("0")     // Retourne null car on ne peut pas aller en dessous de 1
     * getCorrectTime("100")   // Retourne null car on ne peut pas aller au dessus de 99
     * getCorrectTime("ABC")   // Retourne null car on n'accepte pas de lettres
     * @return Nombre de cycles possiblement inchangé ou null en cas d'erreur.
     */
    public String getCorrectCycle(String cycle) {
        cycle = cycle.trim();
        // On vérifie si on peut convertir en nombres
        if (isNotFullNumbers(cycle)) {
            return null;
        }
        // Vérification des limites
        int cycleInt = Integer.parseInt(cycle);
        if ((cycleInt < 1) || (cycleInt > 99)) {
            return null;
        }
        // Format correcte des cycles
        return String.format(Locale.getDefault(), "%02d", cycleInt);
    }

    /**
     * Obtient l'entrée d'un utilisateur.
     * @param id Id du champ éditable.
     * @return Entrée de l'utilisateur.
     */
    public String getUserInput(int id) {
        return ((EditText)findViewById(id)).getText().toString();
    }

    /**
     * Revenir à la liste des entrainements
     * @param view Vue.
     */
    public void btnBackOnClick(View view) {
        finish();
    }

    /**
     * Sauvegarde un entrainement.
     * @param entityTraining Entrainement.
     */
    private void saveTraining(EntityTraining entityTraining) {
        class SaveTraining extends AsyncTask<Void, Void, EntityTraining> {

            @Override
            protected EntityTraining doInBackground(Void... voids) {

                Database db = Database.getInstance(getApplicationContext());
                if (editedId == null) { // Si on est création
                    db.getAppDatabase().entityTrainingDao().insert(entityTraining);
                }
                else { // Si on est modification
                    entityTraining.id = editedId;
                    db.getAppDatabase().entityTrainingDao().update(entityTraining);
                }

                return entityTraining;
            }
        }

        SaveTraining st = new SaveTraining();
        st.execute();
    }

    /**
     * Confirmation des changements (ajout ou modification d'un entrainement)
     * @param view Vue.
     */
    public void btnConfirmOnClick(View view) {
        String name = getUserInput(R.id.inpName);
        if (name.length() > 20) {
            popup.popupWindow(view, "Nom trop long (20 caractères maximum) !");
            return;
        }
        else if (name.isEmpty()) {
            popup.popupWindow(view, "Nom vide !");
            return;
        }
        String preparation = getCorrectTime(getUserInput(R.id.inpPreparation));
        if (preparation == null) {
            popup.popupWindow(view, "Préparation invalide !");
            return;
        }
        String mainCycle = getCorrectCycle(getUserInput(R.id.inpMainCycle));
        if (mainCycle == null) {
            popup.popupWindow(view, "Cycles principaux invalide !");
            return;
        }
        String subCycle = getCorrectCycle(getUserInput(R.id.inpSubCycle));
        if (subCycle == null) {
            popup.popupWindow(view, "Cycles secondaires invalide !");
            return;
        }
        String work = getCorrectTime(getUserInput(R.id.inpWork));
        if (work == null) {
            popup.popupWindow(view, "Travail invalide !");
            return;
        }
        String rest = getCorrectTime(getUserInput(R.id.inpRest));
        if (rest == null) {
            popup.popupWindow(view, "Repos invalide !");
            return;
        }
        String longRest = getCorrectTime(getUserInput(R.id.inpLongRest));
        if (longRest == null) {
            popup.popupWindow(view, "Long repos invalide !");
            return;
        }

        EntityTraining entityTraining = new EntityTraining();
        entityTraining.name = name;
        entityTraining.preparation = timeToSeconds(preparation);
        entityTraining.mainCycle = Integer.parseInt(mainCycle);
        entityTraining.subCycle = Integer.parseInt(subCycle);
        entityTraining.work = timeToSeconds(work);
        entityTraining.rest = timeToSeconds(rest);
        entityTraining.longRest = timeToSeconds(longRest);
        saveTraining(entityTraining);
        finish();
    }
}