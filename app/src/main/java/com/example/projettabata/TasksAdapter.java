package com.example.projettabata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class TasksAdapter extends ArrayAdapter<Task> {

    /**
     * Obtient l'adapter de la liste des tâches.
     * @param mCtx Contexte (l'activité concernée).
     * @param taskList Liste des tâches.
     */
    public TasksAdapter(Context mCtx, List<Task> taskList) {
        super(mCtx, R.layout.template_training, taskList);
    }

    /**
     * Obtient la vue (en rapport avec l'activité concernée).
     * @param position Position de la tâche.
     * @param convertView Vue.
     * @param parent Groupe de la vue.
     * @return Ligne contenant la tâche.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Task task = getItem(position);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.template_task, parent, false);

        TextView txtViewTask = rowView.findViewById(R.id.txtViewTask);

        txtViewTask.setText(String.format("%s: %s • %s", task.getTime(), task.getName(), task.getIteration()));

        return rowView;
    }
}
