package com.dm.parser;

import com.dm.model.ParsedRequest;
import java.util.HashMap;
import java.util.Map;

public class CurlParser {

    public static ParsedRequest parse(String curl) {
        // 1. Kritik Temizlik: Windows ^ işaretlerini, satır sonlarını ve tırnakları temizle
        String cleanCurl = curl.replace("^", " ")
                .replace("\\n", " ")
                .replace("\n", " ")
                .replace("\r", " ")
                .replaceAll("\\s+", " ");

        ParsedRequest request = new ParsedRequest();

        // METHOD
        if (cleanCurl.contains("-X")) {
            String method = cleanCurl.split("-X")[1].trim().split(" ")[0];
            request.setMethod(method.replace("'", "").replace("\"", ""));
        } else {
            request.setMethod("GET");
        }

        // URL (Tırnaklardan arındırılmış)
        request.setUrl(extractUrl(cleanCurl));

        // HEADERS
        request.setHeaders(extractHeaders(cleanCurl));

        // BODY
        request.setBody(extractBody(curl)); // Body için orijinal curl'ü gönderiyoruz (içerik bozulmasın diye)

        return request;
    }

    private static String extractUrl(String cleanCurl) {
        String[] parts = cleanCurl.split(" ");
        for (String part : parts) {
            String p = part.replace("'", "").replace("\"", "");
            if (p.startsWith("http")) {
                return p;
            }
        }
        return "";
    }

    private static Map<String, String> extractHeaders(String cleanCurl) {
        Map<String, String> headers = new HashMap<>();
        String[] parts = cleanCurl.split("-H ");

        for (int i = 1; i < parts.length; i++) {
            String headerPart = parts[i].split(" -")[0].trim();
            headerPart = headerPart.replace("'", "").replace("\"", "");

            if (headerPart.contains(":")) {
                String[] kv = headerPart.split(":", 2);
                headers.put(kv[0].trim(), kv[1].trim());
            }
        }
        return headers;
    }

    private static String extractBody(String curl) {
        // Tüm olası data bayraklarını kontrol et
        String[] dataMarkers = {"--data-raw", "--data-binary", "--data", "-d"};

        for (String marker : dataMarkers) {
            int index = curl.indexOf(marker);
            if (index != -1) {
                // Bayraktan sonrasını al ve temizle
                String bodyPart = curl.substring(index + marker.length()).trim();

                // Eğer body tırnakla başlıyorsa (' veya "), dış tırnakları ayıkla
                if (bodyPart.startsWith("'") || bodyPart.startsWith("\"")) {
                    char quoteChar = bodyPart.charAt(0);
                    int lastQuoteIndex = bodyPart.lastIndexOf(quoteChar);
                    if (lastQuoteIndex > 0) {
                        return bodyPart.substring(1, lastQuoteIndex).trim();
                    }
                    return bodyPart.substring(1).trim();
                }
                return bodyPart;
            }
        }
        return null;
    }
}