[![Build Status](https://travis-ci.org/eclipse-ee4j/yasson.svg?branch=master)](https://travis-ci.org/eclipse-ee4j/yasson)
[![License](https://img.shields.io/badge/License-EPL%201.0-green.svg)](https://opensource.org/licenses/EPL-1.0)
[![Maven Central](https://img.shields.io/maven-central/v/org.eclipse/yasson.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.eclipse%22%20a%3A%22yasson%22)

# Yasson
Yasson is a Java framework which provides a standard binding layer between Java classes and JSON documents. This is similar to what JAXB is doing in the XML world. Yasson is an official reference implementation of JSON Binding ([JSR-367](https://jcp.org/en/jsr/detail?id=367)).

It defines a **default mapping** algorithm for converting existing Java classes to JSON suitable for the most cases:

```java
Jsonb jsonb = JsonbBuilder.create();
String result = jsonb.toJson(someObject);
```

For whom it's not enough it provides rich customization abilities through a set of **annotations** and rich **programmatic API**: 

```java
// Create custom configuration
JsonbConfig config = new JsonbConfig()
  .withNullValues(true)
  .withFormating(true);

// Create Jsonb with custom configuration
Jsonb jsonb = JsonbBuilder.create(config);

// Use it!
String result = jsonb.toJson(someObject);
```

## Licenses
- [Eclipse Distribution License 1.0 (BSD)](https://projects.eclipse.org/content/eclipse-distribution-license-1.0-bsd)
- [Eclipse Public License 1.0](https://projects.eclipse.org/content/eclipse-public-license-1.0)

## Links
- Yasson home page: https://projects.eclipse.org/projects/ee4j.yasson
- JSON-B official web site: http://json-b.net
- JSON-B API & spec project: https://github.com/eclipse-ee4j/jsonb-api
- JSR-367 page on JCP site: https://jcp.org/en/jsr/detail?id=367
