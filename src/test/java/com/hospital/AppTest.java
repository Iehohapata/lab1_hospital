package com.hospital;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hospital.core.Appointment;
import com.hospital.core.Doctor;
import com.hospital.core.Hospital;
import com.hospital.core.MedicalSpecialty;
import com.hospital.core.Patient;

public class AppTest {
    private Hospital hospital;
    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        hospital = new Hospital();
        doctor = hospital.createDoctor("Іван", "Іванов", MedicalSpecialty.CARDIOLOGIST,
                LocalTime.of(9, 0), LocalTime.of(17, 0));
        patient = hospital.createPatient("Петро", "Петренко", LocalDate.of(1990, 1, 1));
    }

    @Test
    void testCreateDoctorAssignsUniqueId() {
        Doctor doctor2 = hospital.createDoctor("Марія", "Коваленко", MedicalSpecialty.DERMATOLOGIST,
                LocalTime.of(10, 0), LocalTime.of(18, 0));
        assertNotEquals(doctor.getId(), doctor2.getId());
    }

    @Test
    void testCreatePatientAssignsUniqueId() {
        Patient patient2 = hospital.createPatient("Олена", "Гринчук", LocalDate.of(1985, 5, 5));
        assertNotEquals(patient.getId(), patient2.getId());
    }

    @Test
    void testAddAppointmentSuccessfully() {
        Appointment appointment = hospital.createNearestAvailableAppointment(patient, doctor);
        assertEquals(1, doctor.getAppointments().size());
        assertEquals(1, patient.getAppointments().size());
        assertEquals(appointment, doctor.getAppointments().get(0));
    }

    @Test
    void testOverlappingAppointmentThrows() {
        Appointment a1 = hospital.createNearestAvailableAppointment(patient, doctor);
        Appointment overlapping = new Appointment(doctor, patient, a1.getDate(),
                a1.getStart(), a1.getEnd(), Appointment.Status.ACTIVE);
        assertThrows(IllegalStateException.class, () -> doctor.addAppointment(overlapping));
    }

    @Test
    void testUpdateDoctorUpdatesFields() {
        hospital.updateDoctor(doctor.getId(), "Олексій", null,
                LocalTime.of(8, 0), null);
        Doctor updated = hospital.getDoctor(doctor.getId()).get();
        assertEquals("Олексій", updated.getFirstName());
        assertEquals(LocalTime.of(8, 0), updated.getWorkStart());
    }

    @Test
    void testUpdatePatientUpdatesFields() {
        hospital.updatePatient(patient.getId(), null, "Сидоренко", null);
        Patient updated = hospital.getPatient(patient.getId()).get();
        assertEquals("Сидоренко", updated.getLastName());
    }

    @Test
    void testUpdateNonExistentDoctorThrows() {
        assertThrows(NoSuchElementException.class,
                () -> hospital.updateDoctor(999L, "Ім'я", null, null, null));
    }

    @Test
    void testDeletePatientRemovesThem() {
        hospital.deletePatient(patient.getId());
        assertFalse(hospital.getPatient(patient.getId()).isPresent());
    }

    @Test
    void testFindDoctorsBySpecialty() {
        var list = hospital.findDoctorsBySpecialty(MedicalSpecialty.CARDIOLOGIST);
        assertTrue(list.contains(doctor));
    }

    @Test
    void testFindDoctorsByNullSpecialtyThrows() {
        assertThrows(IllegalArgumentException.class, () -> hospital.findDoctorsBySpecialty(null));
    }

    @Test
    void testAddAppointmentToPatientSuccess() {
        Patient patient = new Patient(1L, "Іван", "Іваненко", LocalDate.of(1990, 1, 1));
        Appointment appointment = new Appointment(null, patient, LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(10, 30), Appointment.Status.ACTIVE);

        patient.addAppointment(appointment);

        assertEquals(1, patient.getAppointments().size());
        assertTrue(patient.getAppointments().contains(appointment));
    }

    @Test
    void testAddAppointmentToPatientConflict() {
        Patient patient = new Patient(1L, "Іван", "Іваненко", LocalDate.of(1990, 1, 1));

        Appointment first = new Appointment(null, patient, LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(10, 30), Appointment.Status.ACTIVE);
        Appointment overlapping = new Appointment(null, patient, LocalDate.now(),
                LocalTime.of(10, 15), LocalTime.of(10, 45), Appointment.Status.ACTIVE);

        patient.addAppointment(first);

        assertThrows(IllegalStateException.class, () -> patient.addAppointment(overlapping));
    }

    @Test
    void testAddAppointmentToDoctorSuccess() {
        Doctor doctor = new Doctor(1L, "Олена", "Петрова", MedicalSpecialty.DERMATOLOGIST,
                LocalTime.of(9, 0), LocalTime.of(17, 0));
        Appointment appointment = new Appointment(doctor, null, LocalDate.now(),
                LocalTime.of(9, 30), LocalTime.of(10, 0), Appointment.Status.ACTIVE);

        doctor.addAppointment(appointment);

        assertEquals(1, doctor.getAppointments().size());
        assertTrue(doctor.getAppointments().contains(appointment));
    }

    @Test
    void testAddAppointmentToDoctorOutOfWorkingHours() {
        Doctor doctor = new Doctor(1L, "Олена", "Петрова", MedicalSpecialty.DERMATOLOGIST,
                LocalTime.of(9, 0), LocalTime.of(17, 0));
        Appointment lateAppointment = new Appointment(doctor, null, LocalDate.now(),
                LocalTime.of(17, 0), LocalTime.of(17, 30), Appointment.Status.ACTIVE);

        assertThrows(IllegalStateException.class, () -> doctor.addAppointment(lateAppointment));
    }
}
