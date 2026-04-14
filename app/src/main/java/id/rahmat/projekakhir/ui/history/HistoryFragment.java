package id.rahmat.projekakhir.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import id.rahmat.projekakhir.databinding.FragmentHistoryBinding;
import id.rahmat.projekakhir.ui.base.BaseFragment;

public class HistoryFragment extends BaseFragment {

    private FragmentHistoryBinding binding;
    private TransactionAdapter adapter;
    private HistoryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        adapter = new TransactionAdapter(item -> {
            Intent intent = new Intent(requireContext(), TransactionDetailActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("amount", item.getAmount());
            intent.putExtra("status", item.getStatus());
            startActivity(intent);
        });
        binding.recyclerTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTransactions.setAdapter(adapter);

        binding.chipAll.setOnClickListener(v -> viewModel.setFilter("all"));
        binding.chipSent.setOnClickListener(v -> viewModel.setFilter("sent"));
        binding.chipReceived.setOnClickListener(v -> viewModel.setFilter("received"));

        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactionItems -> adapter.submitList(transactionItems));
        viewModel.refresh();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
