package com.dm.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class DtoGenerator {

    public static String generateDto(String className, String jsonBody) {
        // 🛡️ KORUMA KALKANI
        if (jsonBody == null || jsonBody.trim().isEmpty()) {
            return "// ERROR: JSON Body is empty. Check your cURL parser.\n" +
                    "public class " + className + " { }";
        }

        StringBuilder mainBuilder = new StringBuilder();
        List<String> innerClasses = new ArrayList<>();

        // Package ve Importlar
        mainBuilder.append("package com.generated;\n\n");
        mainBuilder.append("import lombok.*;\n");
        mainBuilder.append("import com.fasterxml.jackson.annotation.JsonProperty;\n");
        mainBuilder.append("import java.util.List;\n\n");

        // Ana Sınıf Başlangıcı
        mainBuilder.append("@Data @Builder @NoArgsConstructor @AllArgsConstructor\n");
        mainBuilder.append("public class ").append(className).append(" {\n\n");

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonBody);

            processFields(root, mainBuilder, innerClasses);

        } catch (Exception e) {
            return "// ERROR PARSING JSON: " + e.getMessage();
        }

        mainBuilder.append("\n");

        // İç içe sınıfları (Nested Objects) ana sınıfın içine ekle
        for (String inner : innerClasses) {
            mainBuilder.append(inner).append("\n");
        }

        mainBuilder.append("}");

        return mainBuilder.toString();
    }

    private static void processFields(JsonNode node, StringBuilder builder, List<String> innerClasses) {
        if (!node.isObject()) return;

        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode value = node.get(fieldName);

            // Tip belirleme
            String type = mapType(fieldName, value, innerClasses);

            // JsonProperty gerekliyse ekle (snake_case durumları için)
            if (fieldName.contains("_")) {
                builder.append("    @JsonProperty(\"").append(fieldName).append("\")\n");
            }

            builder.append("    private ").append(type).append(" ").append(toCamelCase(fieldName)).append(";\n");
        }
    }

    private static String mapType(String fieldName, JsonNode node, List<String> innerClasses) {
        if (node.isInt()) return "Integer";
        if (node.isLong()) return "Long";
        if (node.isBoolean()) return "Boolean";
        if (node.isDouble() || node.isFloat()) return "Double";

        if (node.isArray()) {
            if (node.size() > 0) {
                JsonNode first = node.get(0);
                if (first.isObject()) {
                    String subClassName = capitalize(toCamelCase(fieldName)) + "DTO";
                    generateInnerClass(subClassName, first, innerClasses);
                    return "List<" + subClassName + ">";
                }
                return "List<" + mapType("", first, innerClasses) + ">";
            }
            return "List<Object>";
        }

        if (node.isObject()) {
            // 🔄 RECURSIVE: İç içe obje bulursa yeni bir class yarat
            String subClassName = capitalize(toCamelCase(fieldName)) + "DTO";
            generateInnerClass(subClassName, node, innerClasses);
            return subClassName;
        }

        return "String";
    }

    private static void generateInnerClass(String className, JsonNode node, List<String> innerClasses) {
        // Aynı isimde class zaten üretildiyse (Duplicate kontrolü basitçe)
        if (innerClasses.stream().anyMatch(c -> c.contains("class " + className))) return;

        StringBuilder builder = new StringBuilder();
        builder.append("    @Data @Builder @NoArgsConstructor @AllArgsConstructor\n");
        builder.append("    public static class ").append(className).append(" {\n");

        processFields(node, builder, innerClasses);

        builder.append("    }\n");
        innerClasses.add(builder.toString());
    }

    private static String toCamelCase(String input) {
        if (!input.contains("_")) return input;
        String[] parts = input.split("_");
        StringBuilder result = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            result.append(capitalize(parts[i]));
        }
        return result.toString();
    }

    private static String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}