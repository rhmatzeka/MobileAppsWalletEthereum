package id.rahmat.projekakhir.wallet;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class KeyStoreCryptoManager {

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "mats_wallet_master_key";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private final KeyStore keyStore;

    public KeyStoreCryptoManager() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to initialize Android Keystore", exception);
        }
    }

    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return "";
        }

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey());
            byte[] iv = cipher.getIV();
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // We store IV + ciphertext together because AES/GCM needs the exact IV to decrypt later.
            return Base64.encodeToString(iv, Base64.NO_WRAP)
                    + ":"
                    + Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to encrypt sensitive wallet data", exception);
        }
    }

    public String decrypt(String encryptedPayload) {
        if (encryptedPayload == null || encryptedPayload.isEmpty()) {
            return "";
        }

        try {
            String[] parts = encryptedPayload.split(":");
            byte[] iv = Base64.decode(parts[0], Base64.NO_WRAP);
            byte[] encryptedBytes = Base64.decode(parts[1], Base64.NO_WRAP);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec);
            return new String(cipher.doFinal(encryptedBytes), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to decrypt wallet data", exception);
        }
    }

    private SecretKey getOrCreateSecretKey() throws GeneralSecurityException {
        KeyStore.SecretKeyEntry existingEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
        if (existingEntry != null) {
            return existingEntry.getSecretKey();
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        keyGenerator.init(new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .build());
        return keyGenerator.generateKey();
    }
}
