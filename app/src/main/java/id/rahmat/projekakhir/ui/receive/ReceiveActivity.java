package id.rahmat.projekakhir.ui.receive;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.math.BigDecimal;
import java.math.BigInteger;

import id.rahmat.projekakhir.R;
import id.rahmat.projekakhir.databinding.ActivityReceiveBinding;
import id.rahmat.projekakhir.di.ServiceLocator;
import id.rahmat.projekakhir.ui.base.BaseActivity;
import id.rahmat.projekakhir.utils.WindowInsetsHelper;

public class ReceiveActivity extends BaseActivity {

    private ActivityReceiveBinding binding;
    private String receiveAddress = "";
    private String requestedAmountEth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowInsetsHelper.applySystemBarPadding(binding.receiveRoot, true, true);

        receiveAddress = ServiceLocator.getWalletRepository(this).getWalletAddress();
        binding.textReceiveAddress.setText(receiveAddress.isEmpty() ? getString(R.string.wallet_not_ready) : receiveAddress);
        binding.textReceiveQrAddress.setText(formatQrAddress(receiveAddress));
        updateAmountSummary();
        renderQrCode();

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonInfo.setOnClickListener(v -> showMessage(getString(R.string.receive_info_message)));
        binding.buttonCopy.setOnClickListener(v -> copyAddress());
        binding.buttonSetAmount.setOnClickListener(v -> showSetAmountDialog());
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
        String amount = requestedAmountEth;
        String shareText = amount.isEmpty()
                ? address
                : "Kirim " + amount + " ETH ke " + address + "\n" + buildEthereumPayload(address);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_action)));
    }

    private void renderQrCode() {
        if (receiveAddress == null || receiveAddress.isEmpty()) {
            binding.imageReceiveQr.setImageDrawable(null);
            return;
        }
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            BitMatrix bitMatrix = new MultiFormatWriter().encode(buildEthereumPayload(receiveAddress), BarcodeFormat.QR_CODE, 720, 720);
            Bitmap bitmap = encoder.createBitmap(bitMatrix);
            binding.imageReceiveQr.setBackgroundColor(Color.WHITE);
            binding.imageReceiveQr.setImageBitmap(bitmap);
        } catch (Exception exception) {
            showMessage(getString(R.string.receive_invalid_amount));
        }
    }

    private String buildEthereumPayload(String address) {
        String amount = requestedAmountEth;
        if (amount.isEmpty()) {
            return "ethereum:" + address;
        }
        BigInteger wei = new BigDecimal(amount).movePointRight(18).toBigInteger();
        return "ethereum:" + address + "?value=" + wei.toString();
    }

    private void showSetAmountDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setSingleLine(true);
        input.setHint(getString(R.string.receive_amount_hint));
        input.setText(requestedAmountEth);
        input.setSelectAllOnFocus(true);
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.parseColor("#B8C0CC"));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.receive_set_amount))
                .setMessage(getString(R.string.receive_request_subtitle))
                .setView(input)
                .setNegativeButton(getString(R.string.receive_clear_amount), null)
                .setPositiveButton(getString(R.string.receive_save_amount), null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                requestedAmountEth = "";
                updateAmountSummary();
                renderQrCode();
                dialog.dismiss();
            });
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String value = input.getText() == null ? "" : input.getText().toString().trim();
                if (!isValidAmount(value)) {
                    input.setError(getString(R.string.receive_invalid_amount));
                    return;
                }
                requestedAmountEth = value;
                updateAmountSummary();
                renderQrCode();
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    private boolean isValidAmount(String value) {
        try {
            return !value.isEmpty() && new BigDecimal(value).compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception exception) {
            return false;
        }
    }

    private void updateAmountSummary() {
        if (requestedAmountEth == null || requestedAmountEth.isEmpty()) {
            binding.textReceiveAmountSummary.setText(R.string.receive_amount_not_set);
        } else {
            binding.textReceiveAmountSummary.setText(getString(R.string.receive_amount_summary, requestedAmountEth));
        }
    }

    private String formatQrAddress(String address) {
        if (address == null || address.length() < 18) {
            return address == null ? "" : address;
        }
        int middle = address.length() / 2;
        return address.substring(0, middle) + "\n" + address.substring(middle);
    }
}
