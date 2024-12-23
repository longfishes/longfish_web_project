package service;

import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import static constant.CommonConstant.DefaultData;
import static constant.CommonConstant.FilePath;

public class StudentService {

    private static final Random random = new Random();

    public static Vector<Vector<String>> loads() {
        return readDataFromFile();
    }

    public static void save(Vector<Vector<String>> data) {
        writeDataToFile(data);
    }

    private static void writeDataToFile(Vector<Vector<String>> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FilePath))) {
            for (Vector<String> row : data) {
                writer.write(String.join("\t", row));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Vector<Vector<String>> readDataFromFile() {
        Vector<Vector<String>> data = new Vector<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Vector<String> row = new Vector<>(List.of(line.split("\t")));
                data.add(row);
            }
        } catch (IOException e) {
            writeDataToFile(DefaultData);
            return DefaultData;
        }

        return data;
    }

    public static String randomId() {
        int randomNumber = 100000 + random.nextInt(900000);
        return String.valueOf(randomNumber);
    }
}
