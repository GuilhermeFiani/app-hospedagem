package com.example.hospedagens.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hospedagens.R;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.hospedagens.MainActivity;
import com.example.hospedagens.data.AppDatabase;
import com.example.hospedagens.data.User;
import com.example.hospedagens.utils.PasswordUtils;

public class RegisterFragment extends Fragment {

    private EditText editName, editEmail, editPassword;
    private RadioGroup radioGroupUserType;
    private Button btnRegister, btnBackToLogin;
    private AppDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        database = AppDatabase.getDatabase(getContext());

        editName = view.findViewById(R.id.edit_name);
        editEmail = view.findViewById(R.id.edit_email);
        editPassword = view.findViewById(R.id.edit_password);
        radioGroupUserType = view.findViewById(R.id.radio_group_user_type);
        btnRegister = view.findViewById(R.id.btn_register);
        btnBackToLogin = view.findViewById(R.id.btn_back_to_login);

        btnRegister.setOnClickListener(v -> performRegister());
        btnBackToLogin.setOnClickListener(v -> navigateToLogin());

        return view;
    }

    private void performRegister() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = radioGroupUserType.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(getContext(), "Selecione o tipo de usuário", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadio = getView().findViewById(selectedId);
        String userType = selectedRadio.getText().toString().toLowerCase();
        System.out.println(userType);

        // Verifica se usuário já existe
        User existingUser = database.userDao().getUserByEmail(email);
        if (existingUser != null) {
            Toast.makeText(getContext(), "Email já cadastrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordHash = PasswordUtils.hashPassword(password);
        User newUser = new User(email, passwordHash, name, userType);

        database.userDao().insert(newUser);
        Toast.makeText(getContext(), "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

        navigateToLogin();
    }

    private void navigateToLogin() {
        ((MainActivity) getActivity()).loadFragment(new LoginFragment());
    }
}