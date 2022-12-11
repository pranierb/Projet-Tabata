package com.example.projettabata;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface EntityTrainingDao {
    // Obtient tout les entrainements.
    @Query("SELECT * FROM entitytraining")
    List<EntityTraining> getAll();

    // Insert un entrainement.
    @Insert
    void insert(EntityTraining task);

    // Supprime un entrainement
    @Delete
    void delete(EntityTraining entityTraining);

    // Met à jour un entrainement
    @Update
    void update(EntityTraining task);
}
