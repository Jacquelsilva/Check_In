package com.example.mylocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mylocation.models.Aluno;
import com.example.mylocation.models.RegistroCheckin;
import com.example.mylocation.utils.AlunoDatabase;
import com.example.mylocation.utils.CheckinManager;
import com.example.mylocation.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckinActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 100;

    private TextView tvNomeAluno, tvCurso, tvEmpresa, tvEnderecoEmpresa;
    private TextView tvTurno, tvStatus, tvLocalizacao, tvCoordenadas, tvData;
    private TextView tvIniciaisAvatar;
    private Button btnCheckin, btnCheckout;
    private MaterialCardView cardLocalizacao;
    private ImageView ivStatusLoc;

    private FusedLocationProviderClient fusedLocationClient;
    private SessionManager sessionManager;
    private CheckinManager checkinManager;
    private Aluno alunoLogado;

    private double latitudeAtual = 0;
    private double longitudeAtual = 0;
    private String enderecoAtual = "";
    private boolean localizacaoObtida = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        sessionManager = new SessionManager(this);
        checkinManager = CheckinManager.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String matricula = sessionManager.getMatricula();

        AlunoDatabase.buscarPorMatricula(this, matricula, aluno -> {
            if (aluno == null) {
                sessionManager.encerrarSessao();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
            alunoLogado = aluno;
            inicializarViews();
            configurarToolbar();
            preencherDadosAluno();
            atualizarEstadoBotoes();
            solicitarLocalizacao();
        });
    }

    private void inicializarViews() {
        tvNomeAluno = findViewById(R.id.tv_nome_aluno);
        tvCurso = findViewById(R.id.tv_curso);
        tvEmpresa = findViewById(R.id.tv_empresa);
        tvEnderecoEmpresa = findViewById(R.id.tv_endereco_empresa);
        tvTurno = findViewById(R.id.tv_turno);
        tvStatus = findViewById(R.id.tv_status);
        tvLocalizacao = findViewById(R.id.tv_localizacao);
        tvCoordenadas = findViewById(R.id.tv_coordenadas);
        tvData = findViewById(R.id.tv_data);
        tvIniciaisAvatar = findViewById(R.id.tv_iniciais_avatar);
        btnCheckin = findViewById(R.id.btn_checkin);
        btnCheckout = findViewById(R.id.btn_checkout);
        cardLocalizacao = findViewById(R.id.card_localizacao);
        ivStatusLoc = findViewById(R.id.iv_status_loc);

        btnCheckin.setOnClickListener(v -> confirmarCheckin());
        btnCheckout.setOnClickListener(v -> confirmarCheckout());
    }

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Check-in de Estágio");
        }
    }

    private void preencherDadosAluno() {
        tvIniciaisAvatar.setText(alunoLogado.getIniciais());
        tvNomeAluno.setText(alunoLogado.getNome());
        tvCurso.setText(alunoLogado.getCurso() + " · " + alunoLogado.getSemestre() + "º Sem.");
        tvEmpresa.setText(alunoLogado.getEmpresa());
        tvEnderecoEmpresa.setText(alunoLogado.getEnderecoEmpresa());
        tvTurno.setText(alunoLogado.getTurno());

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
        tvData.setText(capitalize(sdf.format(new Date())));
    }

    private void solicitarLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            obterLocalizacao();
        }
    }

    private void obterLocalizacao() {
        tvLocalizacao.setText("Obtendo localização via GPS...");
        tvCoordenadas.setText("");
        ivStatusLoc.setImageResource(R.drawable.ic_status_amarelo);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitudeAtual = location.getLatitude();
                        longitudeAtual = location.getLongitude();
                        enderecoAtual = obterEnderecoPorCoordenadas(latitudeAtual, longitudeAtual);
                        localizacaoObtida = true;

                        String coords = String.format(Locale.US, "%.5f, %.5f", latitudeAtual, longitudeAtual);
                        tvLocalizacao.setText(enderecoAtual.isEmpty() ? "Localização obtida" : enderecoAtual);
                        tvCoordenadas.setText(coords);
                        ivStatusLoc.setImageResource(R.drawable.ic_status_verde);

                        atualizarEstadoBotoes();
                    } else {
                        tvLocalizacao.setText("Não foi possível obter a localização. Tente novamente.");
                        ivStatusLoc.setImageResource(R.drawable.ic_status_vermelho);
                    }
                })
                .addOnFailureListener(e -> {
                    tvLocalizacao.setText("Erro ao obter localização: " + e.getMessage());
                    ivStatusLoc.setImageResource(R.drawable.ic_status_vermelho);
                });
    }

    private String obterEnderecoPorCoordenadas(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, new Locale("pt", "BR"));
        try {
            List<Address> enderecos = geocoder.getFromLocation(lat, lng, 1);
            if (enderecos != null && !enderecos.isEmpty()) {
                Address address = enderecos.get(0);
                StringBuilder sb = new StringBuilder();
                if (address.getThoroughfare() != null) sb.append(address.getThoroughfare());
                if (address.getSubLocality() != null) sb.append(", ").append(address.getSubLocality());
                if (address.getLocality() != null) sb.append(", ").append(address.getLocality());
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void confirmarCheckin() {
        if (!localizacaoObtida) {
            Toast.makeText(this, "Aguarde a localização ser obtida.", Toast.LENGTH_SHORT).show();
            return;
        }

        String horarioFormatado = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        new AlertDialog.Builder(this)
                .setTitle("Confirmar Check-in")
                .setMessage("Confirmar entrada no estágio?\n\nHorário: " + horarioFormatado +
                        "\nLocal: " + (enderecoAtual.isEmpty() ?
                        String.format(Locale.US, "%.5f, %.5f", latitudeAtual, longitudeAtual) : enderecoAtual))
                .setPositiveButton("Confirmar", (dialog, which) -> registrarCheckin())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarCheckout() {
        String horarioFormatado = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        new AlertDialog.Builder(this)
                .setTitle("Confirmar Check-out")
                .setMessage("Confirmar saída do estágio?\n\nHorário: " + horarioFormatado)
                .setPositiveButton("Confirmar", (dialog, which) -> registrarCheckout())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void registrarCheckin() {
        RegistroCheckin registro = new RegistroCheckin(
                RegistroCheckin.Tipo.CHECKIN,
                new Date(),
                latitudeAtual,
                longitudeAtual,
                enderecoAtual
        );
        checkinManager.adicionarRegistro(registro);
        atualizarEstadoBotoes();
        Toast.makeText(this, "✅ Check-in registrado com sucesso!", Toast.LENGTH_LONG).show();
    }

    private void registrarCheckout() {
        RegistroCheckin registro = new RegistroCheckin(
                RegistroCheckin.Tipo.CHECKOUT,
                new Date(),
                latitudeAtual,
                longitudeAtual,
                enderecoAtual
        );
        checkinManager.adicionarRegistro(registro);
        atualizarEstadoBotoes();
        Toast.makeText(this, "👋 Check-out registrado. Até amanhã!", Toast.LENGTH_LONG).show();
    }

    private void atualizarEstadoBotoes() {
        boolean emEstagio = checkinManager.isEmEstagio();

        if (emEstagio) {
            btnCheckin.setVisibility(View.GONE);
            btnCheckout.setVisibility(View.VISIBLE);
            tvStatus.setText("Em estágio");
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.verde_confirmacao));
        } else {
            btnCheckin.setVisibility(View.VISIBLE);
            btnCheckout.setVisibility(View.GONE);
            btnCheckin.setEnabled(localizacaoObtida);
            tvStatus.setText(localizacaoObtida ? "Pronto para check-in" : "Aguardando localização...");
            tvStatus.setTextColor(ContextCompat.getColor(this,
                    localizacaoObtida ? R.color.azul_primario : R.color.cinza_texto));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checkin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_historico) {
            startActivity(new Intent(this, com.example.mylocation.activities.HistoricoActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_atualizar_loc) {
            localizacaoObtida = false;
            atualizarEstadoBotoes();
            solicitarLocalizacao();
            return true;
        }
        if (item.getItemId() == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Sair")
                    .setMessage("Deseja encerrar a sessão?")
                    .setPositiveButton("Sair", (d, w) -> {
                        sessionManager.encerrarSessao();
                        checkinManager.limpar();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obterLocalizacao();
            } else {
                tvLocalizacao.setText("Permissão de localização negada. Vá em Configurações para habilitar.");
                ivStatusLoc.setImageResource(R.drawable.ic_status_vermelho);
                Toast.makeText(this, "Permissão de localização é necessária para o check-in.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}