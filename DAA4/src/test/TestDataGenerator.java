package test;

import data.DataGenerator;
import java.io.File;

public class TestDataGenerator {
    public static void main(String[] args) throws Exception {
        System.out.println("=== TestDataGenerator ===");
        DataGenerator.generateDatasets();
        File dir = new File("gendata");
        if (!dir.exists()) {
            System.out.println("❌ gendata/ folder not created");
        } else {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            System.out.println("Generated files:");
            for (File f : files) System.out.println(" - " + f.getName());
        }
    }
}
