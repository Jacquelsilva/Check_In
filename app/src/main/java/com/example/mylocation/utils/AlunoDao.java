package com.example.mylocation.utils;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mylocation.models.Aluno;

@Dao
public interface AlunoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void inserir(Aluno aluno);

    @Query("SELECT * FROM alunos WHERE matricula = :matricula LIMIT 1")
    Aluno buscarPorMatricula(String matricula);

    @Query("SELECT COUNT(*) FROM alunos")
    int contar();
}
