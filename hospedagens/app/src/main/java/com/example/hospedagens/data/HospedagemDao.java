package com.example.hospedagens.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hospedagens.data.Hospedagem;

import java.util.List;

@Dao
public interface HospedagemDao {
    @Insert
    long insert(Hospedagem hospedagem);

    @Update
    void update(Hospedagem hospedagem);

    @Delete
    void delete(Hospedagem hospedagem);

    @Query("SELECT * FROM hospedagens WHERE anfitriaoId = :anfitriaoId ORDER BY titulo ASC")
    List<Hospedagem> getHospedagensByAnfitriao(int anfitriaoId);

    @Query("SELECT * FROM hospedagens WHERE id = :id")
    Hospedagem getHospedagemById(int id);

    @Query("SELECT * FROM hospedagens WHERE disponivel = 1 ORDER BY titulo ASC")
    List<Hospedagem> getHospedagensDisponiveis();

    @Query("SELECT * FROM hospedagens WHERE cidade LIKE '%' || :cidade || '%' AND disponivel = 1 ORDER BY titulo ASC")
    List<Hospedagem> buscarHospedagensPorCidade(String cidade);

    @Query("SELECT * FROM hospedagens ORDER BY titulo ASC")
    List<Hospedagem> getAllHospedagens();

    @Query("DELETE FROM hospedagens WHERE anfitriaoId = :anfitriaoId")
    void deleteHospedagensByAnfitriao(int anfitriaoId);
}
