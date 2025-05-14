package com.hospital.core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Appointment {
    private Doctor doctor;
    private Patient patient;
    private LocalDate date;
    private Status status;
    private LocalTime start;
    private LocalTime end;

    public enum Status {
        ACTIVE,
        CLOSED,
        CANCELLED
    }

    public Appointment() {
    }

    public Appointment(Doctor doctor, Patient patient, LocalDate date,
            LocalTime start, LocalTime end, Status status) {
        this.doctor = doctor;
        this.patient = patient;
        this.date = date;
        this.start = start;
        this.end = end;
        this.status = status;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Appointment))
            return false;
        Appointment that = (Appointment) o;
        return Objects.equals(doctor, that.doctor) &&
                Objects.equals(patient, that.patient) &&
                Objects.equals(date, that.date) &&
                Objects.equals(start, that.start) &&
                Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctor, patient, date, start, end);
    }

    @Override
    public String toString() {
        return String.format("Прийом: %s %s у лікаря %s %s (%s), дата: %s, %s-%s, статус: %s",
                patient.getFirstName(), patient.getLastName(),
                doctor.getFirstName(), doctor.getLastName(), doctor.getSpecialization().getUkrainianName(),
                date, start, end, status);
    }
}
