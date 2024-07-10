package k_paas.balloon.keeper.application.climate.type;

import lombok.Getter;

@Getter
public enum ClimateApiVariableType {
    U_VECTOR(2002),
    V_VECTOR(2003);

    private final Integer variableNumber;

    ClimateApiVariableType(Integer variableNumber) {
        this.variableNumber = variableNumber;
    }
}
