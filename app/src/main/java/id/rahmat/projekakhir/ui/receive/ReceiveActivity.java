package id.rahmat.projekakhir.ui.receive;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import id.rahmat.projekakhir.R;
import id.rahmat.projekakhir.databinding.ActivityReceiveBinding;
import id.rahmat.projekakhir.di.ServiceLocator;
import id.rahmat.projekakhir.ui.base.BaseActivity;
import id.rahmat.projekakhir.utils.WindowInsetsHelper;

public class ReceiveActivity extends BaseActivity {

    private ActivityReceiveBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowInsetsHelper.applySystemBarPadding(binding.receiveRoot, true, true);

        String address = ServiceLocator.getWalletRepository(this).getWalletAddress();
        binding.textReceiveAddress.setText(address.isEmpty() ? getString(R.string.wallet_not_ready) : address);
        renderQrCode(address);

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonCopy.setOnClickListener(v -> copyAddress());
        binding.buttonShare.setOnClickListener(v -> shareAddress());
    }

    private void copyAddress() {
        String address = binding.textReceiveAddress.getText().toString();
        if (address.isEmpty() || getString(R.string.wallet_not_ready).contentEquals(address)) {
            showMessage(getString(R.string.wallet_not_ready));
            return;
        }
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("wallet_address", address);
        clipboardManager.setPrimaryClip(clipData);
        showMessage(getString(R.string.copy_action));
    }

    private void shareAddress() {
        String address = binding.textReceiveAddress.getText().toString();
        if (address.isEmpty() || getString(R.string.wallet_not_ready).contentEquals(address)) {
            showMessage(getString(R.string.wallet_not_ready));
            return;
        }
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, address);
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_action)));
    }

    private void renderQrCode(String address) {
        if (address == null || address.isEmpty()) {
            binding.imageReceiveQr.setImageDrawable(null);
            return;
        }
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            BitMatrix bitMatrix = new MultiFormatWriter().encode("ethereum:" + address, BarcodeFormat.QR_CODE, 720, 720);
            Bitmap bitmap = encoder.createBitmap(bitMatrix);
            binding.imageReceiveQr.setBackgroundColor(Color.WHITE);
            binding.imageReceiveQr.setImageBitmap(bitmap);
        } catch (Exception exception) {
            showMessage(exception.getMessage());
        }
    }
}
