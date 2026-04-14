package id.rahmat.projekakhir;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import id.rahmat.projekakhir.databinding.ActivityMainBinding;
import id.rahmat.projekakhir.ui.base.BaseActivity;
import id.rahmat.projekakhir.ui.history.HistoryFragment;
import id.rahmat.projekakhir.ui.home.HomeFragment;
import id.rahmat.projekakhir.ui.settings.SettingsFragment;
import id.rahmat.projekakhir.ui.swap.SwapFragment;
import id.rahmat.projekakhir.utils.WindowInsetsHelper;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowInsetsHelper.applySystemBarPadding(binding.mainRoot, true, true);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            openTab(item.getItemId());
            return true;
        });

        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.menu_home);
        }
    }

    private void openTab(int itemId) {
        Fragment fragment;
        String tag;

        if (itemId == R.id.menu_history) {
            fragment = new HistoryFragment();
            tag = "history";
        } else if (itemId == R.id.menu_swap) {
            fragment = new SwapFragment();
            tag = "swap";
        } else if (itemId == R.id.menu_settings) {
            fragment = new SettingsFragment();
            tag = "settings";
        } else {
            fragment = new HomeFragment();
            tag = "home";
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.mainFragmentContainer.getId(), fragment, tag)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
