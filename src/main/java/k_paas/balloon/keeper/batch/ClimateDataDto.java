package k_paas.balloon.keeper.batch;

public record ClimateDataDto(
        Integer y,
        Integer x,
        Integer pressure,
        String uVector,
        String vVector
) {

    public ClimateDataDto {
        if (pressure != null && pressure < 0) {
            throw new IllegalArgumentException("Pressure cannot be negative");
        }
    }
    public boolean isValid() {
        return y != null && x != null && pressure != null;
    }

    public static class Builder {
        private Integer y;
        private Integer x;
        private Integer pressure;
        private String uVector;
        private String vVector;

        public Builder y(Integer y) {
            this.y = y;
            return this;
        }

        public Builder x(Integer x) {
            this.x = x;
            return this;
        }

        public Builder pressure(Integer pressure) {
            this.pressure = pressure;
            return this;
        }

        public Builder uVector(String uVector) {
            this.uVector = uVector;
            return this;
        }

        public Builder vVector(String vVector) {
            this.vVector = vVector;
            return this;
        }

        public ClimateDataDto build() {
            return new ClimateDataDto(y, x, pressure, uVector, vVector);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
