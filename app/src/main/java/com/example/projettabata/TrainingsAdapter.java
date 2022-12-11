package com.example.projettabata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class TrainingsAdapter extends ArrayAdapter<EntityTraining> {

    /**
     * Obtient l'adapter de la liste des entrainements.
     * @param mCtx Contexte (l'activité concernée).
     * @param trainingList Liste des entrainements.
     */
    public TrainingsAdapter(Context mCtx, List<EntityTraining> trainingList) {
        super(mCtx, R.layout.template_training, trainingList);
    }

    /**
     * Obtient la vue (en rapport avec l'activité concernée).
     * @param position Position de l'entrainement.
     * @param convertView Vue.
     * @param parent Groupe de la vue.
     * @return Ligne contenant l'entrainement.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final EntityTraining entityTraining = getItem(position);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.template_training, parent, false);

        TextView txtViewTraining = rowView.findViewById(R.id.txtViewTraining);

        txtViewTraining.setText(entityTraining.getName());

        return rowView;
    }
}
