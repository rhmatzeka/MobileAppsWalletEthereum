package id.rahmat.projekakhir.ui.auth;

import android.content.Intent;
import android.os.Bundle;

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
        binding.buttonUseBiometric.setVisibility(biometricEnabled ? android.view.View.VISIBLE : android.view.View.GONE);

        binding.buttonContinue.setOnClickListener(v -> verifyPin());
        binding.buttonUseBiometric.setOnClickListener(v -> authenticateWithBiometric());
    }

    private void verifyPin() {
        String pin = getText(binding.inputPin.getText());
        binding.layoutPin.setError(null);

        if (pin.length() != 6) {
            binding.layoutPin.setError(getString(R.string.pin_invalid_length));
            return;
        }

        if (!walletManager.verifyPin(pin)) {
            binding.layoutPin.setError(getString(R.string.pin_invalid));
            return;
        }

        openMain();
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

    private String getText(CharSequence text) {
        return text == null ? "" : text.toString().trim();
    }
}
