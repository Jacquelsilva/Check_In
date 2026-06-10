package com.example.mylocation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylocation.utils.AlunoDatabase;
import com.example.mylocation.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CadastroActivity extends AppCompatActivity {

    private TextInputLayout tilNome, tilMatricula, tilEmail, tilSenha, tilConfirmarSenha;
    private TextInputEditText etNome, etMatricula, etEmail, etSenha, etConfirmarSenha;
    private Button btnCadastrar;
    private ProgressBar progressBar;
    private TextView tvErro, tvIrLogin;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        sessionManager = new SessionManager(this);
        inicializarViews();
    }

    private void inicializarViews() {
        tilNome            = findViewById(R.id.til_nome);
        tilMatricula       = findViewById(R.id.til_matricula);
        tilEmail           = findViewById(R.id.til_email);
        tilSenha           = findViewById(R.id.til_senha);
        tilConfirmarSenha  = findViewById(R.id.til_confirmar_senha);

        etNome            = findViewById(R.id.et_nome);
        etMatricula       = findViewById(R.id.et_matricula);
        etEmail           = findViewById(R.id.et_email);
        etSenha           = findViewById(R.id.et_senha);
        etConfirmarSenha  = findViewById(R.id.et_confirmar_senha);

        btnCadastrar = findViewById(R.id.btn_cadastrar);
        progressBar  = findViewById(R.id.progress_bar);
        tvErro       = findViewById(R.id.tv_erro);
        tvIrLogin    = findViewById(R.id.tv_ir_login);

        btnCadastrar.setOnClickListener(v -> tentarCadastro());

        tvIrLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void tentarCadastro() {

        limparErros();
        tvErro.setVisibility(View.GONE);

        String nome           = etNome.getText().toString().trim();
        String matricula      = etMatricula.getText().toString().trim();
        String email          = etEmail.getText().toString().trim();
        String senha          = etSenha.getText().toString().trim();
        String confirmarSenha = etConfirmarSenha.getText().toString().trim();


        if (TextUtils.isEmpty(nome)) {
            tilNome.setError("Informe o nome completo");
            etNome.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(matricula)) {
            tilMatricula.setError("Informe a matrícula");
            etMatricula.requestFocus();
            return;
        }

        if (matricula.length() < 5) {
            tilMatricula.setError("Matrícula deve ter ao menos 5 dígitos");
            etMatricula.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Informe o e-mail");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("E-mail inválido");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(senha)) {
            tilSenha.setError("Informe a senha");
            etSenha.requestFocus();
            return;
        }

        if (senha.length() < 6) {
            tilSenha.setError("A senha deve ter ao menos 6 caracteres");
            etSenha.requestFocus();
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            tilConfirmarSenha.setError("As senhas não coincidem");
            etConfirmarSenha.requestFocus();
            return;
        }

        setCarregando(true);


        AlunoDatabase.buscarPorMatricula(this, matricula, alunoExistente -> {
            if (alunoExistente != null) {
                setCarregando(false);
                tilMatricula.setError("Esta matrícula já está cadastrada");
                etMatricula.requestFocus();
                return;
            }


            AlunoDatabase.salvarAluno(this, matricula, nome, email, aluno -> {
                setCarregando(false);

                if (aluno == null) {
                    mostrarErro("Erro ao criar conta. Tente novamente.");
                    return;
                }


                sessionManager.salvarSessao(matricula);
                irParaCheckin();
            });
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
        btnCadastrar.setEnabled(!carregando);
    }

    private void mostrarErro(String mensagem) {
        tvErro.setText(mensagem);
        tvErro.setVisibility(View.VISIBLE);
    }

    private void limparErros() {
        tilNome.setError(null);
        tilMatricula.setError(null);
        tilEmail.setError(null);
        tilSenha.setError(null);
        tilConfirmarSenha.setError(null);
    }
}