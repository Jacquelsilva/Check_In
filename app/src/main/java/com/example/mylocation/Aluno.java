package com.example.mylocation.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "alunos")
public class Aluno {

    @PrimaryKey
    @NonNull
    private String matricula;
    private String nome;
    private String curso;
    private int semestre;
    private String empresa;
    private String enderecoEmpresa;
    private String turno;

    public Aluno(@NonNull String matricula, String nome, String curso, int semestre,
                 String empresa, String enderecoEmpresa, String turno) {
        this.matricula = matricula;
        this.nome = nome;
        this.curso = curso;
        this.semestre = semestre;
        this.empresa = empresa;
        this.enderecoEmpresa = enderecoEmpresa;
        this.turno = turno;
    }

    @NonNull public String getMatricula() { return matricula; }
    public String getNome() { return nome; }
    public String getCurso() { return curso; }
    public int getSemestre() { return semestre; }
    public String getEmpresa() { return empresa; }
    public String getEnderecoEmpresa() { return enderecoEmpresa; }
    public String getTurno() { return turno; }
    public void setMatricula(@NonNull String matricula) { this.matricula = matricula; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCurso(String curso) { this.curso = curso; }
    public void setSemestre(int semestre) { this.semestre = semestre; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public void setEnderecoEmpresa(String enderecoEmpresa) { this.enderecoEmpresa = enderecoEmpresa; }
    public void setTurno(String turno) { this.turno = turno; }

    public String getIniciais() {
        if (nome == null || nome.isEmpty()) return "?";
        String[] partes = nome.trim().split(" ");
        if (partes.length == 1) return partes[0].substring(0, 1).toUpperCase();
        return (partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1)).toUpperCase();
    }
}