package eu.europeana.annotation.web.model;

public enum BatchOperationStep {

    VALIDATION("Validation"),
    CHECK_UPDATE_ANNOTATIONS_AVAILABLE("Verification if annotations to be updated are available"),
    UPDATE_EXISTING_ANNOTATIONS("Updating existing annotations");

    private final String text;

    private BatchOperationStep(final String errorType) {
        this.text = errorType;
    }

    @Override
    public String toString() {
        return text;
    }
}