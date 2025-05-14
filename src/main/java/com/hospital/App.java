package com.hospital;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.hospital.core.Appointment;
import com.hospital.core.Doctor;
import com.hospital.core.Hospital;
import com.hospital.core.MedicalSpecialty;
import com.hospital.core.Patient;
import com.hospital.io.json.AppointmentComparator;
import com.hospital.io.json.HospitalFileIO;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final HospitalFileIO jsonHandler = new HospitalFileIO();
    private static Hospital hospital = new Hospital();
    private static final String OPTION_LIST = """
                <-- Функціональності -->
                1. Пошук лікарів за спеціальністю
                2. Записатись на прийом
                3. Вивести актуальну інформацію
                <-- CRUD пацієнтів -->
                4. Створити пацієнта
                5. Знайти пацієнта
                6. Вивести список пацієнтів
                7. Оновити дані про пацієнта
                8. Видалити пацієнта
                <-- CRUD лікарів -->
                9. Створити лікаря
                10. Знайти лікаря
                11. Вивести список лікарів
                12. Оновити дані про лікаря
                13. Звільнити лікаря
                <-- Файлові операції -->
                14. Експорт з JSON
                15. Імпорт у JSON
            """;
    private static final String defaultFileImportPath = "src/main/resources/example.json";
    private static final String defaultFileExportPath = "src/main/resources/exported.json";

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println(OPTION_LIST);
            String option = getConsoleInput("Оберіть опцію: ");
            switch (option) {
                case "1" -> handleFindDoctorsBySpecialty();
                case "2" -> handleCreateAppointment();
                case "3" -> handleReport();
                case "4" -> handleCreatePatient();
                case "5" -> handleFindPatient();
                case "6" -> handleListPatients();
                case "7" -> handleUpdatePatient();
                case "8" -> handleDeletePatient();
                case "9" -> handleCreateDoctor();
                case "10" -> handleFindDoctor();
                case "11" -> handleListDoctors();
                case "12" -> handleUpdateDoctor();
                case "13" -> handleDeleteDoctor();
                case "14" -> handleFileRead();
                case "15" -> handleFileWrite();
                case "0" -> running = false;
                default -> printError();
            }
        }
        scanner.close();
    }

    private static String getConsoleInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        return input;
    }

    private static void handleCreatePatient() {
        String firstName = getConsoleInput("Введіть ім'я пацієнта: ");
        String lastName = getConsoleInput("Введіть прізвище пацієнта: ");
        String dobInput = getConsoleInput(
                "Введіть дату народження (рррр-мм-дд) (aбо Enter): ");

        LocalDate dob;
        if (dobInput.isBlank()) {
            dob = LocalDate.now().minusYears(20);
        } else {
            dob = LocalDate.parse(dobInput);
        }

        Patient created = hospital.createPatient(firstName, lastName, dob);
        System.out.println("Пацієнта створено успішно. ID: " + created.getId());
    }

    private static void handleFindPatient() {
        String input = getConsoleInput("Введіть ID пацієнта: ");
        try {
            Long id = Long.parseLong(input);
            Patient patient = hospital.getPatient(id)
                    .orElseThrow(NoSuchElementException::new);
            System.out.println(patient);
        } catch (NumberFormatException | NoSuchElementException e) {
            printError();
        }
    }

    private static void handleListPatients() {
        hospital.getAllPatients().forEach(System.out::println);
    }

    private static void handleUpdatePatient() {
        String input = getConsoleInput("Введіть ID пацієнта: ");
        try {
            Long id = Long.parseLong(input);
            String firstName = getConsoleInput("Нове ім'я (або Enter): ");
            String lastName = getConsoleInput("Нове прізвище (або Enter): ");
            String dobInput = getConsoleInput("Нова дата народження (рррр-мм-дд) (або Enter): ");
            LocalDate dob = dobInput.isBlank() ? null : LocalDate.parse(dobInput);
            hospital.updatePatient(id, firstName.isBlank() ? null : firstName,
                    lastName.isBlank() ? null : lastName,
                    dob);
            System.out.println("Пацієнта оновлено успішно.");
        } catch (NumberFormatException | NoSuchElementException e) {
            printError();
        }
    }

    private static void handleDeletePatient() {
        String input = getConsoleInput("Введіть ID пацієнта: ");
        try {
            Long id = Long.parseLong(input);
            hospital.deletePatient(id);
            System.out.println("Пацієнта видалено успішно.");
        } catch (NumberFormatException | NoSuchElementException e) {
            printError();
        }
    }

    private static void handleCreateDoctor() {
        String firstName = getConsoleInput("Введіть ім'я лікаря: ");
        String lastName = getConsoleInput("Введіть прізвище лікаря: ");

        displayMedicalSpecialitiesMenu();
        int specialtyIndex = getValidSpecialityInput();
        MedicalSpecialty specialty = MedicalSpecialty.values()[specialtyIndex - 1];

        String startInput = getConsoleInput("Початок робочого дня (гг:хх) (або Enter): ");
        String endInput = getConsoleInput("Кінець робочого дня (гг:хх) (або Enter): ");
        LocalTime start = startInput.isBlank() ? LocalTime.of(9, 0) : LocalTime.parse(startInput);
        LocalTime end = endInput.isBlank() ? LocalTime.of(17, 0) : LocalTime.parse(endInput);

        Doctor doctor = hospital.createDoctor(firstName, lastName, specialty, start, end);
        System.out.println("Лікаря створено успішно. ID: " + doctor.getId());
    }

    private static void handleFindDoctor() {
        String input = getConsoleInput("Введіть ID лікаря: ");
        try {
            Long id = Long.parseLong(input);
            Doctor doctor = hospital.getDoctor(id)
                    .orElseThrow(NoSuchElementException::new);
            System.out.println(doctor);
        } catch (NumberFormatException | NoSuchElementException e) {
            printError();
        }
    }

    private static void handleListDoctors() {
        hospital.getAllDoctors().forEach(System.out::println);
    }

    private static void handleUpdateDoctor() {
        String input = getConsoleInput("Введіть ID лікаря: ");
        try {
            Long id = Long.parseLong(input);
            String firstName = getConsoleInput("Нове ім'я (або Enter): ");
            String lastName = getConsoleInput("Нове прізвище (або Enter): ");
            String startInput = getConsoleInput("Новий початок робочого дня (гг:хх) або Enter: ");
            String endInput = getConsoleInput("Новий кінець робочого дня (гг:хх) або Enter: ");

            LocalTime start = startInput.isBlank() ? null : LocalTime.parse(startInput);
            LocalTime end = endInput.isBlank() ? null : LocalTime.parse(endInput);

            hospital.updateDoctor(id,
                    firstName.isBlank() ? null : firstName,
                    lastName.isBlank() ? null : lastName,
                    start, end);

            System.out.println("Дані лікаря оновлено успішно.");
        } catch (NumberFormatException | NoSuchElementException e) {
            printError();
        }
    }

    private static void handleDeleteDoctor() {
        String input = getConsoleInput("Введіть ID лікаря: ");
        try {
            Long id = Long.parseLong(input);
            hospital.deleteDoctor(id);
            System.out.println("Лікаря звільнено успішно.");
        } catch (NumberFormatException | NoSuchElementException e) {
            printError();
        }
    }

    private static void handleFindDoctorsBySpecialty() {
        displayMedicalSpecialitiesMenu();
        MedicalSpecialty[] specialties = MedicalSpecialty.values();

        int option = getValidSpecialityInput();

        var result = hospital.findDoctorsBySpecialty(specialties[option - 1]);

        if (result.isEmpty()) {
            System.out.println("Не знайдено лікарів із заданою спеціальністю");
        } else {
            result.forEach(System.out::println);
        }
    }

    private static void handleCreateAppointment() {
        Patient patient;
        while (true) {
            try {
                hospital.getAllPatients().forEach(System.out::println);
                String input = getConsoleInput("Оберіть пацієнта: ");
                Long patientId = Long.parseLong(input);
                patient = hospital.getPatient(patientId).orElseThrow();
                break;
            } catch (Exception e) {
                printError();
            }
        }

        Doctor doctor;
        while (true) {
            try {
                hospital.getAllDoctors().forEach(System.out::println);
                String input = getConsoleInput("Оберіть лікаря: ");
                Long doctorId = Long.parseLong(input);
                doctor = hospital.getDoctor(doctorId).orElseThrow();
                break;
            } catch (Exception e) {
                printError();
            }
        }

        var visit = hospital.createNearestAvailableAppointment(patient, doctor);
        System.out.println("Запис до лікаря успішно створено. Деталі запису:\n" + visit.toString());
    }

    public static void handleReport() {
        System.out.println(hospital.generateFullReport());
    }

    public static void handleFileRead() {
        String filePath = getConsoleInput("Вкажіть шлях до файлу(Enter: resources/example.json): ");

        if (filePath.isBlank() || filePath.isEmpty()) {
            filePath = defaultFileImportPath;
        }

        try {
            hospital = jsonHandler.loadFromFile(filePath);
            System.out.println("Дані зчитані успішно\n");
        } catch (IOException e) {
            System.out.println("Не вдалось коректно зчитати вкзаний файл\n");
        }
    }

    public static void handleFileWrite() {
        String filePath = getConsoleInput("Вкажіть шлях до файлу(Enter: resources/exported.json): ");

        if (filePath.isBlank() || filePath.isEmpty()) {
            filePath = defaultFileExportPath;
        }

        AppointmentComparator sortingStrategy = getValidAppointmentComparator();

        try {
            jsonHandler.saveToFile(hospital, filePath, sortingStrategy.getComparator());
            System.out.println("Дані записано успішно\n");
        } catch (IOException e) {
            System.out.println("Помилка запису даних");
        }
    }

    private static int getValidSpecialityInput() {
        MedicalSpecialty[] specialties = MedicalSpecialty.values();

        int option;
        while (true) {
            try {
                String input = getConsoleInput("Оберіть спеціальність: ");
                option = Integer.parseInt(input);
                if (option < 1 || option > specialties.length) {
                    printError();
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                printError();
            }
        }

        return option;
    }

    private static AppointmentComparator getValidAppointmentComparator() {
        AppointmentComparator[] comparators = AppointmentComparator.values();

        for (int i = 0; i < comparators.length; i++) {
            System.out.printf("%d — %s%n", i + 1, comparators[i].getDisplayName());
        }

        int option;
        while (true) {
            try {
                String input = getConsoleInput("Оберіть критерій сортування: ");
                option = Integer.parseInt(input);
                if (option < 1 || option > comparators.length) {
                    printError();
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                printError();
            }
        }

        return comparators[option - 1];
    }

    private static void displayMedicalSpecialitiesMenu() {
        System.out.println("<-- Оберіть спеціальність -->");
        MedicalSpecialty[] values = MedicalSpecialty.values();
        for (int i = 0; i < values.length; i++) {
            System.out.printf("%d. %s%n", i + 1, values[i].getUkrainianName());
        }
    }

    private static void printError() {
        System.out.println("Хибне введення");
    }
}
