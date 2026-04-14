package id.rahmat.projekakhir.ui.onboarding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.rahmat.projekakhir.databinding.ItemOnboardingPageBinding;

public class OnboardingPagerAdapter extends RecyclerView.Adapter<OnboardingPagerAdapter.OnboardingViewHolder> {

    private final List<OnboardingSlide> slides;

    public OnboardingPagerAdapter(List<OnboardingSlide> slides) {
        this.slides = slides;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOnboardingPageBinding binding = ItemOnboardingPageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new OnboardingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.bind(slides.get(position));
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {

        private final ItemOnboardingPageBinding binding;

        OnboardingViewHolder(ItemOnboardingPageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(OnboardingSlide slide) {
            binding.textSlideStep.setText(slide.getStepLabel());
            binding.textSlideTitle.setText(slide.getTitle());
            binding.textSlideDescription.setText(slide.getDescription());
        }
    }
}
