package id.rahmat.projekakhir.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import id.rahmat.projekakhir.MainActivity;
import id.rahmat.projekakhir.R;
import id.rahmat.projekakhir.databinding.ActivityPinSetupBinding;
import id.rahmat.projekakhir.ui.base.BaseActivity;
import id.rahmat.projekakhir.ui.onboarding.OnboardingActivity;
import id.rahmat.projekakhir.utils.AppPreferences;
import id.rahmat.projekakhir.utils.WindowInsetsHelper;
import id.rahmat.projekakhir.wallet.WalletManager;

public class PinSetupActivity extends BaseActivity {

    private ActivityPinSetupBinding binding;
    private WalletManager walletManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPinSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowInsetsHelper.applySystemBarPadding(binding.pinSetupRoot, true, true);
        try {
            walletManager = new WalletManager(this);
        } catch (Exception exception) {
            showMessage(exception.getMessage() == null ? getString(R.string.wallet_not_ready) : exception.getMessage());
            finish();
            return;
        }

        String authMode = getIntent().getStringExtra(OnboardingActivity.EXTRA_AUTH_MODE);
        if (OnboardingActivity.MODE_IMPORT.equals(authMode)) {
            binding.textAuthMode.setText(R.string.import_wallet);
        } else {
            binding.textAuthMode.setText(R.string.create_wallet);
        }

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonContinue.setOnClickListener(v -> handleContinue());
    }

    private void handleContinue() {
        String pin = getText(binding.inputPin.getText());
        String confirmPin = getText(binding.inputConfirmPin.getText());

        binding.layoutPin.setError(null);
        binding.layoutConfirmPin.setError(null);

        if (pin.length() != 6) {
            binding.layoutPin.setError(getString(R.string.pin_invalid_length));
            return;
        }

        if (!TextUtils.equals(pin, confirmPin)) {
            binding.layoutConfirmPin.setError(getString(R.string.pin_not_match));
            return;
        }

        AppPreferences preferences = new AppPreferences(this);
        walletManager.savePin(pin);

        String authMode = getIntent().getStringExtra(OnboardingActivity.EXTRA_AUTH_MODE);
        try {
            if (OnboardingActivity.MODE_IMPORT.equals(authMode)) {
                importWallet();
                showMessage(getString(R.string.wallet_imported_message));
            } else {
                walletManager.createNewWallet();
                showMessage(getString(R.string.wallet_created_message));
            }
        } catch (Exception exception) {
            showMessage(exception.getMessage() == null ? getString(R.string.wallet_not_ready) : exception.getMessage());
            return;
        }

        preferences.setOnboardingDone(true);
        preferences.setBiometricEnabled(binding.switchBiometric.isChecked());

        Class<?> nextActivity = binding.switchBiometric.isChecked()
                ? BiometricLoginActivity.class
                : MainActivity.class;

        startActivity(new Intent(this, nextActivity));
        finish();
    }

    private String getText(CharSequence text) {
        return text == null ? "" : text.toString().trim();
    }

    private void importWallet() {
        String mnemonic = getIntent().getStringExtra(ImportWalletActivity.EXTRA_MNEMONIC);
        String privateKey = getIntent().getStringExtra(ImportWalletActivity.EXTRA_PRIVATE_KEY);

        if (!TextUtils.isEmpty(mnemonic)) {
            walletManager.importFromMnemonic(mnemonic);
            return;
        }
        if (!TextUtils.isEmpty(privateKey)) {
            walletManager.importFromPrivateKey(privateKey);
            return;
        }
        throw new IllegalStateException(getString(R.string.wallet_not_ready));
    }
}
