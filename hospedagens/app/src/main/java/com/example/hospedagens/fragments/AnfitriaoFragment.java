package com.example.hospedagens.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.hospedagens.MainActivity;
import com.example.hospedagens.R;

import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hospedagens.adapters.HospedagemAdapter;
import com.example.hospedagens.data.Hospedagem;
import com.example.hospedagens.repository.HospedagemRepository;
import com.google.android.material.textfield.TextInputEditText;

public class AnfitriaoFragment extends Fragment {
    private TextView textWelcome;
    private Button btnLogout;

    private TextInputEditText etTitulo, etDescricao, etCidade, etEndereco, etPreco, etCapacidade;
    private AutoCompleteTextView actvTipoImovel;
    private Switch switchDisponivel;
    private Button btnAdicionar, btnRemover, btnLimpar;

    // Lista e adapter
    private ListView listViewHospedagens;
    private TextView tvMensagemVazia;
    private HospedagemAdapter adapter;
    private List<Hospedagem> hospedagens;

    // Dados
    private HospedagemRepository repository;
    private int userId;
    private Hospedagem hospedagemSelecionada;
    private boolean modoEdicao = false;

    // Tipos de imóvel
    private String[] tiposImovel = {"Casa", "Apartamento", "Quarto", "Loft", "Chalé", "Pousada", "Hotel"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anfitriao, container, false);

        textWelcome = view.findViewById(R.id.text_welcome);
        btnLogout = view.findViewById(R.id.btn_logout);

        textWelcome.setText("Bem-vindo, Anfitrião!");
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getInt("userId", -1);

            if (userId != -1) {
                inicializarComponentes(view);
                configurarTipoImovel();
                configurarListeners();
                carregarHospedagens();
            } else {
                Toast.makeText(getContext(), "Erro ao carregar dados do usuário", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void inicializarComponentes(View view) {
        // Campos do formulário
        etTitulo = view.findViewById(R.id.etTitulo);
        etDescricao = view.findViewById(R.id.etDescricao);
        etCidade = view.findViewById(R.id.etCidade);
        etEndereco = view.findViewById(R.id.etEndereco);
        etPreco = view.findViewById(R.id.etPreco);
        etCapacidade = view.findViewById(R.id.etCapacidade);
        actvTipoImovel = view.findViewById(R.id.actvTipoImovel);
        switchDisponivel = view.findViewById(R.id.switchDisponivel);

        // Botões
        btnAdicionar = view.findViewById(R.id.btnAdicionar);
        btnRemover = view.findViewById(R.id.btnRemover);
        btnLimpar = view.findViewById(R.id.btnLimpar);

        // Lista
        listViewHospedagens = view.findViewById(R.id.listViewHospedagens);
        tvMensagemVazia = view.findViewById(R.id.tvMensagemVazia);

        // Inicializar repository e dados
        repository = new HospedagemRepository(getContext());
        hospedagens = new ArrayList<>();
        adapter = new HospedagemAdapter(getContext(), hospedagens);
        listViewHospedagens.setAdapter(adapter);
    }

    private void configurarTipoImovel() {
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, tiposImovel);
        actvTipoImovel.setAdapter(adapterTipo);

        // Configurações adicionais para garantir que funcione
        actvTipoImovel.setThreshold(0); // Mostra todas as opções mesmo sem digitar
        actvTipoImovel.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        // Força mostrar o dropdown ao clicar
        actvTipoImovel.setOnClickListener(v -> {
            actvTipoImovel.showDropDown();
        });

        // Também pode ser útil para dispositivos com teclado físico
        actvTipoImovel.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                actvTipoImovel.showDropDown();
            }
        });
    }

    private void configurarListeners() {
        btnAdicionar.setOnClickListener(v -> salvarHospedagem());
        btnRemover.setOnClickListener(v -> removerHospedagem());
        btnLimpar.setOnClickListener(v -> limparFormulario());
        listViewHospedagens.setOnItemClickListener((parent, view, position, id) -> {
            hospedagemSelecionada = hospedagens.get(position);
            preencherFormulario(hospedagemSelecionada);
            alternarModoEdicao(true);
        });
    }

    // Método unificado para adicionar e editar
    private void salvarHospedagem() {
        if (!validarCampos()) {
            return;
        }

        if (modoEdicao) {
            editarHospedagem();
        } else {
            adicionarHospedagem();
        }
    }

    private void adicionarHospedagem() {
        Hospedagem hospedagem = criarHospedagemFromForm();

        repository.insert(hospedagem, new HospedagemRepository.HospedagemCallback() {
            @Override
            public void onSuccess(Long result) {
                executarNaUIThread(() -> {
                    Toast.makeText(getContext(), "Hospedagem adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                    limparFormulario();
                    carregarHospedagens();
                });
            }

            @Override
            public void onError(String error) {
                executarNaUIThread(() -> {
                    Toast.makeText(getContext(), "Erro ao adicionar hospedagem: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void editarHospedagem() {
        if (hospedagemSelecionada == null) {
            return;
        }

        atualizarHospedagemFromForm(hospedagemSelecionada);

        repository.update(hospedagemSelecionada, new HospedagemRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                executarNaUIThread(() -> {
                    Toast.makeText(getContext(), "Hospedagem editada com sucesso!", Toast.LENGTH_SHORT).show();
                    limparFormulario();
                    alternarModoEdicao(false);
                    carregarHospedagens();
                });
            }

            @Override
            public void onError(String error) {
                executarNaUIThread(() -> {
                    Toast.makeText(getContext(), "Erro ao editar hospedagem: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void removerHospedagem() {
        if (hospedagemSelecionada == null) {
            Toast.makeText(getContext(), "Nenhuma hospedagem selecionada", Toast.LENGTH_SHORT).show();
            return;
        }

        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Confirmar Remoção")
                .setMessage("Tem certeza que deseja remover a hospedagem '" + hospedagemSelecionada.getTitulo() + "'?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    repository.delete(hospedagemSelecionada, new HospedagemRepository.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            executarNaUIThread(() -> {
                                Toast.makeText(getContext(), "Hospedagem removida com sucesso!", Toast.LENGTH_SHORT).show();
                                limparFormulario();
                                alternarModoEdicao(false);
                                carregarHospedagens();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            executarNaUIThread(() -> {
                                Toast.makeText(getContext(), "Erro ao remover hospedagem: " + error, Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void carregarHospedagens() {
        repository.getHospedagensByAnfitriao(userId, new HospedagemRepository.HospedagensListCallback() {
            @Override
            public void onSuccess(List<Hospedagem> result) {
                executarNaUIThread(() -> {
                    hospedagens.clear();
                    hospedagens.addAll(result);
                    adapter.notifyDataSetChanged();

                    // Mostrar/ocultar mensagem vazia
                    if (hospedagens.isEmpty()) {
                        tvMensagemVazia.setVisibility(View.VISIBLE);
                        listViewHospedagens.setVisibility(View.GONE);
                    } else {
                        tvMensagemVazia.setVisibility(View.GONE);
                        listViewHospedagens.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(String error) {
                executarNaUIThread(() -> {
                    Toast.makeText(getContext(), "Erro ao carregar hospedagens: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private boolean validarCampos() {
        boolean valido = true;

        if (TextUtils.isEmpty(etTitulo.getText())) {
            etTitulo.setError("Título é obrigatório");
            valido = false;
        }

        if (TextUtils.isEmpty(etDescricao.getText())) {
            etDescricao.setError("Descrição é obrigatória");
            valido = false;
        }

        if (TextUtils.isEmpty(etCidade.getText())) {
            etCidade.setError("Cidade é obrigatória");
            valido = false;
        }

        if (TextUtils.isEmpty(etEndereco.getText())) {
            etEndereco.setError("Endereço é obrigatório");
            valido = false;
        }

        // Validar preço
        if (TextUtils.isEmpty(etPreco.getText())) {
            etPreco.setError("Preço é obrigatório");
            valido = false;
        } else {
            if (!validarPreco()) {
                valido = false;
            }
        }

        // Validar capacidade
        if (TextUtils.isEmpty(etCapacidade.getText())) {
            etCapacidade.setError("Capacidade é obrigatória");
            valido = false;
        } else {
            if (!validarCapacidade()) {
                valido = false;
            }
        }

        if (TextUtils.isEmpty(actvTipoImovel.getText())) {
            actvTipoImovel.setError("Tipo do imóvel é obrigatório");
            valido = false;
        }

        return valido;
    }

    private boolean validarPreco() {
        try {
            double preco = Double.parseDouble(etPreco.getText().toString());
            if (preco <= 0) {
                etPreco.setError("Preço deve ser maior que zero");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            etPreco.setError("Preço inválido");
            return false;
        }
    }

    private boolean validarCapacidade() {
        try {
            int capacidade = Integer.parseInt(etCapacidade.getText().toString());
            if (capacidade <= 0) {
                etCapacidade.setError("Capacidade deve ser maior que zero");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            etCapacidade.setError("Capacidade inválida");
            return false;
        }
    }

    private Hospedagem criarHospedagemFromForm() {
        Hospedagem hospedagem = new Hospedagem(-1, "", "", "",
                "", 0, 0, "");
        hospedagem.setAnfitriaoId(userId); // Define o ID do anfitrião
        atualizarHospedagemFromForm(hospedagem);
        return hospedagem;
    }

    private void atualizarHospedagemFromForm(Hospedagem hospedagem) {
        hospedagem.setTitulo(etTitulo.getText().toString().trim());
        hospedagem.setDescricao(etDescricao.getText().toString().trim());
        hospedagem.setCidade(etCidade.getText().toString().trim());
        hospedagem.setEndereco(etEndereco.getText().toString().trim());
        hospedagem.setPrecoPorNoite(Double.parseDouble(etPreco.getText().toString()));
        hospedagem.setCapacidadeMaxima(Integer.parseInt(etCapacidade.getText().toString()));
        hospedagem.setTipoImovel(actvTipoImovel.getText().toString());
        hospedagem.setDisponivel(switchDisponivel.isChecked());
    }

    private void preencherFormulario(Hospedagem hospedagem) {
        etTitulo.setText(hospedagem.getTitulo());
        etDescricao.setText(hospedagem.getDescricao());
        etCidade.setText(hospedagem.getCidade());
        etEndereco.setText(hospedagem.getEndereco());
        etPreco.setText(String.valueOf(hospedagem.getPrecoPorNoite()));
        etCapacidade.setText(String.valueOf(hospedagem.getCapacidadeMaxima()));
        actvTipoImovel.setText(hospedagem.getTipoImovel());
        switchDisponivel.setChecked(hospedagem.isDisponivel());
    }

    private void limparFormulario() {
        etTitulo.setText("");
        etDescricao.setText("");
        etCidade.setText("");
        etEndereco.setText("");
        etPreco.setText("");
        etCapacidade.setText("");
        actvTipoImovel.setText("");
        switchDisponivel.setChecked(true);

        limparErros();
        hospedagemSelecionada = null;
    }

    private void limparErros() {
        etTitulo.setError(null);
        etDescricao.setError(null);
        etCidade.setError(null);
        etEndereco.setError(null);
        etPreco.setError(null);
        etCapacidade.setError(null);
        actvTipoImovel.setError(null);
    }

    private void alternarModoEdicao(boolean edicao) {
        modoEdicao = edicao;
        if (edicao) {
            btnAdicionar.setText("Editar");
            btnRemover.setVisibility(View.VISIBLE);
        } else {
            btnAdicionar.setText("Adicionar");
            btnRemover.setVisibility(View.GONE);
        }
    }

    // Método auxiliar para executar código na UI Thread
    private void executarNaUIThread(Runnable acao) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(acao);
        }
    }

    private void logout() {
        ((MainActivity) getActivity()).loadFragment(new LoginFragment());
    }
}