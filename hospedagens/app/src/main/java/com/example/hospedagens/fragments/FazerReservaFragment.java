package com.example.hospedagens.fragments;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import com.example.hospedagens.R;
import com.example.hospedagens.data.AppDatabase;
import com.example.hospedagens.data.Hospedagem;
import com.example.hospedagens.data.Reserva;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.ParseException;

public class FazerReservaFragment extends Fragment {
    private static final String ARG_HOSPEDAGEM_ID = "hospedagem_id";
    private static final String ARG_USER_ID = "user_id";
    private static final String CHANNEL_ID = "reserva_channel";

    private TextView tvNomeHospedagem;
    private TextView tvCidadeHospedagem;
    private TextView tvDescricaoHospedagem;
    private TextView tvPrecoDiaria;
    private EditText etDataCheckIn;
    private EditText etDataCheckOut;
    private EditText etNumHospedes;
    private TextView tvValorTotal;
    private Button btnConfirmarReserva;
    private Button btnCancelar;

    private AppDatabase database;
    private ExecutorService executor;
    private int hospedagemId;
    private int userId;
    private Hospedagem hospedagem;
    private SimpleDateFormat dateFormat;

    public static FazerReservaFragment newInstance(int hospedagemId, int userId) {
        FazerReservaFragment fragment = new FazerReservaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HOSPEDAGEM_ID, hospedagemId);
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hospedagemId = getArguments().getInt(ARG_HOSPEDAGEM_ID);
            userId = getArguments().getInt(ARG_USER_ID);
        }
        database = AppDatabase.getDatabase(getContext());
        executor = Executors.newSingleThreadExecutor();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        createNotificationChannel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fazer_reserva, container, false);

        initViews(view);
        loadHospedagemDetails();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        tvNomeHospedagem = view.findViewById(R.id.tvNomeHospedagem);
        tvCidadeHospedagem = view.findViewById(R.id.tvCidadeHospedagem);
        tvDescricaoHospedagem = view.findViewById(R.id.tvDescricaoHospedagem);
        tvPrecoDiaria = view.findViewById(R.id.tvPrecoDiaria);
        etDataCheckIn = view.findViewById(R.id.etDataCheckIn);
        etDataCheckOut = view.findViewById(R.id.etDataCheckOut);
        etNumHospedes = view.findViewById(R.id.etNumHospedes);
        tvValorTotal = view.findViewById(R.id.tvValorTotal);
        btnConfirmarReserva = view.findViewById(R.id.btnConfirmarReserva);
        btnCancelar = view.findViewById(R.id.btnCancelar);
    }

    private void loadHospedagemDetails() {
        executor.execute(() -> {
            hospedagem = database.hospedagemDao().getHospedagemById(hospedagemId);

            if (getActivity() != null && hospedagem != null) {
                getActivity().runOnUiThread(() -> {
                    tvNomeHospedagem.setText(hospedagem.getTitulo());
                    tvCidadeHospedagem.setText(hospedagem.getCidade());
                    tvDescricaoHospedagem.setText(hospedagem.getDescricao());
                    tvPrecoDiaria.setText(String.format("R$ %.2f/diária", hospedagem.getPrecoPorNoite()));
                });
            }
        });
    }

    private void setupClickListeners() {
        etDataCheckIn.setOnClickListener(v -> showDatePicker(etDataCheckIn));
        etDataCheckOut.setOnClickListener(v -> showDatePicker(etDataCheckOut));

        etDataCheckIn.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDatePicker(etDataCheckIn);
        });

        etDataCheckOut.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDatePicker(etDataCheckOut);
        });

        btnConfirmarReserva.setOnClickListener(v -> confirmarReserva());
        btnCancelar.setOnClickListener(v -> voltarParaHospede());

        // Listener para calcular valor total quando dados mudarem
        View.OnFocusChangeListener calculateTotalListener = (v, hasFocus) -> {
            if (!hasFocus) calculateTotal();
        };

        etDataCheckIn.setOnFocusChangeListener(calculateTotalListener);
        etDataCheckOut.setOnFocusChangeListener(calculateTotalListener);
        etNumHospedes.setOnFocusChangeListener(calculateTotalListener);
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String selectedDate = dateFormat.format(calendar.getTime());
                    editText.setText(selectedDate);
                    calculateTotal();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Não permitir datas passadas
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void calculateTotal() {
        String checkInStr = etDataCheckIn.getText().toString().trim();
        String checkOutStr = etDataCheckOut.getText().toString().trim();
        String numHospedesStr = etNumHospedes.getText().toString().trim();

        if (checkInStr.isEmpty() || checkOutStr.isEmpty() || numHospedesStr.isEmpty() || hospedagem == null) {
            tvValorTotal.setText("R$ 0,00");
            return;
        }

        try {
            Date checkIn = dateFormat.parse(checkInStr);
            Date checkOut = dateFormat.parse(checkOutStr);
            int numHospedes = Integer.parseInt(numHospedesStr);

            if (checkOut.before(checkIn) || checkOut.equals(checkIn)) {
                tvValorTotal.setText("Datas inválidas");
                return;
            }

            long diffInMillies = checkOut.getTime() - checkIn.getTime();
            int numDias = (int) (diffInMillies / (1000 * 60 * 60 * 24));

            double total = numDias * hospedagem.getPrecoPorNoite();
            tvValorTotal.setText(String.format("R$ %.2f (%d diárias)", total, numDias));

        } catch (ParseException | NumberFormatException e) {
            tvValorTotal.setText("Erro no cálculo");
        }
    }

    private void confirmarReserva() {
        if (!validarCampos()) {
            return;
        }

        String checkInStr = etDataCheckIn.getText().toString().trim();
        String checkOutStr = etDataCheckOut.getText().toString().trim();
        int numHospedes = Integer.parseInt(etNumHospedes.getText().toString().trim());

        try {
            Date checkIn = dateFormat.parse(checkInStr);
            Date checkOut = dateFormat.parse(checkOutStr);

            long diffInMillies = checkOut.getTime() - checkIn.getTime();
            int numDias = (int) (diffInMillies / (1000 * 60 * 60 * 24));
            double valorTotal = numDias * hospedagem.getPrecoPorNoite();

            Reserva reserva = new Reserva(
                    userId,
                    hospedagemId,
                    checkInStr,
                    checkOutStr,
                    numHospedes,
                    valorTotal,
                    hospedagem.getTitulo(),
                    hospedagem.getCidade()
            );

            executor.execute(() -> {
                long reservaId = database.reservaDao().insert(reserva);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (reservaId > 0) {
                            showReservaConfirmadaNotification();
                            Toast.makeText(getContext(), "Reserva confirmada com sucesso!", Toast.LENGTH_SHORT).show();
                            voltarParaHospede();
                        } else {
                            Toast.makeText(getContext(), "Erro ao confirmar reserva", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (ParseException e) {
            Toast.makeText(getContext(), "Erro nas datas informadas", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCampos() {
        String checkIn = etDataCheckIn.getText().toString().trim();
        String checkOut = etDataCheckOut.getText().toString().trim();
        String numHospedesStr = etNumHospedes.getText().toString().trim();

        if (checkIn.isEmpty()) {
            Toast.makeText(getContext(), "Informe a data de check-in", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (checkOut.isEmpty()) {
            Toast.makeText(getContext(), "Informe a data de check-out", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (numHospedesStr.isEmpty()) {
            Toast.makeText(getContext(), "Informe o número de hóspedes", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int numHospedes = Integer.parseInt(numHospedesStr);
            if (numHospedes <= 0) {
                Toast.makeText(getContext(), "Número de hóspedes deve ser maior que zero", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Número de hóspedes inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Date checkInDate = dateFormat.parse(checkIn);
            Date checkOutDate = dateFormat.parse(checkOut);

            if (checkOutDate.before(checkInDate) || checkOutDate.equals(checkInDate)) {
                Toast.makeText(getContext(), "Data de check-out deve ser posterior ao check-in", Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (ParseException e) {
            Toast.makeText(getContext(), "Formato de data inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reservas";
            String description = "Notificações de reservas confirmadas";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showReservaConfirmadaNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Reserva Confirmada!")
                .setContentText("Sua reserva em " + hospedagem.getTitulo() + " foi confirmada com sucesso.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void voltarParaHospede() {
        getParentFragmentManager().popBackStack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}