package com.example.mylocation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mylocation.R;
import com.example.mylocation.models.RegistroCheckin;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.ViewHolder> {

    private List<RegistroCheckin> registros;

    public RegistroAdapter(List<RegistroCheckin> registros) {
        this.registros = registros;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_registro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistroCheckin registro = registros.get(position);
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdfData = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        boolean isCheckin = registro.getTipo() == RegistroCheckin.Tipo.CHECKIN;

        holder.tvTipo.setText(isCheckin ? "Check-in" : "Check-out");
        holder.tvHorario.setText(sdfHora.format(registro.getHorario()));
        holder.tvData.setText(sdfData.format(registro.getHorario()));
        holder.tvCoordenadas.setText(registro.getCoordenadas());

        String endereco = registro.getEnderecoObtido();
        if (endereco != null && !endereco.isEmpty()) {
            holder.tvEndereco.setVisibility(View.VISIBLE);
            holder.tvEndereco.setText(endereco);
        } else {
            holder.tvEndereco.setVisibility(View.GONE);
        }

        int cor = isCheckin
                ? ContextCompat.getColor(holder.itemView.getContext(), R.color.verde_confirmacao)
                : ContextCompat.getColor(holder.itemView.getContext(), R.color.vermelho_checkout);

        holder.tvTipo.setTextColor(cor);
        holder.ivIcone.setImageResource(isCheckin ? R.drawable.ic_checkin : R.drawable.ic_checkout);
        holder.ivIcone.setColorFilter(cor);
        holder.viewIndicador.setBackgroundColor(cor);
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipo, tvHorario, tvData, tvCoordenadas, tvEndereco;
        ImageView ivIcone;
        View viewIndicador;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipo = itemView.findViewById(R.id.tv_tipo);
            tvHorario = itemView.findViewById(R.id.tv_horario);
            tvData = itemView.findViewById(R.id.tv_data_registro);
            tvCoordenadas = itemView.findViewById(R.id.tv_coordenadas_registro);
            tvEndereco = itemView.findViewById(R.id.tv_endereco_registro);
            ivIcone = itemView.findViewById(R.id.iv_icone_registro);
            viewIndicador = itemView.findViewById(R.id.view_indicador);
        }
    }
}
