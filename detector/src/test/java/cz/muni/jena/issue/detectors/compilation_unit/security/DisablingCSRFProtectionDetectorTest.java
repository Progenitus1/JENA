package cz.muni.jena.issue.detectors.compilation_unit.security;

import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.detectors.IssueDetectorTester;
import cz.muni.jena.issue.detectors.compilation_unit.IssueDetector;
import org.junit.jupiter.api.Test;

import java.io.File;

class DisablingCSRFProtectionDetectorTest extends IssueDetectorTester
{
    private static final String AUTHORIZATION_SERVER_PROJECT = ".." + File.separator + "AuthorizationServer";

    @Test
    void disablingCSRFProtectionDetectorTest()
    {
        Configuration configuration = Configuration.readConfiguration();
        IssueDetector issueDetector = new DisablingCSRFProtectionDetector();
        verifyDetectorFindsIssues(
                new Issue[] {
                        new Issue(
                                IssueType.DISABLING_CSRF_PROTECTION,
                                86,
                                "example.OAuth2AuthorizationServerSecurityConfiguration"
                        ),
                        new Issue(
                                IssueType.DISABLING_CSRF_PROTECTION,
                                88,
                                "example.OAuth2AuthorizationServerSecurityConfiguration"
                        ),
                        new Issue(
                                IssueType.DISABLING_CSRF_PROTECTION,
                                89,
                                "example.OAuth2AuthorizationServerSecurityConfiguration"
                        )
                },
                issueDetector,
                configuration,
                AUTHORIZATION_SERVER_PROJECT
        );
    }
}
