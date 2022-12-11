package com.example.projettabata;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Database {

    // Instance de la base de données
    private static Database instance;

    // Pour le builder Room
    private final AppDatabase appDatabase;

    /**
     * Constructeur de la base de données.
     * @param context Contexte de l'application (l'activité).
     */
    private Database(final Context context) {
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "Tabata").addCallback(roomDatabaseCallback).build();
    }

    /**
     * Récupération de l'instance.
     * @param context Contexte.
     * @return Instance crée ou sauvegardée.
     */
    public static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context);
        }
        return instance;
    }

    /**
     * Récupération de l'instance Room liée à la base de données.
     * @return Instance Room.
     */
    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // Colonnes : name | preparation | main_cycle | sub_cycle | work | rest | long_rest
            db.execSQL("INSERT INTO entityTraining (name, preparation, main_cycle, sub_cycle, work, rest, long_rest) VALUES(\"Pompes\", 30, 2, 3, 30, 30, 90);");
            db.execSQL("INSERT INTO entityTraining (name, preparation, main_cycle, sub_cycle, work, rest, long_rest) VALUES(\"Course\", 30, 3, 2, 20, 20, 60);");
        }
    };
}
