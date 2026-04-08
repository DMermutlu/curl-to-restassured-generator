package com.dm.controller;

import com.dm.generator.*;
import com.dm.model.ParsedRequest;
import com.dm.naming.NamingEngine;
import com.dm.parser.CurlParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class GenerateController {

    @PostMapping("/generate")
    public GeneratedResponse generate(@RequestBody CurlRequest request) {

        String curlInput = request.getCurl();

        // 🔥 POSTMAN JSON GELİRSE İÇİNDEN CURL ÇEK
        try {
            if (curlInput != null && curlInput.trim().startsWith("{")) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(curlInput);

                if (node.has("curl")) {
                    curlInput = node.get("curl").asText();
                }
            }
        } catch (Exception e) {
            // ignore
        }

        // 🔥 1. PARSE (DIŞ CURL)
        ParsedRequest parsed = CurlParser.parse(curlInput);

        // 🔥 BODY AL
        String body = parsed.getBody();

        // 🔥 INNER CURL VARSA TEKRAR PARSE ET
        if (body != null && body.contains("curl -X")) {
            ParsedRequest inner = CurlParser.parse(body);
            body = inner.getBody();
        }

        System.out.println("FINAL BODY: " + body);

        // 🔥 NAMING
        String resource = NamingEngine.extractResourceName(parsed.getUrl());
        String serviceName = NamingEngine.generateServiceName(resource);
        String methodName = NamingEngine.generateMethodName(parsed.getMethod(), resource);

        // 🔥 GENERATION
        String service = ServiceGenerator.generateServiceClass(serviceName, methodName, parsed);
        String dto = DtoGenerator.generateDto(resource + "RequestDTO", body);
        String pom = PomGenerator.generatePom();
        String readme = ReadmeGenerator.generateReadme();

        return new GeneratedResponse(service, dto, pom, readme);
    }

    // 🔽 REQUEST BODY CLASS
    static class CurlRequest {
        private String curl;

        public String getCurl() {
            return curl;
        }

        public void setCurl(String curl) {
            this.curl = curl;
        }
    }
}