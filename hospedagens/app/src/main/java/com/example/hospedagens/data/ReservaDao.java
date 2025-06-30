package com.example.hospedagens.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ReservaDao {
    @Insert
    long insert(Reserva reserva);

    @Update
    void update(Reserva reserva);

    @Query("SELECT * FROM reservas WHERE userId = :userId ORDER BY dataReserva DESC")
    List<Reserva> getReservasByUser(int userId);

    @Query("SELECT * FROM reservas WHERE id = :id")
    Reserva getReservaById(int id);

    @Query("SELECT COUNT(*) FROM reservas WHERE hospedagemId = :hospedagemId AND status = 'CONFIRMADA'")
    int countReservasConfirmadasByHospedagem(int hospedagemId);

    @Query("DELETE FROM reservas WHERE id = :id")
    void deleteById(int id);
}
