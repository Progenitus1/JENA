package cz.muni.jena.issue.detectors.compilation_unit.security;

import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.detectors.IssueDetectorTester;
import cz.muni.jena.issue.detectors.compilation_unit.IssueDetector;
import org.junit.jupiter.api.Test;

import java.io.File;

class SigningJWTWithFixedSecretDetectorTest extends IssueDetectorTester
{
    private static final String AUTHORIZATION_SERVER_PROJECT = ".." + File.separator + "AuthorizationServer";

    @Test
    void signingJWTWithFixedSecretDetectorTest()
    {
        Configuration configuration = Configuration.readConfiguration();
        IssueDetector issueDetector = new SigningJWTWithFixedSecretDetector();
        verifyDetectorFindsIssues(
                new Issue[] {
                        new Issue(
                                IssueType.SIGNING_JWT_WITH_FIXED_SECRET,
                                195,
                                "example.OAuth2AuthorizationServerSecurityConfiguration"
                        ),
                        new Issue(
                                IssueType.SIGNING_JWT_WITH_FIXED_SECRET,
                                205,
                                "example.OAuth2AuthorizationServerSecurityConfiguration"
                        )
                },
                issueDetector,
                configuration,
                AUTHORIZATION_SERVER_PROJECT
        );
    }
}
