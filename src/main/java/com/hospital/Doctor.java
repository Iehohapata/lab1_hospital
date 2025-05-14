package com.hospital;

import java.time.LocalTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Doctor extends BusyHuman {
    private Long id;
    private String firstName;
    private String lastName;
    private MedicalSpecialty specialization;
    private LocalTime workStart;
    private LocalTime workEnd;
    private List<Appointment> appointments = new ArrayList<>();

    public Doctor() {
    }

    @JsonCreator
    public Doctor(
            @JsonProperty("id") Long id,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("specialization") MedicalSpecialty specialization,
            @JsonProperty("workStart") LocalTime workStart,
            @JsonProperty("workEnd") LocalTime workEnd) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.workStart = workStart;
        this.workEnd = workEnd;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public MedicalSpecialty getSpecialization() {
        return specialization;
    }

    public void setSpecialization(MedicalSpecialty specialization) {
        this.specialization = specialization;
    }

    public LocalTime getWorkStart() {
        return workStart;
    }

    public void setWorkStart(LocalTime workStart) {
        this.workStart = workStart;
    }

    public LocalTime getWorkEnd() {
        return workEnd;
    }

    public void setWorkEnd(LocalTime workEnd) {
        this.workEnd = workEnd;
    }

    public List<Appointment> getAppointments() {
        return Collections.unmodifiableList(appointments);
    }

    public void addAppointment(Appointment appointment) {
        if (!isAppointmentWithinWorkingHours(appointment)) {
            throw new IllegalStateException("Appointment doesn't much working hours");
        }

        if (!canAcceptAppointment(appointment)) {
            throw new IllegalStateException("Given appointment overlaps existing ones");
        }

        appointments.add(appointment);
    }

    @Override
    public boolean canAcceptAppointment(Appointment candidate) {
        for (Appointment existing : appointments) {
            if (existing.getDate().equals(candidate.getDate())
                    && timesOverlap(existing.getStart(), existing.getEnd(),
                            candidate.getStart(), candidate.getEnd())) {
                return false;
            }
        }
        return true;
    }

    public boolean isAppointmentWithinWorkingHours(Appointment appointment) {
        return appointment.getStart().isAfter(workStart)
                && appointment.getEnd().isBefore(workEnd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Doctor))
            return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(id, doctor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Лікар {" +
                "ID=" + id +
                ", Ім'я='" + firstName + '\'' +
                ", Прізвище='" + lastName + '\'' +
                ", Спеціальність='" + specialization.getUkrainianName() + '\'' +
                ", Робочі години=" + workStart + " - " + workEnd +
                ", К-сть призначень=" + appointments.size() +
                '}';
    }
}
