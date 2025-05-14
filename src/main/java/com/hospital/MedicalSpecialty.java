package com.hospital;

public enum MedicalSpecialty {
    CARDIOLOGIST("Кардіолог"),
    DERMATOLOGIST("Дерматолог"),
    NEUROLOGIST("Невролог"),
    PEDIATRICIAN("Педіатр"),
    RADIOLOGIST("Рентгенолог"),
    SURGEON("Хірург"),
    PSYCHIATRIST("Психіатр");

    private final String ukrainianName;

    MedicalSpecialty(String ukrainianName) {
        this.ukrainianName = ukrainianName;
    }

    public String getUkrainianName() {
        return ukrainianName;
    }
}
