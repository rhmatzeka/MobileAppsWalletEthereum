package id.rahmat.projekakhir.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import java.util.concurrent.Executor;

import id.rahmat.projekakhir.MainActivity;
import id.rahmat.projekakhir.R;
import id.rahmat.projekakhir.databinding.ActivityPinLoginBinding;
import id.rahmat.projekakhir.ui.base.BaseActivity;
import id.rahmat.projekakhir.utils.AppPreferences;
import id.rahmat.projekakhir.utils.WindowInsetsHelper;
import id.rahmat.projekakhir.wallet.WalletManager;

public class PinLoginActivity extends BaseActivity {

    private ActivityPinLoginBinding binding;
    private WalletManager walletManager;
    private final StringBuilder pinBuilder = new StringBuilder();
    private TextView[] pinSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPinLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowInsetsHelper.applySystemBarPadding(binding.pinLoginRoot, true, true);

        try {
            walletManager = new WalletManager(this);
        } catch (Exception exception) {
            showMessage(exception.getMessage() == null ? getString(R.string.wallet_not_ready) : exception.getMessage());
            finish();
            return;
        }

        AppPreferences preferences = new AppPreferences(this);
        boolean biometricEnabled = preferences.isBiometricEnabled();
        binding.buttonUseBiometric.setVisibility(biometricEnabled ? View.VISIBLE : View.INVISIBLE);

        pinSlots = new TextView[]{
                binding.pinSlotOne,
                binding.pinSlotTwo,
                binding.pinSlotThree,
                binding.pinSlotFour,
                binding.pinSlotFive,
                binding.pinSlotSix
        };

        setupKeypad();
        binding.buttonUseBiometric.setOnClickListener(v -> authenticateWithBiometric());
    }

    private void setupKeypad() {
        binding.keyZero.setOnClickListener(v -> appendPin("0"));
        binding.keyOne.setOnClickListener(v -> appendPin("1"));
        binding.keyTwo.setOnClickListener(v -> appendPin("2"));
        binding.keyThree.setOnClickListener(v -> appendPin("3"));
        binding.keyFour.setOnClickListener(v -> appendPin("4"));
        binding.keyFive.setOnClickListener(v -> appendPin("5"));
        binding.keySix.setOnClickListener(v -> appendPin("6"));
        binding.keySeven.setOnClickListener(v -> appendPin("7"));
        binding.keyEight.setOnClickListener(v -> appendPin("8"));
        binding.keyNine.setOnClickListener(v -> appendPin("9"));
        binding.keyBackspace.setOnClickListener(v -> removeLastPinDigit());
        updatePinSlots();
    }

    private void appendPin(String digit) {
        if (pinBuilder.length() >= 6) {
            return;
        }
        binding.textPinMessage.setText("");
        pinBuilder.append(digit);
        updatePinSlots();
        if (pinBuilder.length() == 6) {
            verifyPin();
        }
    }

    private void removeLastPinDigit() {
        if (pinBuilder.length() == 0) {
            return;
        }
        binding.textPinMessage.setText("");
        pinBuilder.deleteCharAt(pinBuilder.length() - 1);
        updatePinSlots();
    }

    private void verifyPin() {
        String pin = pinBuilder.toString();

        if (!walletManager.verifyPin(pin)) {
            binding.textPinMessage.setText(R.string.pin_invalid);
            pinBuilder.setLength(0);
            updatePinSlots();
            binding.pinIndicatorContainer.animate()
                    .translationX(12f)
                    .setDuration(70L)
                    .withEndAction(() -> binding.pinIndicatorContainer.animate()
                            .translationX(0f)
                            .setDuration(70L)
                            .start())
                    .start();
            return;
        }

        openMain();
    }

    private void updatePinSlots() {
        int pinLength = pinBuilder.length();
        for (int i = 0; i < pinSlots.length; i++) {
            boolean filled = i < pinLength;
            pinSlots[i].setText(filled ? "\u2022" : "");
            pinSlots[i].setBackgroundResource(filled
                    ? R.drawable.bg_pin_slot_filled
                    : R.drawable.bg_pin_slot_empty);
        }
    }

    private void authenticateWithBiometric() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG
                | BiometricManager.Authenticators.BIOMETRIC_WEAK) != BiometricManager.BIOMETRIC_SUCCESS) {
            showMessage(getString(R.string.activate_biometric));
            return;
        }

        Executor executor = getMainExecutor();
        BiometricPrompt prompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                openMain();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                showMessage(errString.toString());
            }
        });

        prompt.authenticate(new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.back_action))
                .build());
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
