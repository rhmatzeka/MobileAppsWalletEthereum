package id.rahmat.projekakhir.ui.base;

import android.widget.Toast;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    protected void showMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
