package id.rahmat.projekakhir.wallet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import id.rahmat.projekakhir.utils.AppPreferences;

public final class EthereumNetworkRegistry {

    private static final Gson GSON = new Gson();
    private static final Type NETWORK_LIST_TYPE = new TypeToken<List<EthereumNetwork>>() { }.getType();

    private EthereumNetworkRegistry() {
    }

    public static List<EthereumNetwork> getAll(AppPreferences appPreferences) {
        Map<String, EthereumNetwork> unique = new LinkedHashMap<>();
        for (EthereumNetwork preset : EthereumNetwork.getPresetNetworks()) {
            unique.put(preset.getKey(), preset);
        }
        for (EthereumNetwork custom : getCustomNetworks(appPreferences)) {
            unique.put(custom.getKey(), custom);
        }
        return new ArrayList<>(unique.values());
    }

    public static EthereumNetwork resolve(String key, AppPreferences appPreferences) {
        for (EthereumNetwork network : getAll(appPreferences)) {
            if (network.getKey().equalsIgnoreCase(key == null ? "" : key.trim())) {
                return network;
            }
        }
        return EthereumNetwork.defaultNetwork();
    }

    public static List<EthereumNetwork> getCustomNetworks(AppPreferences appPreferences) {
        String json = appPreferences.getCustomNetworksJson();
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<EthereumNetwork> networks = GSON.fromJson(json, NETWORK_LIST_TYPE);
            return networks == null ? new ArrayList<>() : networks;
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    public static void saveCustomNetwork(AppPreferences appPreferences, EthereumNetwork network) {
        List<EthereumNetwork> customNetworks = getCustomNetworks(appPreferences);
        List<EthereumNetwork> updated = new ArrayList<>();
        for (EthereumNetwork existing : customNetworks) {
            if (!existing.getKey().equalsIgnoreCase(network.getKey())) {
                updated.add(existing);
            }
        }
        updated.add(network);
        appPreferences.setCustomNetworksJson(GSON.toJson(updated, NETWORK_LIST_TYPE));
    }
}
