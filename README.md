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

##Licenses
- [Eclipse Distribution License 1.0 (BSD)](https://projects.eclipse.org/content/eclipse-distribution-license-1.0-bsd)
- [Eclipse Public License 1.0](https://projects.eclipse.org/content/eclipse-public-license-1.0)

##Links
- JSON-B official web site: https://json-b.net
- Specification project: http://jsonb-spec.java.net
- JSR-367 page on JCP site: https://jcp.org/en/jsr/detail?id=367
