package com.hospital;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class HospitalSerializer extends StdSerializer<Hospital> {
    public HospitalSerializer() {
        super(Hospital.class);
    }

    @Override
    public void serialize(Hospital hospital, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();

        jgen.writeFieldName("doctors");
        jgen.writeStartArray();
        for (Doctor doctor : hospital.getAllDoctors()) {
            jgen.writeStartObject();
            jgen.writeNumberField("id", doctor.getId());
            jgen.writeStringField("firstName", doctor.getFirstName());
            jgen.writeStringField("lastName", doctor.getLastName());
            jgen.writeStringField("specialization", doctor.getSpecialization().name());
            jgen.writeStringField("workStart", doctor.getWorkStart().toString());
            jgen.writeStringField("workEnd", doctor.getWorkEnd().toString());
            jgen.writeEndObject();
        }
        jgen.writeEndArray();

        jgen.writeFieldName("patients");
        jgen.writeStartArray();
        for (Patient patient : hospital.getAllPatients()) {
            jgen.writeStartObject();
            jgen.writeNumberField("id", patient.getId());
            jgen.writeStringField("firstName", patient.getFirstName());
            jgen.writeStringField("lastName", patient.getLastName());
            jgen.writeStringField("dateOfBirth", patient.getDateOfBirth().toString());
            jgen.writeEndObject();
        }
        jgen.writeEndArray();

        jgen.writeFieldName("appointments");
        jgen.writeStartArray();

        List<Appointment> appointments = hospital.getAllDoctors().stream()
                .flatMap(doctor -> doctor.getAppointments().stream())
                .collect(Collectors.toList());
        for (Appointment appointment : appointments) {
            jgen.writeStartObject();
            jgen.writeNumberField("doctor", appointment.getDoctor().getId());
            jgen.writeNumberField("patient", appointment.getPatient().getId());
            jgen.writeStringField("date", appointment.getDate().toString());
            jgen.writeStringField("status", appointment.getStatus().name());
            jgen.writeStringField("start", appointment.getStart().toString());
            jgen.writeStringField("end", appointment.getEnd().toString());
            jgen.writeEndObject();
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }
}
