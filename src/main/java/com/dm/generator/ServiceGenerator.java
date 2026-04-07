package com.dm.generator;

import com.dm.model.ParsedRequest;

public class ServiceGenerator {

    public static String generateServiceClass(String serviceName, String methodName, ParsedRequest request) {

        String baseUrl = extractBaseUrl(request.getUrl());
        String path = extractPath(request.getUrl());

        StringBuilder headersBuilder = new StringBuilder();

        request.getHeaders().forEach((k, v) -> {
            if (k.equalsIgnoreCase("Content-Type")) {
                headersBuilder.append("                .contentType(ContentType.JSON)\n");
            } else {
                headersBuilder
                        .append("                .header(\"")
                        .append(k)
                        .append("\", \"")
                        .append(v)
                        .append("\")\n");
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
                headersBuilder +
                "                .body(body)\n" +
                "        .when()\n" +
                "                ." + request.getMethod().toLowerCase() + "(\"" + path + "\")\n" +
                "        .then()\n" +
                "                .extract()\n" +
                "                .response();\n" +
                "    }\n\n" +
                "}";
    }

    private static String extractBaseUrl(String fullUrl) {
        String[] parts = fullUrl.split("/", 4);
        return parts[0] + "//" + parts[2];
    }

    private static String extractPath(String fullUrl) {
        String[] parts = fullUrl.split("/", 4);
        return "/" + parts[3];
    }
}