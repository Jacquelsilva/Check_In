package com.example.mylocation.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "CheckinEstagioSession";
    private static final String KEY_MATRICULA = "matricula";
    private static final String KEY_LOGADO = "logado";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void salvarSessao(String matricula) {
        editor.putBoolean(KEY_LOGADO, true);
        editor.putString(KEY_MATRICULA, matricula);
        editor.apply();
    }

    public boolean estaLogado() {
        return prefs.getBoolean(KEY_LOGADO, false);
    }

    public String getMatricula() {
        return prefs.getString(KEY_MATRICULA, null);
    }

    public void encerrarSessao() {
        editor.clear();
        editor.apply();
    }
}
