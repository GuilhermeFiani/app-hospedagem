package com.example.hospedagens.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hospedagens.R;
import com.example.hospedagens.data.Hospedagem;

import java.util.List;

public class HospedagemAdapter extends BaseAdapter {
    private Context context;
    private List<Hospedagem> hospedagens;
    private LayoutInflater inflater;

    public HospedagemAdapter(Context context, List<Hospedagem> hospedagens) {
        this.context = context;
        this.hospedagens = hospedagens;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return hospedagens.size();
    }

    @Override
    public Object getItem(int position) {
        return hospedagens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return hospedagens.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_hospedagem, parent, false);
            holder = new ViewHolder();
            holder.tvTitulo = convertView.findViewById(R.id.tvTitulo);
            holder.tvCidade = convertView.findViewById(R.id.tvCidade);
            holder.tvPreco = convertView.findViewById(R.id.tvPreco);
            holder.tvTipoImovel = convertView.findViewById(R.id.tvTipoImovel);
            holder.tvStatus = convertView.findViewById(R.id.tvStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Hospedagem hospedagem = hospedagens.get(position);

        holder.tvTitulo.setText(hospedagem.getTitulo());
        holder.tvCidade.setText(hospedagem.getCidade());
        holder.tvPreco.setText(String.format("R$ %.2f/noite", hospedagem.getPrecoPorNoite()));
        holder.tvTipoImovel.setText(hospedagem.getTipoImovel());

        // Status de disponibilidade
        if (hospedagem.isDisponivel()) {
            holder.tvStatus.setText("Disponível");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.accent_color));
        } else {
            holder.tvStatus.setText("Indisponível");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.secondary_color));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvTitulo;
        TextView tvCidade;
        TextView tvPreco;
        TextView tvTipoImovel;
        TextView tvStatus;
    }
}
