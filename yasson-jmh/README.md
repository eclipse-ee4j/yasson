#JMH performance tests for Yasson.

Binaries for performance testing should never be uploaded into Maven repository.

Use of JMH dependency is approved for testing purposes only as "Works with" CQ type, see:

[jmh-core CQ](https://dev.eclipse.org/ipzilla/show_bug.cgi?id=18908),  
[jmh-generator-annprocess CQ](https://dev.eclipse.org/ipzilla/show_bug.cgi?id=18909)

To run tests:
 - build it with maven
 - run from commandline

For all JMH options available:
```
java -jar target/yasson-jmh.jar -h
```

Run examples:
```
java -jar target/yasson-jmh.jar
java -jar target/yasson-jmh.jar -i 3 -t 5 -f 3 -prof stack
java -jar target/yasson-jmh.jar ScalarDataTest.testDeserialize
```