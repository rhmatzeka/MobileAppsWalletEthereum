package id.rahmat.projekakhir.ui.history;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.rahmat.projekakhir.databinding.ItemTransactionBinding;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    public interface OnTransactionClickListener {
        void onTransactionClicked(TransactionItem item);
    }

    private final List<TransactionItem> items = new ArrayList<>();
    private final OnTransactionClickListener listener;

    public TransactionAdapter(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<TransactionItem> transactionItems) {
        items.clear();
        items.addAll(transactionItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new TransactionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        private final ItemTransactionBinding binding;

        TransactionViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TransactionItem item, OnTransactionClickListener listener) {
            binding.textTransactionTitle.setText(item.getTitle());
            binding.textTransactionTime.setText(item.getTime());
            binding.textTransactionAmount.setText(item.getAmount());
            binding.textTransactionStatus.setText(item.getStatus());
            binding.viewTypeIndicator.setBackgroundColor(
                    Color.parseColor(item.isOutgoing() ? "#E94560" : "#3DD598")
            );
            binding.getRoot().setOnClickListener(v -> listener.onTransactionClicked(item));
        }
    }
}
