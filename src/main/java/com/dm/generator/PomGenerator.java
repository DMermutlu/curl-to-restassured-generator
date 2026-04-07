package com.dm.generator;

public class PomGenerator {

    public static String generatePom() {

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <groupId>com.generated</groupId>\n" +
                "    <artifactId>generated-api-test</artifactId>\n" +
                "    <version>1.0-SNAPSHOT</version>\n" +
                "\n" +
                "    <properties>\n" +
                "        <maven.compiler.source>17</maven.compiler.source>\n" +
                "        <maven.compiler.target>17</maven.compiler.target>\n" +
                "    </properties>\n" +
                "\n" +
                "    <dependencies>\n" +
                "\n" +
                "        <!-- RestAssured -->\n" +
                "        <dependency>\n" +
                "            <groupId>io.rest-assured</groupId>\n" +
                "            <artifactId>rest-assured</artifactId>\n" +
                "            <version>5.4.0</version>\n" +
                "        </dependency>\n" +
                "\n" +
                "        <!-- Jackson -->\n" +
                "        <dependency>\n" +
                "            <groupId>com.fasterxml.jackson.core</groupId>\n" +
                "            <artifactId>jackson-databind</artifactId>\n" +
                "            <version>2.17.0</version>\n" +
                "        </dependency>\n" +
                "\n" +
                "    </dependencies>\n" +
                "\n" +
                "</project>";
    }
}