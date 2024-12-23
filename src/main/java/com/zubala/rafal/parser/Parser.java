package com.zubala.rafal.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    private final static int YEAR = 2025;
    private final static String RESOURCES = "resources";
    private final static String BIO_ZMIESZANE_FILE = "zmieszane_bio.txt";
    private final static String CHOINKI_FILE = "choinki.txt";
    private final static String PAPIER_PLASTIKI_FILE = "papier_metale_plastiki.txt";
    private final static String GABARYTY_FILE = "gabaryty.txt";
    private final static String SZKŁO_FILE = "szkło.txt";
    private final static String ZIELONE_FILE = "zielone.txt";
    private final static String CSV_FILENAME = "garbages_2025.csv";
    private final static String CSV_HEADER = "Subject,Start Date,All Day Event,Description";
    private final static String SUBJECT = "Odbiór śmieci";

    enum GarbageType {
        BIO_MIXED(1, "zmieszane i biodegradowalne"),
        TREE(2, "choinki"),
        PAPER_PLASTIC(3, "papier, metale i tworzywa sztuczne"),
        GARBAGE(4, "gabaryty"),
        GREEN(5, "zielone"),
        GLASS(6, "opakowania szklane");
        private final String description;
        private final int id;

        private GarbageType(int id, String description) {
            this.description = description;
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public int getId() {
            return id;
        }
    }

    private final Map<LocalDate, List<GarbageType>> map = new HashMap<>();

    public void process() {
        readFiles();
        writeCsv();
    }

    private void writeCsv() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILENAME, StandardCharsets.UTF_8));
            writer.append(CSV_HEADER);
            writer.newLine();

            map.keySet().stream().sorted().forEach(key -> {
                try {
                    writer.append(SUBJECT);
                    writer.append(",");
                    writer.append(key.toString());
                    writer.append(",");
                    writer.append("TRUE");
                    writer.append(",");
                    writer.append("\"");
                    writer.append(map.get(key).stream().sorted().map(GarbageType::getDescription).collect(Collectors.joining(", ")));
                    writer.append("\"");
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFiles() {
        readFile(BIO_ZMIESZANE_FILE, GarbageType.BIO_MIXED);
        readFile(PAPIER_PLASTIKI_FILE, GarbageType.PAPER_PLASTIC);
        readFile(SZKŁO_FILE, GarbageType.GLASS);
        readFile(GABARYTY_FILE, GarbageType.GARBAGE);
        readFile(CHOINKI_FILE, GarbageType.TREE);
        readFile(ZIELONE_FILE, GarbageType.GREEN);
    }

    private void readFile(String filename, GarbageType type) {
        try {
            Scanner scanner = new Scanner(new File(RESOURCES + "/" + filename));
            while (scanner.hasNextLine()) {
                String code = scanner.nextLine();
                LocalDate date = buildDate(code);
                List<GarbageType> types = Optional.ofNullable(map.get(date)).orElse(new LinkedList<>());
                if (types.isEmpty()) {
                    map.put(date, types);
                }
                types.add(type);
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static LocalDate buildDate(String code) {
        String[] parts = code.split("\\.");
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[0]);
        return LocalDate.of(YEAR, month, day);
    }
}
