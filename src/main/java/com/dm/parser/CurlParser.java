package com.dm.parser;

import com.dm.model.ParsedRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurlParser {

    public static ParsedRequest parse(String curl) {

        String method = "GET";
        String url = "";
        Map<String, String> headers = new HashMap<>();
        String body = null;

        // METHOD
        Pattern methodPattern = Pattern.compile("-X\\s+(\\w+)");
        Matcher methodMatcher = methodPattern.matcher(curl);
        if (methodMatcher.find()) {
            method = methodMatcher.group(1);
        }

        // URL
        Pattern urlPattern = Pattern.compile("(https?://[^\\s']+)");
        Matcher urlMatcher = urlPattern.matcher(curl);
        if (urlMatcher.find()) {
            url = urlMatcher.group(1);
        }

        // HEADERS
        Pattern headerPattern = Pattern.compile("-H\\s+'([^']+)'");
        Matcher headerMatcher = headerPattern.matcher(curl);
        while (headerMatcher.find()) {
            String header = headerMatcher.group(1);
            String[] parts = header.split(":", 2);
            if (parts.length == 2) {
                headers.put(parts[0].trim(), parts[1].trim());
            }
        }

        // BODY
        Pattern bodyPattern = Pattern.compile("-d\\s+'([^']+)'");
        Matcher bodyMatcher = bodyPattern.matcher(curl);
        if (bodyMatcher.find()) {
            body = bodyMatcher.group(1);
        }

        return new ParsedRequest(method, url, headers, body);
    }
}