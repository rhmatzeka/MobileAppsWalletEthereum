package id.rahmat.projekakhir.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.bumptech.glide.Glide;

import id.rahmat.projekakhir.databinding.ItemNftBinding;

public class NftAdapter extends RecyclerView.Adapter<NftAdapter.NftViewHolder> {

    private final List<NftItem> items;

    public NftAdapter(List<NftItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public NftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNftBinding binding = ItemNftBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NftViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NftViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class NftViewHolder extends RecyclerView.ViewHolder {

        private final ItemNftBinding binding;

        NftViewHolder(ItemNftBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(NftItem item) {
            binding.textNftSymbol.setText(item.getSymbol());
            binding.textNftName.setText(item.getCollectionName());
            binding.textNftTokenId.setText(item.getTokenIdLabel());
            binding.textNftNetwork.setText(item.getNetworkName());
            binding.textNftContract.setText(item.getShortContract());

            if (item.getImageUrl() == null || item.getImageUrl().isEmpty()) {
                Glide.with(binding.imageNft.getContext()).clear(binding.imageNft);
                binding.imageNft.setVisibility(View.GONE);
                binding.textNftSymbol.setVisibility(View.VISIBLE);
            } else {
                binding.imageNft.setVisibility(View.VISIBLE);
                binding.textNftSymbol.setVisibility(View.GONE);
                Glide.with(binding.imageNft.getContext())
                        .load(item.getImageUrl())
                        .centerCrop()
                        .into(binding.imageNft);
            }
        }
    }
}
