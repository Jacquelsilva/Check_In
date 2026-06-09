package com.example.mylocation.utils;

import com.example.mylocation.models.RegistroCheckin;
import java.util.ArrayList;
import java.util.List;


public class CheckinManager {

    private static CheckinManager instance;
    private List<RegistroCheckin> registros = new ArrayList<>();
    private boolean emEstagio = false;

    private CheckinManager() {}

    public static CheckinManager getInstance() {
        if (instance == null) {
            instance = new CheckinManager();
        }
        return instance;
    }

    public void adicionarRegistro(RegistroCheckin registro) {
        registros.add(registro);
        emEstagio = registro.getTipo() == RegistroCheckin.Tipo.CHECKIN;
    }

    public List<RegistroCheckin> getRegistros() {
        return new ArrayList<>(registros);
    }

    public boolean isEmEstagio() {
        return emEstagio;
    }

    public void limpar() {
        registros.clear();
        emEstagio = false;
    }
}
