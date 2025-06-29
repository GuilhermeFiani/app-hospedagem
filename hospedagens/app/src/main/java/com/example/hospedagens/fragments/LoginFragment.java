package com.example.hospedagens.fragments;

import com.example.hospedagens.R;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hospedagens.MainActivity;
import com.example.hospedagens.data.AppDatabase;
import com.example.hospedagens.data.User;
import com.example.hospedagens.utils.PasswordUtils;

public class LoginFragment extends Fragment {

    private EditText editEmail, editPassword;
    private Button btnLogin, btnRegister;
    private AppDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        database = AppDatabase.getDatabase(getContext());

        editEmail = view.findViewById(R.id.edit_email);
        editPassword = view.findViewById(R.id.edit_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(v -> performLogin());
        btnRegister.setOnClickListener(v -> navigateToRegister());

        return view;
    }

    private void performLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordHash = PasswordUtils.hashPassword(password);
        User user = database.userDao().login(email, passwordHash);

        if (user != null) {
            navigateToUserHome(user.getUserType(), user.getId());
        } else {
            Toast.makeText(getContext(), "Credenciais inválidas", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToUserHome(String userType, int userId) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putInt("userId", userId);
        if ("anfitrião".equals(userType)) {
            fragment = new AnfitriaoFragment();
        } else {
            fragment = new HospedeFragment();
        }
        fragment.setArguments(bundle);
        ((MainActivity) getActivity()).loadFragment(fragment);
    }

    private void navigateToRegister() {
        ((MainActivity) getActivity()).loadFragment(new RegisterFragment());
    }
}