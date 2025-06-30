package com.example.hospedagens.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hospedagens.R;
import com.example.hospedagens.MainActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.hospedagens.adapters.HospedagemAdapter;
import com.example.hospedagens.adapters.ReservaAdapter;
import com.example.hospedagens.data.AppDatabase;
import com.example.hospedagens.data.Hospedagem;
import com.example.hospedagens.data.Reserva;
import com.example.hospedagens.repository.HospedagemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HospedeFragment extends Fragment {
    private TextView textWelcome;
    private Button btnLogout;
    private HospedagemAdapter hospedagemAdapter;
    private ReservaAdapter reservaAdapter;
    private HospedagemRepository repository;
    private AppDatabase database;
    private ExecutorService executor;
    private int currentUserId;
    private Button btnToggleView;
    private TextView tvSectionTitle;
    private boolean showingHospedagens = true;
    private ListView listViewHospedagens;
    private ListView listViewReservas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospede, container, false);

        textWelcome = view.findViewById(R.id.tvWelcome);
        btnLogout = view.findViewById(R.id.btn_logout);
        listViewHospedagens = view.findViewById(R.id.listViewHospedagens);
        listViewReservas = view.findViewById(R.id.listViewReservas);
        btnToggleView = view.findViewById(R.id.btnToggleView);
        tvSectionTitle = view.findViewById(R.id.tvSectionTitle);
        textWelcome.setText("Bem-vindo, Hóspede!");

        reservaAdapter = new ReservaAdapter(getContext(), new ArrayList<>());
        listViewReservas.setAdapter(reservaAdapter);
        listViewReservas.setVisibility(View.GONE);

        setupClickListeners();
        loadHospedagens();

        btnLogout.setOnClickListener(v -> logout());

        listViewHospedagens.setOnItemClickListener((parent, view1, position, id) -> {
            Hospedagem hospedagem = (Hospedagem) hospedagemAdapter.getItem(position);
            onHospedagemClick(hospedagem);
        });

        return view;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = AppDatabase.getDatabase(getContext());
        repository = new HospedagemRepository(getContext());
        executor = Executors.newSingleThreadExecutor();

        // Obter ID do usuário logado (você precisa implementar isso baseado no seu sistema de login)
        currentUserId = getCurrentUserId();
    }

    private void setupClickListeners() {
        btnToggleView.setOnClickListener(v -> {
            if (showingHospedagens) {
                showReservas();
            } else {
                showHospedagens();
            }
        });
    }

    private void showHospedagens() {
        showingHospedagens = true;
        listViewHospedagens.setVisibility(View.VISIBLE); // Use listViewHospedagens
        listViewReservas.setVisibility(View.GONE);       // Use listViewReservas
        btnToggleView.setText("Ver Minhas Reservas");
        tvSectionTitle.setText("Hospedagens Disponíveis");
        loadHospedagens();
    }

    private void showReservas() {
        showingHospedagens = false;
        listViewHospedagens.setVisibility(View.GONE);
        listViewReservas.setVisibility(View.VISIBLE);
        btnToggleView.setText("Ver Hospedagens");
        tvSectionTitle.setText("Minhas Reservas");
        loadReservas();
    }

    private void loadHospedagens() {
        repository.getHospedagensDisponiveis(new HospedagemRepository.HospedagensListCallback() {
            @Override
            public void onSuccess(List<Hospedagem> hospedagensDisponiveis) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Cria um novo adapter com a lista atualizada
                        HospedagemAdapter adapter = new HospedagemAdapter(getContext(), hospedagensDisponiveis);
                        listViewHospedagens.setAdapter(adapter);

                        // Clique em item
                        listViewHospedagens.setOnItemClickListener((parent, view, position, id) -> {
                            Hospedagem hospedagem = (Hospedagem) adapter.getItem(position);
                            onHospedagemClick(hospedagem);
                        });
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void loadReservas() {
        executor.execute(() -> {
            List<Reserva> reservas = database.reservaDao().getReservasByUser(currentUserId);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    reservaAdapter.setReservas(reservas);
                });
            }
        });
    }

    private void onHospedagemClick(Hospedagem hospedagem) {
        // Navegar para o fragment de fazer reserva, se desejar
        FazerReservaFragment fragment = FazerReservaFragment.newInstance(hospedagem.getId(), currentUserId);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private int getCurrentUserId() {
        return getContext().getSharedPreferences("user_prefs", 0)
                .getInt("current_user_id", -1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }

    private void logout() {
        ((MainActivity) getActivity()).loadFragment(new LoginFragment());
    }
}