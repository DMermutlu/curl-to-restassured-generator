package com.dm.naming;

public class NamingEngine {

    /**
     * Bu metodun ismi artık 'extractResourceName', controller'daki hatayı çözer.
     */
    public static String extractResourceName(String url) {
        if (url == null || url.isEmpty()) {
            return "Default";
        }

        // 1. Query parametrelerini ve tırnakları temizle (? sonrasını at)
        String path = url.replace("'", "").replace("\"", "");
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }

        // 2. Path parçala
        String[] parts = path.split("/");
        String rawResource = "Default";

        // 3. Sondan geriye doğru anlamlı kelimeyi bul
        for (int i = parts.length - 1; i >= 0; i--) {
            String part = parts[i].trim();
            if (part.isEmpty() || part.matches("^\\d+$") || part.matches("^[0-9a-fA-F-]{20,}$")) {
                continue;
            }
            rawResource = part;
            break;
        }

        // 4. Karakter temizliği yap (bulk-create -> BulkCreate)
        return convertToCamelCaseName(rawResource);
    }

    /**
     * Alternatif isim: Controller'da hangisini kullanırsan kullan çalışır.
     */
    public static String cleanResourceName(String url) {
        return extractResourceName(url);
    }

    private static String convertToCamelCaseName(String input) {
        // Alfanümerik olmayan her şeyi (tire, nokta vb.) ayraç kabul et
        String[] words = input.split("[^a-zA-Z0-9]");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase());
            }
        }

        String finalName = result.toString();
        return finalName.isEmpty() ? "Default" : finalName;
    }

    public static String generateServiceName(String resource) {
        return resource + "Service";
    }

    public static String generateMethodName(String httpMethod, String resource) {
        String prefix = switch (httpMethod.toUpperCase()) {
            case "POST" -> "create";
            case "GET" -> "get";
            case "PUT" -> "update";
            case "DELETE" -> "delete";
            default -> "call";
        };
        return prefix + resource;
    }
}