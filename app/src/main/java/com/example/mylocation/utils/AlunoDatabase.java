package com.example.mylocation.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.mylocation.models.Aluno;

import java.util.concurrent.Executors;

public class AlunoDatabase {

    public interface Callback {
        void onResult(Aluno aluno);
    }

    public static void buscarPorMatricula(Context context, String matricula, Callback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Aluno aluno = AppDatabase.getInstance(context)
                    .alunoDao()
                    .buscarPorMatricula(matricula);

            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(aluno));
        });
    }

    public static void salvarAluno(Context context, String matricula, String nome, String email, Callback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Aluno novoAluno = new Aluno(
                    matricula,
                    nome,
                    "",
                    0,
                    "",
                    "",
                    ""
            );

            AppDatabase.getInstance(context)
                    .alunoDao()
                    .inserir(novoAluno);

            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(novoAluno));
        });
    }
}