package com.example.hospedagens.repository;

import android.content.Context;
import android.os.AsyncTask;

import com.example.hospedagens.data.HospedagemDao;
import com.example.hospedagens.data.AppDatabase;
import com.example.hospedagens.data.Hospedagem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HospedagemRepository {
    private HospedagemDao hospedagemDao;
    private ExecutorService executor;

    public HospedagemRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        hospedagemDao = db.hospedagemDao();
        executor = Executors.newFixedThreadPool(4);
    }

    // Interface para callbacks
    public interface HospedagemCallback {
        void onSuccess(Long result);
        void onError(String error);
    }

    public interface HospedagensListCallback {
        void onSuccess(List<Hospedagem> hospedagens);
        void onError(String error);
    }

    public interface HospedagemSingleCallback {
        void onSuccess(Hospedagem hospedagem);
        void onError(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String error);
    }

    // Inserir hospedagem
    public void insert(Hospedagem hospedagem, HospedagemCallback callback) {
        executor.execute(() -> {
            try {
                long id = hospedagemDao.insert(hospedagem);
                callback.onSuccess(id);
            } catch (Exception e) {
                callback.onError("Erro ao inserir hospedagem: " + e.getMessage());
            }
        });
    }

    // Atualizar hospedagem
    public void update(Hospedagem hospedagem, SimpleCallback callback) {
        executor.execute(() -> {
            try {
                hospedagemDao.update(hospedagem);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError("Erro ao atualizar hospedagem: " + e.getMessage());
            }
        });
    }

    // Deletar hospedagem
    public void delete(Hospedagem hospedagem, SimpleCallback callback) {
        executor.execute(() -> {
            try {
                hospedagemDao.delete(hospedagem);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError("Erro ao deletar hospedagem: " + e.getMessage());
            }
        });
    }

    // Buscar hospedagens por anfitrião
    public void getHospedagensByAnfitriao(int anfitriaoId, HospedagensListCallback callback) {
        executor.execute(() -> {
            try {
                List<Hospedagem> hospedagens = hospedagemDao.getHospedagensByAnfitriao(anfitriaoId);
                callback.onSuccess(hospedagens);
            } catch (Exception e) {
                callback.onError("Erro ao buscar hospedagens: " + e.getMessage());
            }
        });
    }

    // Buscar hospedagem por ID
    public void getHospedagemById(int id, HospedagemSingleCallback callback) {
        executor.execute(() -> {
            try {
                Hospedagem hospedagem = hospedagemDao.getHospedagemById(id);
                callback.onSuccess(hospedagem);
            } catch (Exception e) {
                callback.onError("Erro ao buscar hospedagem: " + e.getMessage());
            }
        });
    }

    // Buscar hospedagens disponíveis
    public void getHospedagensDisponiveis(HospedagensListCallback callback) {
        executor.execute(() -> {
            try {
                List<Hospedagem> hospedagens = hospedagemDao.getHospedagensDisponiveis();
                callback.onSuccess(hospedagens);
            } catch (Exception e) {
                callback.onError("Erro ao buscar hospedagens disponíveis: " + e.getMessage());
            }
        });
    }
}
