package com.hospital.io.json;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hospital.core.Appointment;
import com.hospital.core.Hospital;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

public class HospitalFileIO {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
    }

    public void saveToFile(Hospital hospital, String filePath, Comparator<Appointment> comparator)
            throws StreamWriteException, DatabindException, IOException {
        ObjectMapper customMapper = new ObjectMapper();
        customMapper.enable(SerializationFeature.INDENT_OUTPUT);
        customMapper.registerModule(new JavaTimeModule());

        SimpleModule module = new SimpleModule();
        module.addSerializer(Hospital.class, new HospitalSerializer(comparator));
        customMapper.registerModule(module);

        customMapper.writeValue(new File(filePath), hospital);
    }

    public void saveToFile(Hospital hospital, String filePath)
            throws StreamReadException, DatabindException, IOException {
        mapper.writeValue(new File(filePath), hospital);
    }

    public Hospital loadFromFile(String filePath) throws StreamReadException, DatabindException, IOException {
        return mapper.readValue(new File(filePath), Hospital.class);
    }
}
