package id.rahmat.projekakhir.ui.history;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import id.rahmat.projekakhir.R;
import id.rahmat.projekakhir.databinding.ActivityTransactionDetailBinding;
import id.rahmat.projekakhir.ui.base.BaseActivity;
import id.rahmat.projekakhir.utils.WindowInsetsHelper;
import id.rahmat.projekakhir.utils.AppPreferences;
import id.rahmat.projekakhir.wallet.EthereumNetwork;
import id.rahmat.projekakhir.wallet.EthereumNetworkRegistry;

public class TransactionDetailActivity extends BaseActivity {

    private ActivityTransactionDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowInsetsHelper.applySystemBarPadding(binding.transactionDetailRoot, true, true);

        String hash = getIntent().getStringExtra("hash");
        String time = getIntent().getStringExtra("time");
        String amount = getIntent().getStringExtra("amount");
        String gasFee = getIntent().getStringExtra("gasFee");
        String status = getIntent().getStringExtra("status");
        String networkKey = getIntent().getStringExtra("networkKey");
        EthereumNetwork network = EthereumNetworkRegistry.resolve(networkKey, new AppPreferences(this));

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonOpenEtherscan.setOnClickListener(v -> openExplorer(network, hash));
        binding.buttonOpenEtherscan.setEnabled(network.hasExplorerUrl() && hash != null && !hash.isEmpty());

        if (hash != null && !hash.isEmpty()) {
            binding.textHash.setText(hash);
        }
        if (time != null) {
            binding.textTime.setText(time);
        }
        if (amount != null) {
            binding.textAmount.setText(amount);
        }
        if (gasFee != null) {
            binding.textGasFee.setText(gasFee);
        }
        if (status != null) {
            binding.textStatus.setText(status);
        }
    }

    private void openExplorer(EthereumNetwork network, String hash) {
        if (!network.hasExplorerUrl() || hash == null || hash.isEmpty()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(network.getExplorerTxBaseUrl() + hash));
        startActivity(intent);
    }
}
