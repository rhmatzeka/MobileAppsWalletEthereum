package id.rahmat.projekakhir.ui.onboarding;

public class OnboardingSlide {

    private final String stepLabel;
    private final String title;
    private final String description;

    public OnboardingSlide(String stepLabel, String title, String description) {
        this.stepLabel = stepLabel;
        this.title = title;
        this.description = description;
    }

    public String getStepLabel() {
        return stepLabel;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
