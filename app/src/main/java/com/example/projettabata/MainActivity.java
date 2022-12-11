package com.example.projettabata;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Instance de la base de données
    private Database db;
    // Adapteur de la liste des entrainements
    private TrainingsAdapter adapter;
    // Liste des entrainements
    private ListView listTraining;
    // L'entrainement sélectionné
    private EntityTraining entityTrainingSelected = null;
    // Dernière position sélectionnée
    private Integer lastPositionSelected = null;
    // Popup pour les erreurs.
    private Popup popup;

    /**
     * Initialisation du menu de choix des entrainements.
     * @param savedInstanceState Activité.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Database.getInstance(getApplicationContext());
        popup = new Popup((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE));

        listTraining = findViewById(R.id.lstTask);

        adapter = new TrainingsAdapter(this, new ArrayList<>());
        listTraining.setAdapter(adapter);

        listTraining.setOnItemClickListener((parent, view, position, id) -> {
            if (lastPositionSelected != null) {
                listTraining.getChildAt(lastPositionSelected).setBackgroundColor(getResources().getColor(android.R.color.white));
            }
            if ((lastPositionSelected == null) || (lastPositionSelected != position)) {
                listTraining.getChildAt(position).setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                entityTrainingSelected = adapter.getItem(position);
                lastPositionSelected = position;
            }
            else {
                entityTrainingSelected = null;
                lastPositionSelected = null;
            }
        });

        listTraining.setOnItemLongClickListener((adapterView, view, position, id) -> {
            entityTrainingSelected = adapter.getItem(position);
            goToTrainingScreen(entityTrainingSelected);
            return false;
        });
    }

    private void getTrainings() {
        class GetTrainings extends AsyncTask<Void, Void, List<EntityTraining>> {

            @Override
            protected List<EntityTraining> doInBackground(Void... voids) {
                return db.getAppDatabase().entityTrainingDao().getAll();
            }

            @Override
            protected void onPostExecute(List<EntityTraining> entityTrainings) {
                super.onPostExecute(entityTrainings);

                adapter.clear();
                adapter.addAll(entityTrainings);

                adapter.notifyDataSetChanged();
            }
        }

        GetTrainings gt = new GetTrainings();
        gt.execute();
    }

    private void deleteTraining(EntityTraining entityTraining) {
        class DeleteTraining extends AsyncTask<Void, Void, EntityTraining> {

            @Override
            protected EntityTraining doInBackground(Void... voids) {

                Database db = Database.getInstance(getApplicationContext());
                db.getAppDatabase().entityTrainingDao().delete(entityTraining);

                return entityTraining;
            }
        }

        DeleteTraining dt = new DeleteTraining();
        dt.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getTrainings();
    }

    // Aller à l'écran d'affichage pour créer un nouvel entrainement
    public void goToTrainingScreen() {
        goToTrainingScreen(null);
    }

    // Aller à l'écran d'affichage pour modifier un entrainement
    public void goToTrainingScreen(EntityTraining entityTraining) {
        Intent TrainingViewActivityIntent = new Intent(MainActivity.this, Modify.class);
        TrainingViewActivityIntent.putExtra("ENTITY", entityTraining);
        startActivity(TrainingViewActivityIntent);
    }

    public void startTraining(EntityTraining entityTraining) {
        Intent TrainingViewActivityIntent = new Intent(MainActivity.this, Training.class);
        TrainingViewActivityIntent.putExtra("ENTITY", entityTraining);
        startActivity(TrainingViewActivityIntent);
    }

    // Création d'un entrainement
    public void btnCreateOnClick(View view) {
        goToTrainingScreen();
    }

    // Suppression de l'entrainement
    public void btnDelOnClick(View view) {
        if (entityTrainingSelected != null) {
            deleteTraining(entityTrainingSelected);
            adapter.remove(entityTrainingSelected);
        }
        else {
            popup.popupWindow(view, "Aucun entrainement sélectionné !");
            entityTrainingSelected = null;
            lastPositionSelected = null;
        }
    }

    // Modification de l'entrainement
    public void btnModOnClick(View view) {
        if (entityTrainingSelected != null) {
            goToTrainingScreen(entityTrainingSelected);
        }
        else {
            popup.popupWindow(view, "Aucun entrainement sélectionné !");
        }
    }

    // Lancement de l'entrainement
    public void btnStartOnClick(View view) {
        if (entityTrainingSelected != null) {
            startTraining(entityTrainingSelected);
        }
        else {
            popup.popupWindow(view, "Aucun entrainement sélectionné !");
        }
    }
}
