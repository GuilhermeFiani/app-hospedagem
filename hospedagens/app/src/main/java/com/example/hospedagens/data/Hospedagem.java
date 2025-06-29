package com.example.hospedagens.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "hospedagens",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "anfitriaoId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("anfitriaoId")})
public class Hospedagem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int anfitriaoId; // FK para o usuário anfitrião
    private String titulo;
    private String descricao;
    private String cidade;
    private String endereco;
    private double precoPorNoite;
    private String imagemUrl;
    private boolean disponivel;
    private int capacidadeMaxima;
    private String tipoImovel; // Casa, Apartamento, Quarto, etc.

    // Construtor
    public Hospedagem(int anfitriaoId, String titulo, String descricao, String cidade,
                      String endereco, double precoPorNoite, int capacidadeMaxima, String tipoImovel) {
        this.anfitriaoId = anfitriaoId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.cidade = cidade;
        this.endereco = endereco;
        this.precoPorNoite = precoPorNoite;
        this.capacidadeMaxima = capacidadeMaxima;
        this.tipoImovel = tipoImovel;
        this.disponivel = true;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnfitriaoId() {
        return anfitriaoId;
    }

    public void setAnfitriaoId(int anfitriaoId) {
        this.anfitriaoId = anfitriaoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public double getPrecoPorNoite() {
        return precoPorNoite;
    }

    public void setPrecoPorNoite(double precoPorNoite) {
        this.precoPorNoite = precoPorNoite;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public String getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(String tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    @Override
    public String toString() {
        return titulo + " - " + cidade + " (R$ " + String.format("%.2f", precoPorNoite) + "/noite)";
    }
}
