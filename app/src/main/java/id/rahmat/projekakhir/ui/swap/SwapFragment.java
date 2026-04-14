package id.rahmat.projekakhir.ui.swap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import id.rahmat.projekakhir.databinding.FragmentSwapBinding;
import id.rahmat.projekakhir.ui.base.BaseFragment;

public class SwapFragment extends BaseFragment {

    private FragmentSwapBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSwapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
