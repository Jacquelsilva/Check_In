package com.example.mylocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.mylocation.utils.AlunoDatabase;
import com.example.mylocation.utils.SessionManager;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText etMatricula, etSenha;
    private Button btnEntrar;
    private ProgressBar progressBar;
    private TextView tvErro;

    private SessionManager sessionManager;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        credentialManager = CredentialManager.create(this);

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

        com.google.android.gms.common.SignInButton btnGoogle = findViewById(R.id.btn_google);
        btnGoogle.setSize(com.google.android.gms.common.SignInButton.SIZE_WIDE);
        btnGoogle.setOnClickListener(v -> iniciarLoginGoogle());

        TextView tvIrCadastro = findViewById(R.id.tv_ir_cadastro);
        tvIrCadastro.setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class))
        );
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

    private void iniciarLoginGoogle() {
        tvErro.setVisibility(View.GONE);

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        setCarregando(true);

        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse response) {
                        runOnUiThread(() -> {
                            setCarregando(false);
                            handleCredential(response);
                        });
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        runOnUiThread(() -> {
                            setCarregando(false);
                            mostrarErro("Falha no login com Google: " + e.getMessage());
                        });
                    }
                }
        );
    }

    private void handleCredential(GetCredentialResponse response) {
        Credential credential = response.getCredential();

        if (credential instanceof CustomCredential) {
            CustomCredential customCred = (CustomCredential) credential;

            if (customCred.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                try {
                    GoogleIdTokenCredential googleCred =
                            GoogleIdTokenCredential.createFrom(customCred.getData());

                    String email = googleCred.getId();
                    sessionManager.salvarSessao(email != null ? email : "google_user");
                    irParaCheckin();

                } catch (Exception e) {
                    mostrarErro("Erro ao processar credencial Google: " + e.getMessage());
                }
            } else {
                mostrarErro("Tipo de credencial não suportado: " + customCred.getType());
            }

        } else if (credential instanceof GoogleIdTokenCredential) {
            GoogleIdTokenCredential googleCred = (GoogleIdTokenCredential) credential;
            String email = googleCred.getId();
            sessionManager.salvarSessao(email != null ? email : "google_user");
            irParaCheckin();

        } else {
            mostrarErro("Credencial desconhecida: " + credential.getClass().getSimpleName());
        }
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