package com.vitalapp.vital_app_backend.dto.triage;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TriageUpdateDTO {

    @Pattern(regexp = "^\\d{2,3}/\\d{2,3}$", message = "La presión arterial debe tener formato XXX/XXX")
    private String bloodPressure;

    @Min(value = 30, message = "El ritmo cardíaco debe ser al menos 30")
    @Max(value = 200, message = "El ritmo cardíaco no puede exceder 200")
    private Integer heartRate;

    @DecimalMin(value = "35.0", message = "La temperatura debe ser al menos 35.0")
    @DecimalMax(value = "42.0", message = "La temperatura no puede exceder 42.0")
    private Double temperature;

    @Min(value = 0, message = "La saturación de oxígeno debe ser al menos 0")
    @Max(value = 100, message = "La saturación de oxígeno no puede exceder 100")
    private Integer oxygenSaturation;

    @Size(max = 500, message = "La acción recomendada no puede exceder 500 caracteres")
    private String recommendedAction;

    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    private String notes;

    // Getters and setters
    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getOxygenSaturation() {
        return oxygenSaturation;
    }

    public void setOxygenSaturation(Integer oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}