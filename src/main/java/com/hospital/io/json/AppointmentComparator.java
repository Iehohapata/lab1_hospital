package com.hospital.io.json;

import java.util.Comparator;

import com.hospital.core.Appointment;

public enum AppointmentComparator {
    BY_DATE("Дата прийому", Comparator.comparing(Appointment::getDate)),
    BY_START_TIME("Час початку прийому", Comparator.comparing(Appointment::getStart)),
    BY_PATIENT_ID("Пацієнт (ID)", Comparator.comparing(a -> a.getPatient().getId())),
    BY_DOCTOR_ID("Лікар (ID)", Comparator.comparing(a -> a.getDoctor().getId()));

    private final String displayName;
    private final Comparator<Appointment> comparator;

    AppointmentComparator(String displayName, Comparator<Appointment> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Comparator<Appointment> getComparator() {
        return comparator;
    }
}
