package com.example.mylocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylocation.utils.AlunoDatabase;
import com.example.mylocation.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etMatricula, etSenha;
    private Button btnEntrar;
    private ProgressBar progressBar;
    private TextView tvErro;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);


        if (sessionManager.estaLogado()) {
            irParaCheckin();
            return;
        }

        inicializarViews();
    }

    private void inicializarViews() {
        etMatricula = findViewById(R.id.et_matricula);
        etSenha     = findViewById(R.id.et_senha);
        btnEntrar   = findViewById(R.id.btn_entrar);
        progressBar = findViewById(R.id.progress_bar);
        tvErro      = findViewById(R.id.tv_erro);

        btnEntrar.setOnClickListener(v -> tentarLogin());
    }

    private void tentarLogin() {
        String matricula = etMatricula.getText().toString().trim();
        String senha     = etSenha.getText().toString().trim();

        tvErro.setVisibility(View.GONE);

        if (matricula.isEmpty()) {
            etMatricula.setError("Informe a matrícula");
            etMatricula.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            etSenha.setError("Informe a senha");
            etSenha.requestFocus();
            return;
        }

        setCarregando(true);

        AlunoDatabase.buscarPorMatricula(this, matricula, aluno -> {
            setCarregando(false);

            if (aluno == null) {
                mostrarErro("Matrícula não encontrada.");
                return;
            }


            if (!senha.equals(matricula)) {
                mostrarErro("Senha incorreta.");
                return;
            }

            sessionManager.salvarSessao(matricula);
            irParaCheckin();
        });
    }

    private void irParaCheckin() {
        Intent intent = new Intent(this, CheckinActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setCarregando(boolean carregando) {
        progressBar.setVisibility(carregando ? View.VISIBLE : View.GONE);
        btnEntrar.setEnabled(!carregando);
    }

    private void mostrarErro(String mensagem) {
        tvErro.setText(mensagem);
        tvErro.setVisibility(View.VISIBLE);
    }
}