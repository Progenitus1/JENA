#Jena
Jena is application for detection of anti-patterns with focus especially on enterprise specific anti-patterns.  
The detector module is the one capable of the detection of anti-patterns.
The other modules are there as examples of anti-patterns Jena can detect, and they intentionally contain many problems.
For more information on how to use Jena please see [README of the detector module](./detector/README.md).  
The Antipatterns module contains most of the anti-patterns, but we were force to move some of them out of it. Use of the
Powermock had to be moved out because if we would use it in Antipatterns module it would interfere with regular mocking.
AuthorizationServer is a authorization server for the Antipatterns web application that is the reason why it is separate.
It contains many security anti-patterns. We created TestGradleProject module because all other modules use maven and we
need Jena to work with both Gradle and Maven projects.  
Build Jena using following command:
```shell
mvn package -DskipTests
```
