package com.example.hospedagens.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "reservas",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Hospedagem.class,
                        parentColumns = "id",
                        childColumns = "hospedagemId",
                        onDelete = ForeignKey.CASCADE)
        })
public class Reserva {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "userId")
    private int userId;

    @ColumnInfo(name = "hospedagemId")
    private int hospedagemId;

    private String dataCheckIn;
    private String dataCheckOut;
    private int numHospedes;
    private double valorTotal;
    private String status; // "CONFIRMADA", "CANCELADA"
    private String dataReserva;

    // Nome da hospedagem para facilitar exibição
    private String nomeHospedagem;
    private String cidadeHospedagem;

    public Reserva() {}

    public Reserva(int userId, int hospedagemId, String dataCheckIn, String dataCheckOut,
                   int numHospedes, double valorTotal, String nomeHospedagem, String cidadeHospedagem) {
        this.userId = userId;
        this.hospedagemId = hospedagemId;
        this.dataCheckIn = dataCheckIn;
        this.dataCheckOut = dataCheckOut;
        this.numHospedes = numHospedes;
        this.valorTotal = valorTotal;
        this.status = "CONFIRMADA";
        this.dataReserva = getCurrentDateTimeString();
        this.nomeHospedagem = nomeHospedagem;
        this.cidadeHospedagem = cidadeHospedagem;
    }

    private String getCurrentDateTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getHospedagemId() { return hospedagemId; }
    public void setHospedagemId(int hospedagemId) { this.hospedagemId = hospedagemId; }

    public String getDataCheckIn() { return dataCheckIn; }
    public void setDataCheckIn(String dataCheckIn) { this.dataCheckIn = dataCheckIn; }

    public String getDataCheckOut() { return dataCheckOut; }
    public void setDataCheckOut(String dataCheckOut) { this.dataCheckOut = dataCheckOut; }

    public int getNumHospedes() { return numHospedes; }
    public void setNumHospedes(int numHospedes) { this.numHospedes = numHospedes; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDataReserva() { return dataReserva; }
    public void setDataReserva(String dataReserva) { this.dataReserva = dataReserva; }

    public String getNomeHospedagem() { return nomeHospedagem; }
    public void setNomeHospedagem(String nomeHospedagem) { this.nomeHospedagem = nomeHospedagem; }

    public String getCidadeHospedagem() { return cidadeHospedagem; }
    public void setCidadeHospedagem(String cidadeHospedagem) { this.cidadeHospedagem = cidadeHospedagem; }

}
