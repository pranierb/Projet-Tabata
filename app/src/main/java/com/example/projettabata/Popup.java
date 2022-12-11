package com.example.projettabata;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Popup extends AppCompatActivity {

    // Inflater de l'activité ou la popup est utilisée.
    LayoutInflater inflater;

    /**
     * Constructeur de la popup.
     * @param inflater Inflater de l'activité.
     */
    public Popup(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    /**
     * @brief Affiche une popup avec un message.
     * @param view Vue concernée par la popup.
     * @param message Message contenu dans la popup.
     */
    public void popupWindow(View view, String message) {
        View popupView = this.inflater.inflate(R.layout.popup_window, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        ((TextView)popupView.findViewById(R.id.popupText)).setText(message);
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
