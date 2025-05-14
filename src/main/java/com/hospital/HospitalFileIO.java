package com.hospital;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

public class HospitalFileIO {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
    }

    public void saveToFile(Hospital hospital, String filePath)
            throws StreamReadException, DatabindException, IOException {
        mapper.writeValue(new File(filePath), hospital);
    }

    public Hospital loadFromFile(String filePath) throws StreamReadException, DatabindException, IOException {
        return mapper.readValue(new File(filePath), Hospital.class);
    }
}
