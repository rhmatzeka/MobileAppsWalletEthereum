package id.rahmat.projekakhir.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import id.rahmat.projekakhir.R;
import id.rahmat.projekakhir.databinding.ActivityImportWalletBinding;
import id.rahmat.projekakhir.ui.base.BaseActivity;
import id.rahmat.projekakhir.ui.onboarding.OnboardingActivity;
import id.rahmat.projekakhir.utils.WindowInsetsHelper;
import id.rahmat.projekakhir.wallet.WalletManager;

public class ImportWalletActivity extends BaseActivity {

    public static final String EXTRA_MNEMONIC = "extra_mnemonic";
    public static final String EXTRA_PRIVATE_KEY = "extra_private_key";

    private ActivityImportWalletBinding binding;
    private WalletManager walletManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImportWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowInsetsHelper.applySystemBarPadding(binding.importWalletRoot, true, true);
        try {
            walletManager = new WalletManager(this);
        } catch (Exception exception) {
            showMessage(exception.getMessage() == null ? getString(R.string.wallet_not_ready) : exception.getMessage());
            finish();
            return;
        }

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonContinue.setOnClickListener(v -> continueToPinSetup());
    }

    private void continueToPinSetup() {
        String mnemonic = getValue(binding.inputMnemonic.getText());
        String privateKey = getValue(binding.inputPrivateKey.getText());

        if (!mnemonic.isEmpty()) {
            if (!walletManager.isValidMnemonic(mnemonic)) {
                showMessage(getString(R.string.invalid_mnemonic));
                return;
            }
        } else if (!privateKey.isEmpty()) {
            if (!walletManager.isValidPrivateKey(privateKey)) {
                showMessage(getString(R.string.invalid_private_key));
                return;
            }
        } else {
            showMessage(getString(R.string.invalid_mnemonic));
            return;
        }

        Intent intent = new Intent(this, PinSetupActivity.class);
        intent.putExtra(OnboardingActivity.EXTRA_AUTH_MODE, OnboardingActivity.MODE_IMPORT);
        intent.putExtra(EXTRA_MNEMONIC, mnemonic);
        intent.putExtra(EXTRA_PRIVATE_KEY, privateKey);
        startActivity(intent);
        finish();
    }

    private String getValue(CharSequence text) {
        return text == null ? "" : text.toString().trim();
    }
}
