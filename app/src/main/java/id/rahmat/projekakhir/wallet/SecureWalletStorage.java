package id.rahmat.projekakhir.wallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SecureWalletStorage {

    private static final String PREFS_NAME = "mats_wallet_secure_store";
    private static final String KEY_ENCRYPTED_MNEMONIC = "encrypted_mnemonic";
    private static final String KEY_ENCRYPTED_PRIVATE_KEY = "encrypted_private_key";
    private static final String KEY_WALLET_ADDRESS = "wallet_address";
    private static final String KEY_PIN_HASH = "pin_hash";
    private static final String KEY_PIN_SALT = "pin_salt";

    private final SharedPreferences preferences;
    private final KeyStoreCryptoManager cryptoManager;

    public SecureWalletStorage(Context context, KeyStoreCryptoManager cryptoManager) {
        this.cryptoManager = cryptoManager;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            preferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException exception) {
            throw new IllegalStateException("Failed to create encrypted preferences", exception);
        }
    }

    public void saveWallet(String mnemonic, String privateKey, String address) {
        preferences.edit()
                .putString(KEY_ENCRYPTED_MNEMONIC, cryptoManager.encrypt(mnemonic))
                .putString(KEY_ENCRYPTED_PRIVATE_KEY, cryptoManager.encrypt(privateKey))
                .putString(KEY_WALLET_ADDRESS, address)
                .apply();
    }

    public boolean hasWallet() {
        return preferences.contains(KEY_ENCRYPTED_PRIVATE_KEY) && preferences.contains(KEY_WALLET_ADDRESS);
    }

    public String getWalletAddress() {
        return preferences.getString(KEY_WALLET_ADDRESS, "");
    }

    public String getMnemonic() {
        return cryptoManager.decrypt(preferences.getString(KEY_ENCRYPTED_MNEMONIC, ""));
    }

    public String getPrivateKey() {
        return cryptoManager.decrypt(preferences.getString(KEY_ENCRYPTED_PRIVATE_KEY, ""));
    }

    public void savePin(String pin) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        String hash = hashPin(pin, salt);
        preferences.edit()
                .putString(KEY_PIN_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
                .putString(KEY_PIN_HASH, hash)
                .apply();
    }

    public boolean verifyPin(String pin) {
        String saltBase64 = preferences.getString(KEY_PIN_SALT, "");
        String storedHash = preferences.getString(KEY_PIN_HASH, "");
        if (saltBase64 == null || saltBase64.isEmpty() || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        byte[] salt = Base64.decode(saltBase64, Base64.NO_WRAP);
        return storedHash.equals(hashPin(pin, salt));
    }

    public void clearWalletData() {
        preferences.edit()
                .remove(KEY_ENCRYPTED_MNEMONIC)
                .remove(KEY_ENCRYPTED_PRIVATE_KEY)
                .remove(KEY_WALLET_ADDRESS)
                .remove(KEY_PIN_HASH)
                .remove(KEY_PIN_SALT)
                .apply();
    }

    public boolean hasPin() {
        return preferences.contains(KEY_PIN_HASH) && preferences.contains(KEY_PIN_SALT);
    }

    private String hashPin(String pin, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(pin.toCharArray(), salt, 15000, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return Base64.encodeToString(factory.generateSecret(spec).getEncoded(), Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new IllegalStateException("Failed to hash local PIN", exception);
        }
    }
}
