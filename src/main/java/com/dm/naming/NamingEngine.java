package com.dm.naming;

public class NamingEngine {

    public static String extractResourceName(String url) {

        if (url == null || url.isEmpty()) {
            return "Default";
        }

        String[] parts = url.split("/");

        String lastPart = parts[parts.length - 1];

        if (lastPart.isEmpty() && parts.length > 1) {
            lastPart = parts[parts.length - 2];
        }

        if (lastPart.matches("\\d+")) {

            if (parts.length > 1) {
                lastPart = parts[parts.length - 2];
            }
        }

        return capitalize(lastPart);
    }

    public static String generateServiceName(String resource) {
        return resource + "Service";
    }

    public static String generateMethodName(String httpMethod, String resource) {

        String prefix;

        switch (httpMethod.toUpperCase()) {
            case "POST":
                prefix = "create";
                break;
            case "GET":
                prefix = "get";
                break;
            case "PUT":
                prefix = "update";
                break;
            case "DELETE":
                prefix = "delete";
                break;
            default:
                prefix = "call";
        }

        return prefix + resource;
    }

    private static String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}