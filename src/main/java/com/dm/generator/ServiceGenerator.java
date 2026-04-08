package com.dm.generator;

import com.dm.model.ParsedRequest;
import java.net.URI;

public class ServiceGenerator {

    public static String generateServiceClass(String serviceName, String methodName, ParsedRequest request) {
        String fullUrl = request.getUrl();
        String baseUrl = extractBaseUrl(fullUrl);
        String path = extractPath(fullUrl);

        StringBuilder headersBuilder = new StringBuilder();
        request.getHeaders().forEach((k, v) -> {
            if (k.equalsIgnoreCase("Content-Type")) {
                headersBuilder.append("                .contentType(ContentType.JSON)\n");
            } else {
                headersBuilder.append("                .header(\"").append(k).append("\", \"").append(v).append("\")\n");
            }
        });

        return "package com.generated;\n\n" +
                "import io.restassured.response.Response;\n" +
                "import static io.restassured.RestAssured.given;\n" +
                "import io.restassured.http.ContentType;\n\n" +
                "public class " + serviceName + " {\n\n" +
                "    public static Response " + methodName + "(String body) {\n" +
                "        return given()\n" +
                "                .baseUri(\"" + baseUrl + "\")\n" +
                headersBuilder.toString() +
                "                .body(body)\n" +
                "                .when()\n" +
                "                ." + request.getMethod().toLowerCase() + "(\"" + path + "\")\n" +
                "                .then()\n" +
                "                .extract()\n" +
                "                .response();\n" +
                "    }\n" +
                "}";
    }

    private static String extractBaseUrl(String fullUrl) {
        try {
            if (fullUrl == null || !fullUrl.contains("://")) return "https://api.example.com";
            URI uri = new URI(fullUrl);
            return uri.getScheme() + "://" + uri.getHost() + (uri.getPort() != -1 ? ":" + uri.getPort() : "");
        } catch (Exception e) {
            return "https://api.example.com";
        }
    }

    private static String extractPath(String fullUrl) {
        try {
            if (fullUrl == null || !fullUrl.contains("://")) return "/";
            URI uri = new URI(fullUrl);
            String path = uri.getPath();
            return (path == null || path.isEmpty()) ? "/" : path;
        } catch (Exception e) {
            return "/";
        }
    }
}