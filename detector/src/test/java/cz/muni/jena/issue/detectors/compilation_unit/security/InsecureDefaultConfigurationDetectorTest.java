package cz.muni.jena.issue.detectors.compilation_unit.security;

import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.detectors.IssueDetectorTester;
import cz.muni.jena.issue.detectors.compilation_unit.IssueDetector;
import org.junit.jupiter.api.Test;

import java.io.File;

class InsecureDefaultConfigurationDetectorTest extends IssueDetectorTester
{
    private static final String AUTHORIZATION_SERVER_PROJECT = ".." + File.separator + "AuthorizationServer";

    @Test
    void insecureDefaultConfigurationDetectorTest()
    {
        Configuration configuration = Configuration.readConfiguration();
        IssueDetector issueDetector = new InsecureDefaultConfigurationDetector();
        verifyDetectorFindsIssues(
                new Issue[] {
                        new Issue(
                                IssueType.INSECURE_DEFAULT_CONFIGURATION,
                                168,
                                "example.OAuth2AuthorizationServerSecurityConfiguration"
                        ),
                        new Issue(
                                IssueType.INSECURE_DEFAULT_CONFIGURATION,
                                169,
                                "example.OAuth2AuthorizationServerSecurityConfiguration"
                        )
                },
                issueDetector,
                configuration,
                AUTHORIZATION_SERVER_PROJECT
        );
    }
}
