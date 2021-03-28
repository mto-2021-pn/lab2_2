package edu.iis.mto.searcher;

public class SearchResult {

    private final boolean found;
    private final int position;

    public SearchResult(Builder builder) {
        this.found = builder.found;
        this.position = builder.position;
    }

    public boolean isFound() {
        return found;
    }

    public int getPosition() {
        return position;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private boolean found;
        private int position;

        private Builder() {}

        public Builder withFound(boolean found) {
            this.found = found;
            return this;
        }

        public Builder withPosition(int position) {
            this.position = position;
            return this;
        }

        public SearchResult build() {
            return new SearchResult(this);
        }
    }
}
