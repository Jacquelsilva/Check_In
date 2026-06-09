package com.example.mylocation.utils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.annotation.NonNull;

import com.example.mylocation.models.Aluno;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Aluno.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AlunoDao alunoDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "mylocation_db"
                            )
                            .addCallback(popularBancoDeDados)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    private static final RoomDatabase.Callback popularBancoDeDados = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                AlunoDao dao = INSTANCE.alunoDao();

                if (dao.contar() == 0) {
                    dao.inserir(new Aluno(
                            "2024001",
                            "João Silva",
                            "Análise e Desenvolvimento de Sistemas",
                            3,
                            "Empresa Exemplo Ltda",
                            "Av. Paulista, 1000 - Bela Vista, São Paulo",
                            "Manhã"
                    ));
                    dao.inserir(new Aluno(
                            "2024002",
                            "Maria Oliveira",
                            "Engenharia de Software",
                            5,
                            "Tech Solutions S.A.",
                            "Rua Augusta, 500 - Consolação, São Paulo",
                            "Tarde"
                    ));
                    dao.inserir(new Aluno(
                            "2024003",
                            "Carlos Souza",
                            "Ciência da Computação",
                            7,
                            "Inovação Digital ME",
                            "Rua Oscar Freire, 200 - Jardins, São Paulo",
                            "Noite"
                    ));
                }
            });
        }
    };
}
