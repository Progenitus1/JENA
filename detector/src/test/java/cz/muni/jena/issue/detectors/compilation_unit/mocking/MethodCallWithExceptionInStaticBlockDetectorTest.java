package cz.muni.jena.issue.detectors.compilation_unit.mocking;

import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.detectors.IssueDetectorTester;
import org.junit.jupiter.api.Test;

class MethodCallWithExceptionInStaticBlockDetectorTest extends IssueDetectorTester
{
    @Test
    void staticBlockDetectorTest()
    {
        verifyDetectorFindsIssues(
                new Issue[]{
                        new Issue(
                                IssueType.INAPPROPRIATE_METHOD_CALL_IN_STATIC_BLOCK,
                                19,
                                "com.example.antipatterns.AntiPatterns"
                        )
                },
                new MethodCallWithExceptionInStaticBlockDetector(), Configuration.readConfiguration()
        );
    }
}
