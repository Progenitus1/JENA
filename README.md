#Jena
Jena is application for detection of anti-patterns with focus especially on enterprise specific anti-patterns. 
It has the following modules: 
- The detector module is the one capable of the detection of anti-patterns.
- The anti-pattern module contains many examples of anti-pattern.
- PowerMockUsage module had to be separated out because if we would leave it in Antipatterns module it would interfere with regular mocking. It contains example if Powermock usage.
- AuthorizationServer is a authorization server for the Antipatterns web application that is the reason why it is separate. It contains many security anti-patterns.
- TestGradleProject module was created because all other modules use maven, and we
  need Jena to work with both Gradle and Maven projects.

**So in short, detector module is the one capable of detection anti-patterns
and other modules intentionally contain anti-pattern for purposes of detector module tests.**

For more information on how to use Jena please see [README of the detector module](./detector/README.md).

Build Jena using following command:
```shell
mvn package -DskipTests
```
When building Jena we skip tests because PowerMockUsage module only works with Java 11
while the rest of the project uses Java 17. So if we would build the project without
skipping tests then tests in PowerMockUsage module would fail.
