package com.hospital;

import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient extends BusyHuman {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private List<Appointment> appointments = new ArrayList<>();

    public Patient() {
    }

    @JsonCreator
    public Patient(
            @JsonProperty("id") Long id,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("dateOfBirth") LocalDate dateOfBirth) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<Appointment> getAppointments() {
        return Collections.unmodifiableList(appointments);
    }

    public void addAppointment(Appointment appointment) {
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

    public long countAppointmentsInYear(int year) {
        return appointments.stream()
                .filter(app -> app.getDate().getYear() == year)
                .count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Patient))
            return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Пацієнт {" +
                "ID=" + id +
                ", Ім'я='" + firstName + '\'' +
                ", Прізвище='" + lastName + '\'' +
                ", Дата народження=" + dateOfBirth +
                ", К-сть записів=" + appointments.size() +
                '}';
    }
}
