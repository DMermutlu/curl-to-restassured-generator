package com.dm.generator;

import com.dm.model.ParsedRequest;
import com.dm.parser.CurlParser;
import com.dm.naming.NamingEngine;
import com.dm.generator.ServiceGenerator;
import com.dm.writer.JavaFileWriter;
import com.dm.generator.DtoGenerator;

public class CurlGeneratorApp {

    public static void main(String[] args) {

        String curl = "curl -X PUT https://api.example.com/v1/users/123/profile " +
                "-H \"Authorization: Bearer abc123token\" " +
                "-H \"Content-Type: application/json\" " +
                "-H \"X-Custom-Header: customValue\" " +
                "-H \"Accept: application/json\" " +
                "-d '{\"first_name\":\"John\",\"last_name\":\"Doe\",\"age\":28," +
                "\"is_active\":true,\"roles\":[\"admin\",\"user\"]," +
                "\"address\":{\"city\":\"Istanbul\",\"zip_code\":\"34000\"}}'";

        // 1. parse
        ParsedRequest request = CurlParser.parse(curl);

        System.out.println("Method: " + request.getMethod());
        System.out.println("URL: " + request.getUrl());
        System.out.println("Headers: " + request.getHeaders());
        System.out.println("Body: " + request.getBody());

        // 2. naming
        String resource = NamingEngine.extractResourceName(request.getUrl());
        String serviceName = NamingEngine.generateServiceName(resource);
        String methodName = NamingEngine.generateMethodName(request.getMethod(), resource);

        System.out.println("Resource: " + resource);
        System.out.println("Service: " + serviceName);
        System.out.println("Method: " + methodName);

        // 3. service generate
        String serviceCode = ServiceGenerator.generateServiceClass(serviceName, methodName, request);
        System.out.println("\nGenerated Service Code:\n");
        System.out.println(serviceCode);

        JavaFileWriter.writeToFile(serviceName, serviceCode);

        // 4. dto generate
        String dtoName = resource + "RequestDTO";
        String dtoCode = DtoGenerator.generateDto(dtoName, request.getBody());

        System.out.println("\nGenerated DTO Code:\n");
        System.out.println(dtoCode);

        JavaFileWriter.writeToFile(dtoName, dtoCode);

        // 5. README generate
        String readme = ReadmeGenerator.generateReadme();
        JavaFileWriter.writeCustomPath("generated-project/README.md", readme);

        // 6. pom generate
        String pom = PomGenerator.generatePom();
        JavaFileWriter.writeCustomPath("generated-project/pom.xml", pom);

        System.out.println("\n✅ Generation completed successfully!");
    }
}