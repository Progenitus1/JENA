package cz.muni.jena.issue.detectors.compilation_unit.security;

import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.detectors.IssueDetectorTester;
import cz.muni.jena.issue.detectors.compilation_unit.IssueDetector;
import org.junit.jupiter.api.Test;

import java.io.File;

import static cz.muni.jena.Preconditions.verifyCorrectWorkingDirectory;

class InsecureCommunicationDetectorTest extends IssueDetectorTester
{
    private static final String AUTHORIZATION_SERVER_PROJECT = ".." + File.separator + "AuthorizationServer";

    @Test
    void insecureCommunicationDetectorTest()
    {
        verifyCorrectWorkingDirectory();
        Configuration configuration = Configuration.readConfiguration();
        IssueDetector issueDetector = new InsecureCommunicationDetector();
        verifyDetectorFindsIssues(
                new Issue[]{
                        new Issue(
                                IssueType.INSECURE_COMMUNICATION,
                                102,
                                "example.OAuth2AuthorizationServerSecurityConfiguration"
                        ),
                        new Issue(
                                IssueType.INSECURE_COMMUNICATION,
                                103,
                                "example.OAuth2AuthorizationServerSecurityConfiguration"
                        )
                },
                issueDetector,
                configuration,
                AUTHORIZATION_SERVER_PROJECT
        );
    }
}
