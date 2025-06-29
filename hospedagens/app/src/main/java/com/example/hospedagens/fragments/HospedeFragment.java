package com.example.hospedagens.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hospedagens.R;
import com.example.hospedagens.MainActivity;
import android.widget.Button;
import android.widget.TextView;

public class HospedeFragment extends Fragment {
    private TextView textWelcome;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospede, container, false);

        textWelcome = view.findViewById(R.id.text_welcome);
        btnLogout = view.findViewById(R.id.btn_logout);

        textWelcome.setText("Bem-vindo, Hóspede!");

        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void logout() {
        ((MainActivity) getActivity()).loadFragment(new LoginFragment());
    }
}