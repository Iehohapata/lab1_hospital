package com.hospital;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class HospitalDeserializer extends StdDeserializer<Hospital> {

    public HospitalDeserializer() {
        super(Hospital.class);
    }

    @Override
    public Hospital deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = jp.getCodec();
        JsonNode root = codec.readTree(jp);

        Hospital hospital = new Hospital();

        JsonNode doctorsNode = root.get("doctors");
        if (doctorsNode != null && doctorsNode.isArray()) {
            for (JsonNode node : doctorsNode) {
                Doctor doctor = codec.treeToValue(node, Doctor.class);
                hospital.addDoctor(doctor);
            }
        }

        JsonNode patientsNode = root.get("patients");
        if (patientsNode != null && patientsNode.isArray()) {
            for (JsonNode node : patientsNode) {
                Patient patient = codec.treeToValue(node, Patient.class);
                hospital.addPatient(patient);
            }
        }

        JsonNode appointmentsNode = root.get("appointments");
        if (appointmentsNode != null && appointmentsNode.isArray()) {
            for (JsonNode apptNode : appointmentsNode) {
                long doctorId = apptNode.get("doctor").asLong();
                long patientId = apptNode.get("patient").asLong();
                LocalDate date = LocalDate.parse(apptNode.get("date").asText());
                Appointment.Status status = Appointment.Status.valueOf(apptNode.get("status").asText());
                LocalTime start = LocalTime.parse(apptNode.get("start").asText());
                LocalTime end = LocalTime.parse(apptNode.get("end").asText());

                Doctor doctor = hospital.getDoctor(doctorId).get();
                Patient patient = hospital.getPatient(patientId).get();

                if (doctor == null || patient == null) {
                    throw new RuntimeException("Invalid doctorId or patientId in appointment");
                }

                Appointment appointment = new Appointment(doctor, patient, date, start, end, status);
                doctor.addAppointment(appointment);
                patient.addAppointment(appointment);
            }
        }

        return hospital;
    }
}
