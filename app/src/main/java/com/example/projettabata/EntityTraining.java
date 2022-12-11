package com.example.projettabata;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

// Table des entrainements.
@Entity(tableName = "entityTraining")
public class EntityTraining implements Serializable {
    // Identifiant
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Nom
    @ColumnInfo(name = "name")
    public String name;

    // Temps de préparation (en secondes)
    @ColumnInfo(name = "preparation")
    public long preparation;

    // Nombre de cycles principaux
    @ColumnInfo(name = "main_cycle")
    public long mainCycle;

    // Nombre de sous-cycles
    @ColumnInfo(name = "sub_cycle")
    public long subCycle;

    // Temps de travail (en secondes)
    @ColumnInfo(name = "work")
    public long work;

    // Temps de repos (en secondes)
    @ColumnInfo(name = "rest")
    public long rest;

    // Temps de repose long (en secondes)
    @ColumnInfo(name = "long_rest")
    public long longRest;

    /**
     * Retourne l'identifiant.
     * @return Identifiant.
     */
    public long getId() {
        return id;
    }

    /**
     * Définit l'identifiant.
     * @param id Identifiant.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Retourne le nom.
     * @return Nom.
     */
    public String getName() {
        return name;
    }
}
