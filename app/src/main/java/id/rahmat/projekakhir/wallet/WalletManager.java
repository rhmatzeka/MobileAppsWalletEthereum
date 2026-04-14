package id.rahmat.projekakhir.wallet;

import android.content.Context;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import java.security.SecureRandom;

public class WalletManager {

    private static final int[] ETHEREUM_DERIVATION_PATH = {
            44 | Bip32ECKeyPair.HARDENED_BIT,
            60 | Bip32ECKeyPair.HARDENED_BIT,
            0 | Bip32ECKeyPair.HARDENED_BIT,
            0,
            0
    };

    private final SecureWalletStorage secureWalletStorage;

    public WalletManager(Context context) {
        secureWalletStorage = new SecureWalletStorage(context, new KeyStoreCryptoManager());
    }

    public boolean hasWallet() {
        return secureWalletStorage.hasWallet();
    }

    public String getWalletAddress() {
        return secureWalletStorage.getWalletAddress();
    }

    public String getMnemonic() {
        return secureWalletStorage.getMnemonic();
    }

    public Credentials getCredentials() {
        return Credentials.create(secureWalletStorage.getPrivateKey());
    }

    public Credentials createNewWallet() {
        byte[] entropy = new byte[16];
        new SecureRandom().nextBytes(entropy);
        String mnemonic = MnemonicUtils.generateMnemonic(entropy);
        Credentials credentials = credentialsFromMnemonic(mnemonic);
        secureWalletStorage.saveWallet(
                mnemonic,
                credentials.getEcKeyPair().getPrivateKey().toString(16),
                credentials.getAddress()
        );
        return credentials;
    }

    public Credentials importFromMnemonic(String mnemonic) {
        Credentials credentials = credentialsFromMnemonic(mnemonic);
        secureWalletStorage.saveWallet(
                mnemonic.trim().toLowerCase(),
                credentials.getEcKeyPair().getPrivateKey().toString(16),
                credentials.getAddress()
        );
        return credentials;
    }

    public Credentials importFromPrivateKey(String privateKey) {
        String cleanPrivateKey = Numeric.cleanHexPrefix(privateKey).trim();
        Credentials credentials = Credentials.create(cleanPrivateKey);
        secureWalletStorage.saveWallet(
                "",
                cleanPrivateKey,
                credentials.getAddress()
        );
        return credentials;
    }

    public void savePin(String pin) {
        secureWalletStorage.savePin(pin);
    }

    public boolean verifyPin(String pin) {
        return secureWalletStorage.verifyPin(pin);
    }

    public boolean hasPin() {
        return secureWalletStorage.hasPin();
    }

    public void clearWallet() {
        secureWalletStorage.clearWalletData();
    }

    public boolean isValidPrivateKey(String privateKey) {
        try {
            Credentials.create(Numeric.cleanHexPrefix(privateKey));
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public boolean isValidMnemonic(String mnemonic) {
        try {
            return MnemonicUtils.validateMnemonic(mnemonic.trim().toLowerCase());
        } catch (Exception ignored) {
            return false;
        }
    }

    public boolean isValidAddress(String address) {
        return WalletUtils.isValidAddress(address);
    }

    private Credentials credentialsFromMnemonic(String mnemonic) {
        // Web3j does not auto-pick the Ethereum account path, so we derive BIP-44 manually.
        byte[] seed = MnemonicUtils.generateSeed(mnemonic.trim().toLowerCase(), "");
        Bip32ECKeyPair masterKey = Bip32ECKeyPair.generateKeyPair(seed);
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKey, ETHEREUM_DERIVATION_PATH);
        ECKeyPair ecKeyPair = ECKeyPair.create(derivedKeyPair.getPrivateKey());
        return Credentials.create(ecKeyPair);
    }
}
