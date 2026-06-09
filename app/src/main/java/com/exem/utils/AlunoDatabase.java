package com.example.mylocation.utils;

import android.content.Context;
import com.example.mylocation.models.Aluno;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AlunoDatabase {

    public interface Callback {
        void onResult(Aluno aluno);
    }


    public static void buscarPorMatricula(Context context, String matricula, Callback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Aluno aluno = AppDatabase.getInstance(context)
                    .alunoDao()
                    .buscarPorMatricula(matricula);

            new android.os.Handler(android.os.Looper.getMainLooper())
                    .post(() -> callback.onResult(aluno));
        });
    }
}