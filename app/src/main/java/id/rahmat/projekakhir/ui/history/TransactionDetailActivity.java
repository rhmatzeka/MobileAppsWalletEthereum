package id.rahmat.projekakhir.ui.history;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import id.rahmat.projekakhir.R;
import id.rahmat.projekakhir.databinding.ActivityTransactionDetailBinding;
import id.rahmat.projekakhir.ui.base.BaseActivity;
import id.rahmat.projekakhir.utils.WindowInsetsHelper;

public class TransactionDetailActivity extends BaseActivity {

    private ActivityTransactionDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowInsetsHelper.applySystemBarPadding(binding.transactionDetailRoot, true, true);

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonOpenEtherscan.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://sepolia.etherscan.io/tx/" + getString(R.string.sample_tx_hash)));
            startActivity(intent);
        });

        String title = getIntent().getStringExtra("title");
        String amount = getIntent().getStringExtra("amount");
        String status = getIntent().getStringExtra("status");

        if (title != null) {
            binding.textHash.setText(getString(R.string.sample_tx_hash));
        }
        if (amount != null) {
            binding.textAmount.setText(amount);
        }
        if (status != null) {
            binding.textStatus.setText(status);
        }
    }
}
