package id.rahmat.projekakhir.ui.home;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;

import id.rahmat.projekakhir.R;
import id.rahmat.projekakhir.databinding.FragmentHomeBinding;
import id.rahmat.projekakhir.ui.base.BaseFragment;
import id.rahmat.projekakhir.ui.receive.ReceiveActivity;
import id.rahmat.projekakhir.ui.send.SendActivity;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private TokenAdapter tokenAdapter;
    private String currentWalletAddress = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        setupChart();
        setupAssetList();
        setupActions();
        observeState();
        viewModel.refresh();
    }

    private void setupActions() {
        binding.buttonSend.setOnClickListener(v -> startActivity(new Intent(requireContext(), SendActivity.class)));
        binding.buttonReceive.setOnClickListener(v -> startActivity(new Intent(requireContext(), ReceiveActivity.class)));
        binding.buttonBuy.setOnClickListener(v -> {
            CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
            intent.launchUrl(requireContext(), android.net.Uri.parse("https://www.coingecko.com/en/coins/ethereum"));
        });
        binding.buttonRefresh.setOnClickListener(v -> {
            binding.homeSwipeRefresh.setRefreshing(true);
            viewModel.refresh();
        });
        binding.buttonPromo.setOnClickListener(v -> {
            CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
            intent.launchUrl(requireContext(), android.net.Uri.parse("https://ethereum.org/en/wallets/"));
        });
        binding.buttonCopyAddress.setOnClickListener(v -> copyWalletAddress());
        binding.buttonShowQr.setOnClickListener(v -> startActivity(new Intent(requireContext(), ReceiveActivity.class)));
        binding.homeSwipeRefresh.setOnRefreshListener(() -> binding.buttonRefresh.performClick());
    }

    private void copyWalletAddress() {
        if (currentWalletAddress == null || currentWalletAddress.isEmpty()) {
            showMessage(getString(R.string.wallet_not_ready));
            return;
        }
        ClipboardManager clipboardManager = (ClipboardManager) requireContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("wallet_address", currentWalletAddress);
        clipboardManager.setPrimaryClip(clipData);
        showMessage(getString(R.string.copy_action));
    }

    private void setupAssetList() {
        binding.recyclerTokens.setLayoutManager(new LinearLayoutManager(requireContext()));
        tokenAdapter = new TokenAdapter(new java.util.ArrayList<>());
        binding.recyclerTokens.setAdapter(tokenAdapter);
    }

    private void setupChart() {
        binding.chartEthPrice.setDrawGridBackground(false);
        binding.chartEthPrice.setTouchEnabled(true);
        binding.chartEthPrice.setDragEnabled(true);
        binding.chartEthPrice.setScaleEnabled(false);
        binding.chartEthPrice.setPinchZoom(false);
        binding.chartEthPrice.getAxisRight().setEnabled(false);
        binding.chartEthPrice.getLegend().setForm(Legend.LegendForm.NONE);
        binding.chartEthPrice.getLegend().setEnabled(false);
        binding.chartEthPrice.setNoDataText(getString(R.string.eth_chart_loading));
        binding.chartEthPrice.setMaxVisibleValueCount(16);

        Description description = new Description();
        description.setText("");
        binding.chartEthPrice.setDescription(description);

        XAxis xAxis = binding.chartEthPrice.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.parseColor("#B0B0B0"));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setAxisLineColor(Color.TRANSPARENT);

        YAxis leftAxis = binding.chartEthPrice.getAxisLeft();
        leftAxis.setTextColor(Color.parseColor("#B0B0B0"));
        leftAxis.setGridColor(Color.parseColor("#1FFFFFFF"));
        leftAxis.setAxisLineColor(Color.TRANSPARENT);

        binding.chartEthPrice.invalidate();
    }

    private void observeState() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            binding.homeSwipeRefresh.setRefreshing(false);
            currentWalletAddress = state.address;
            binding.textWalletAddress.setText(state.shortAddress);
            binding.textEthBalance.setText(state.balanceEth);
            binding.textFiatBalance.setText(state.balanceIdr + " | " + state.balanceUsd);
            binding.textNetworkBadge.setText(state.networkName);

            tokenAdapter = new TokenAdapter(state.assets);
            binding.recyclerTokens.setAdapter(tokenAdapter);

            if (state.chartEntries == null || state.chartEntries.isEmpty()) {
                binding.chartEthPrice.clear();
                binding.chartEthPrice.setNoDataText(getString(R.string.eth_chart_loading));
                binding.chartEthPrice.invalidate();
            } else {
                CandleDataSet dataSet = new CandleDataSet(state.chartEntries, "ETH");
                dataSet.setShadowColorSameAsCandle(true);
                dataSet.setIncreasingColor(Color.parseColor("#3DD598"));
                dataSet.setIncreasingPaintStyle(android.graphics.Paint.Style.FILL);
                dataSet.setDecreasingColor(Color.parseColor("#E94560"));
                dataSet.setDecreasingPaintStyle(android.graphics.Paint.Style.FILL);
                dataSet.setNeutralColor(Color.parseColor("#66A9FF"));
                dataSet.setShadowWidth(0.9f);
                dataSet.setBarSpace(0.26f);
                dataSet.setDrawValues(false);
                binding.chartEthPrice.setData(new CandleData(dataSet));
                binding.chartEthPrice.animateY(450);
                binding.chartEthPrice.invalidate();
            }

            if (state.errorMessage != null && !state.errorMessage.isEmpty()) {
                showMessage(state.errorMessage);
            }
        });
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
