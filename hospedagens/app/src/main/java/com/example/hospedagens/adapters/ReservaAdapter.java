package com.example.hospedagens.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hospedagens.R;
import com.example.hospedagens.data.Reserva;

import java.util.ArrayList;
import java.util.List;

public class ReservaAdapter extends BaseAdapter {
    private Context context;
    private List<Reserva> reservas;
    private LayoutInflater inflater;

    public ReservaAdapter(Context context, List<Reserva> reservas) {
        this.context = context;
        this.reservas = reservas;
        this.inflater = LayoutInflater.from(context);
    }

    public void setReservas(List<Reserva> newReservas) {
        this.reservas = newReservas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return reservas.size();
    }

    @Override
    public Object getItem(int position) {
        return reservas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position; // Or a unique ID from Reserva object
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_reserva, parent, false); // You'll need to create item_reserva.xml
            holder = new ViewHolder();
            holder.tvNomeHospedagem = convertView.findViewById(R.id.tvNomeHospedagem);
            holder.tvCidadeHospedagem = convertView.findViewById(R.id.tvCidadeHospedagem);
            holder.tvDatasReserva = convertView.findViewById(R.id.tvDatasReserva);
            holder.tvNumHospedes = convertView.findViewById(R.id.tvNumHospedes);
            holder.tvValorTotal = convertView.findViewById(R.id.tvValorTotal);
            holder.tvStatus = convertView.findViewById(R.id.tvStatus);
            holder.tvDataReserva = convertView.findViewById(R.id.tvDataReserva);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Reserva reserva = reservas.get(position);
        holder.tvNomeHospedagem.setText(reserva.getHospedagemId());
        holder.tvDatasReserva.setText(String.format("%s - %s", reserva.getDataCheckIn(), reserva.getDataCheckOut()));
        holder.tvNumHospedes.setText(String.format("%d hóspedes", reserva.getNumHospedes()));
        holder.tvValorTotal.setText(String.format("Total: R$ %.2f", reserva.getValorTotal()));

        return convertView;
    }

    static class ViewHolder {
        private TextView tvNomeHospedagem;
        private TextView tvCidadeHospedagem;
        private TextView tvDatasReserva;
        private TextView tvNumHospedes;
        private TextView tvValorTotal;
        private TextView tvStatus;
        private TextView tvDataReserva;
    }
}
