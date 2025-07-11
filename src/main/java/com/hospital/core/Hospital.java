package com.hospital.core;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hospital.core.Appointment.Status;
import com.hospital.io.json.HospitalDeserializer;
import com.hospital.io.json.HospitalSerializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(using = HospitalSerializer.class)
@JsonDeserialize(using = HospitalDeserializer.class)
public class Hospital {
    @JsonProperty("doctors")
    private HashMap<Long, Doctor> doctors;

    @JsonProperty("patients")
    private HashMap<Long, Patient> patients;

    private Long doctorIdCounter = 1l;
    private Long patientIdCounter = 1l;

    public Hospital() {
        doctors = new HashMap<>();
        patients = new HashMap<>();
    }

    public List<Doctor> findDoctorsBySpecialty(MedicalSpecialty specialty) {
        if (specialty == null)
            throw new IllegalArgumentException("Enum value is null");

        return doctors.values().stream()
                .filter(doctor -> doctor.getSpecialization() == specialty)
                .collect(Collectors.toList());
    }

    public void addDoctor(Doctor doctor) {
        doctors.put(doctor.getId(), doctor);
    }

    public void addPatient(Patient patient) {
        patients.put(patient.getId(), patient);
    }

    public Appointment createNearestAvailableAppointment(Patient patient, Doctor doctor) {
        if (patient == null || doctor == null) {
            throw new IllegalArgumentException("Passing null arguments to function is forbidden");
        }

        LocalDate date = LocalDate.now().plusDays(1);
        Duration duration = Duration.ofMinutes(30);
        LocalTime start = doctor.getWorkStart();

        while (true) {
            while (start.plus(duration).compareTo(doctor.getWorkEnd()) <= 0) {
                LocalTime end = start.plus(duration);
                Appointment candidate = new Appointment(doctor, patient, date, start, end, Status.ACTIVE);

                if (doctor.canAcceptAppointment(candidate) && patient.canAcceptAppointment(candidate)) {
                    doctor.addAppointment(candidate);
                    patient.addAppointment(candidate);
                    return candidate;
                }

                start = start.plusMinutes(30);
            }

            date = date.plusDays(1);
            start = doctor.getWorkStart();
        }
    }

    public Patient createPatient(String firstName, String lastName, LocalDate dateOfBirth) {
        Long id = generateUniquePatientId();
        Patient patient = new Patient(id, firstName, lastName, dateOfBirth);
        patients.put(id, patient);
        return patient;
    }

    private Long generateUniquePatientId() {
        while (patients.containsKey(patientIdCounter)) {
            patientIdCounter++;
            if (patientIdCounter == Long.MAX_VALUE) {
                throw new IllegalStateException("Maximum number of patients reached");
            }
        }
        return patientIdCounter;
    }

    public Optional<Patient> getPatient(Long id) {
        return Optional.ofNullable(patients.get(id));
    }

    public Collection<Patient> getAllPatients() {
        return patients.values();
    }

    public void updatePatient(Long id, String firstName, String lastName, LocalDate dateOfBirht) {
        Patient patient = patients.get(id);
        if (patient == null) {
            throw new NoSuchElementException();
        }
        if (firstName != null)
            patient.setFirstName(firstName);
        if (lastName != null)
            patient.setLastName(lastName);
        if (dateOfBirht != null)
            patient.setDateOfBirth(dateOfBirht);
    }

    public void deletePatient(Long id) {
        if (patients.remove(id) == null) {
            throw new NoSuchElementException();
        }
    }

    public Doctor createDoctor(String firstName, String lastName, MedicalSpecialty specialty,
            LocalTime workStart, LocalTime workEnd) {
        Long id = generateUniqueDoctorId();
        Doctor doctor = new Doctor(id, firstName, lastName, specialty, workStart, workEnd);
        doctors.put(id, doctor);
        return doctor;
    }

    private Long generateUniqueDoctorId() {
        while (doctors.containsKey(doctorIdCounter)) {
            doctorIdCounter++;
            if (doctorIdCounter == Long.MAX_VALUE) {
                throw new IllegalStateException("Maximum number of doctors reached");
            }
        }
        return doctorIdCounter;
    }

    public Optional<Doctor> getDoctor(long id) {
        return Optional.ofNullable(doctors.get(id));
    }

    public Collection<Doctor> getAllDoctors() {
        return doctors.values();
    }

    public void updateDoctor(Long id, String firstName, String lastName,
            LocalTime workStart, LocalTime workEnd) {
        Doctor doctor = doctors.get(id);
        if (doctor == null)
            throw new NoSuchElementException();
        if (firstName != null)
            doctor.setFirstName(firstName);
        if (lastName != null)
            doctor.setLastName(lastName);
        if (workStart != null)
            doctor.setWorkStart(workStart);
        if (workEnd != null)
            doctor.setWorkEnd(workEnd);
    }

    public void deleteDoctor(long id) {
        if (doctors.remove(id) == null) {
            throw new NoSuchElementException();
        }
    }

    public String generateFullReport() {
        StringBuilder report = new StringBuilder();

        report.append("<-- Список лікарів та їх прийомів -->\n");
        for (Doctor doctor : getAllDoctors()) {
            report.append(doctor).append("\n");
            if (doctor.getAppointments().isEmpty()) {
                report.append("    Немає призначень\n");
            } else {
                for (Appointment appointment : doctor.getAppointments()) {
                    report.append("    ").append(appointment).append("\n");
                }
            }
        }

        report.append("\n<-- Список пацієнтів -->\n");
        for (Patient patient : getAllPatients()) {
            report.append(patient).append("\n");
        }

        return report.toString();
    }
}
