package id.rahmat.projekakhir.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import java.util.concurrent.Executor;

import id.rahmat.projekakhir.MainActivity;
import id.rahmat.projekakhir.R;
import id.rahmat.projekakhir.databinding.ActivityBiometricLoginBinding;
import id.rahmat.projekakhir.ui.base.BaseActivity;
import id.rahmat.projekakhir.utils.WindowInsetsHelper;

public class BiometricLoginActivity extends BaseActivity {

    private ActivityBiometricLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBiometricLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowInsetsHelper.applySystemBarPadding(binding.biometricRoot, true, true);

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonUseBiometric.setOnClickListener(v -> authenticateWithBiometric());
        binding.buttonUsePin.setOnClickListener(v -> openMain());
    }

    private void openMain() {
        startActivity(new Intent(this, PinLoginActivity.class));
        finish();
    }

    private void authenticateWithBiometric() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG
                | BiometricManager.Authenticators.BIOMETRIC_WEAK) != BiometricManager.BIOMETRIC_SUCCESS) {
            showMessage(getString(R.string.activate_biometric));
            return;
        }

        Executor executor = getMainExecutor();
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
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

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.use_pin_instead))
                .build();
        biometricPrompt.authenticate(promptInfo);
    }
}
