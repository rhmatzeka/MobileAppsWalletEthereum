package id.rahmat.projekakhir.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.concurrent.Executor;

import id.rahmat.projekakhir.R;
import id.rahmat.projekakhir.data.local.AppDatabase;
import id.rahmat.projekakhir.data.repository.WalletRepository;
import id.rahmat.projekakhir.databinding.FragmentSettingsBinding;
import id.rahmat.projekakhir.di.ServiceLocator;
import id.rahmat.projekakhir.ui.base.BaseFragment;
import id.rahmat.projekakhir.ui.onboarding.OnboardingActivity;
import id.rahmat.projekakhir.utils.AppExecutors;
import id.rahmat.projekakhir.utils.AppPreferences;

public class SettingsFragment extends BaseFragment {

    private FragmentSettingsBinding binding;
    private WalletRepository walletRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppPreferences preferences = new AppPreferences(requireContext());
        walletRepository = ServiceLocator.getWalletRepository(requireContext());

        binding.switchBiometric.setChecked(preferences.isBiometricEnabled());
        binding.switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.setBiometricEnabled(isChecked);
            showMessage(isChecked ? getString(R.string.activate_biometric) : getString(R.string.use_pin_instead));
        });

        binding.toggleGroupNetwork.check(
                AppPreferences.NETWORK_MAINNET.equals(preferences.getSelectedNetwork())
                        ? binding.buttonMainnet.getId()
                        : binding.buttonSepolia.getId()
        );
        updateNetworkButtonStyles();

        binding.toggleGroupNetwork.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            if (checkedId == binding.buttonMainnet.getId()) {
                preferences.setSelectedNetwork(AppPreferences.NETWORK_MAINNET);
                showMessage(getString(R.string.network_mainnet));
            } else {
                preferences.setSelectedNetwork(AppPreferences.NETWORK_SEPOLIA);
                showMessage(getString(R.string.network_sepolia));
            }
            updateNetworkButtonStyles();
        });

        binding.buttonBackupMnemonic.setOnClickListener(v -> revealMnemonic(preferences.isBiometricEnabled()));
        binding.buttonChangePin.setOnClickListener(v -> showMessage(getString(R.string.change_pin)));
        binding.buttonLogout.setOnClickListener(v -> confirmLogout());
        binding.textDeveloperGithub.setOnClickListener(v -> openGithubProfile());
    }

    private void revealMnemonic(boolean biometricEnabled) {
        if (!walletRepository.hasWallet()) {
            showMessage(getString(R.string.wallet_not_ready));
            return;
        }

        if (!biometricEnabled) {
            showMnemonicDialog();
            return;
        }

        BiometricManager biometricManager = BiometricManager.from(requireContext());
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG
                | BiometricManager.Authenticators.BIOMETRIC_WEAK) != BiometricManager.BIOMETRIC_SUCCESS) {
            showMnemonicDialog();
            return;
        }

        Executor executor = requireActivity().getMainExecutor();
        BiometricPrompt prompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                showMnemonicDialog();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                showMessage(errString.toString());
            }
        });

        prompt.authenticate(new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.backup_mnemonic))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.back_action))
                .build());
    }

    private void showMnemonicDialog() {
        String mnemonic = walletRepository.getMnemonic();
        if (mnemonic == null || mnemonic.isEmpty()) {
            showMessage(getString(R.string.no_mnemonic_backup));
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.backup_seed_title))
                .setMessage(mnemonic)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void confirmLogout() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .create();
        dialogView.findViewById(R.id.buttonCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.buttonConfirmLogout).setOnClickListener(v -> {
            dialog.dismiss();
            logout();
        });
        dialog.show();
    }

    private void logout() {
        AppExecutors.io().execute(() -> {
            walletRepository.clearSession();
            AppDatabase.getInstance(requireContext()).clearAllTables();

            requireActivity().runOnUiThread(() -> {
                Intent intent = new Intent(requireContext(), OnboardingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        });
    }

    private void updateNetworkButtonStyles() {
        boolean isMainnet = binding.toggleGroupNetwork.getCheckedButtonId() == binding.buttonMainnet.getId();
        applyNetworkButtonStyle(binding.buttonMainnet, isMainnet);
        applyNetworkButtonStyle(binding.buttonSepolia, !isMainnet);
    }

    private void applyNetworkButtonStyle(com.google.android.material.button.MaterialButton button, boolean selected) {
        int backgroundColor = selected ? R.color.mw_highlight : R.color.mw_surface_elevated;
        int textColor = selected ? R.color.white : R.color.mw_text_primary;
        int strokeColor = selected ? R.color.mw_highlight : R.color.mw_divider;
        button.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), backgroundColor));
        button.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), textColor));
        button.setStrokeColor(androidx.core.content.ContextCompat.getColorStateList(requireContext(), strokeColor));
    }

    private void openGithubProfile() {
        String githubUrl = "https://github.com/rhmatzeka";
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(githubUrl));
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
