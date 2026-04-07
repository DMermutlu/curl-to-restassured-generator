package com.dm.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;

public class DtoGenerator {

    public static String generateDto(String className, String jsonBody) {

        StringBuilder builder = new StringBuilder();

        builder.append("package com.generated;\n\n");

        builder.append("import com.fasterxml.jackson.annotation.JsonProperty;\n");
        builder.append("import com.fasterxml.jackson.databind.PropertyNamingStrategies;\n");
        builder.append("import com.fasterxml.jackson.databind.annotation.JsonNaming;\n\n");

        builder.append("@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)\n");

        builder.append("public class ").append(className).append(" {\n\n");

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonBody);

            Iterator<String> fieldNames = root.fieldNames();

            while (fieldNames.hasNext()) {
                String field = fieldNames.next();
                JsonNode value = root.get(field);

                String type = mapType(value);

                if (needsJsonProperty(field)) {
                    builder.append("    @JsonProperty(\"")
                            .append(field)
                            .append("\")\n");
                }

                builder.append("    private ")
                        .append(type)
                        .append(" ")
                        .append(toCamelCase(field))
                        .append(";\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.append("\n}");

        return builder.toString();
    }

    private static String mapType(JsonNode node) {
        if (node.isInt()) return "int";
        if (node.isBoolean()) return "boolean";
        return "String";
    }
    private static boolean needsJsonProperty(String fieldName) {
        return !fieldName.equals(fieldName.toLowerCase()) || fieldName.contains("_");
    }

    private static String toCamelCase(String input) {

        String normalized = input.toLowerCase();

        //special case
        if (normalized.equalsIgnoreCase("e_mail")) return "email";
        if (normalized.equalsIgnoreCase("user_id")) return "userId";

        String[] parts = normalized.split("_");

        StringBuilder result = new StringBuilder(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            result.append(parts[i].substring(0, 1).toUpperCase())
                    .append(parts[i].substring(1));
        }

        return result.toString();
    }
}