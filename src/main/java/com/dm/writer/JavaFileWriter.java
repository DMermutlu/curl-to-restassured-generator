package com.dm.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JavaFileWriter {

    public static void writeToFile(String className, String content) {

        String basePath = "generated-project/src/main/java/com/generated/";
        String filePath = basePath + className + ".java";

        try {
            // create new folder if folder is not here
            File directory = new File(basePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            FileWriter writer = new FileWriter(filePath);
            writer.write(content);
            writer.close();

            System.out.println("✅ File created: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeCustomPath(String fullPath, String content) {

        try {
            File file = new File(fullPath);

            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }

            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();

            System.out.println("✅ File created: " + fullPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}