package com.vitalapp.vital_app_backend.dto.triage;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TriageCreateDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long patientId;

    @NotBlank(message = "Los síntomas son obligatorios")
    @Size(max = 500, message = "Los síntomas no pueden exceder 500 caracteres")
    private String symptoms;

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

    @NotNull(message = "El nivel de severidad es obligatorio")
    @Min(value = 1, message = "El nivel de severidad debe ser al menos 1")
    @Max(value = 5, message = "El nivel de severidad no puede exceder 5")
    private Integer severityLevel;

    @NotBlank(message = "La acción recomendada es obligatoria")
    @Size(max = 500, message = "La acción recomendada no puede exceder 500 caracteres")
    private String recommendedAction;

    // Getters and setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

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

    public Integer getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(Integer severityLevel) {
        this.severityLevel = severityLevel;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }
}