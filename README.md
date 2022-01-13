# Eclipse Yasson

[![Maven Central](https://img.shields.io/maven-central/v/org.eclipse/yasson.svg?label=Maven%20Central)](https://mvnrepository.com/artifact/org.eclipse/yasson)
[![Jakarta Staging (Snapshots)](https://img.shields.io/nexus/s/https/jakarta.oss.sonatype.org/org.eclipse/yasson.svg)](https://jakarta.oss.sonatype.org/content/repositories/staging/org/eclipse/yasson/)
[![Gitter](https://badges.gitter.im/eclipse/yasson.svg)](https://gitter.im/eclipse/yasson)
[![Javadocs](https://www.javadoc.io/badge/org.eclipse/yasson.svg)](https://www.javadoc.io/doc/org.eclipse/yasson)
[![Build Status](https://github.com/eclipse-ee4j/yasson/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/eclipse-ee4j/yasson/actions/workflows/maven.yml?branch=master)
[![License](https://img.shields.io/badge/License-EPL%202.0-green.svg)](https://opensource.org/licenses/EPL-2.0)

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
  .withFormatting(true);

// Create Jsonb with custom configuration
Jsonb jsonb = JsonbBuilder.create(config);

// Use it!
String result = jsonb.toJson(someObject);
```

## Questions?

Something not working right? Have an idea for an enhancement? Get in touch with the Yasson community in the following ways:

- [Gitter](https://gitter.im/eclipse/yasson): a free instant-messaging platform (similar to Slack) that anyone can join.
- [Stackoverflow](https://stackoverflow.com/questions/tagged/yasson): As a question tagged `[jsonb-api]` and `[yasson]`
- [Github Issues](https://github.com/eclipse-ee4j/yasson/issues/new): Open issues for enhancement ideas or bug reports

## Licenses
- [Eclipse Distribution License 1.0 (BSD)](https://projects.eclipse.org/content/eclipse-distribution-license-1.0-bsd)
- [Eclipse Public License 2.0](https://projects.eclipse.org/content/eclipse-public-license-2.0)

## Links
- Yasson home page: https://projects.eclipse.org/projects/ee4j.yasson
- JSON-B official web site: https://eclipse-ee4j.github.io/jsonb-api/
- JSON-B API & spec project: https://github.com/eclipse-ee4j/jsonb-api
- JSR-367 page on JCP site: https://jcp.org/en/jsr/detail?id=367
