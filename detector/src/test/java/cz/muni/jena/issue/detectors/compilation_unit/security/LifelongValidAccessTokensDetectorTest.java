package cz.muni.jena.issue.detectors.compilation_unit.security;

import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.detectors.IssueDetectorTester;
import cz.muni.jena.issue.detectors.compilation_unit.IssueDetector;
import cz.muni.jena.issue.detectors.compilation_unit.security.token_lifetime.LifelongValidAccessTokensDetector;
import org.junit.jupiter.api.Test;

import java.io.File;

class LifelongValidAccessTokensDetectorTest extends IssueDetectorTester
{
    private static final String AUTHORIZATION_SERVER_PROJECT = ".." + File.separator + "AuthorizationServer";

    @Test
    void lifelongValidAccessTokensDetectorTest()
    {
        Configuration configuration = Configuration.readConfiguration();
        IssueDetector issueDetector = new LifelongValidAccessTokensDetector();
        verifyDetectorFindsIssues(
                new Issue[] {
                        new Issue(
                                IssueType.LIFELONG_ACCESS_TOKENS,
                                116,
                                "example.OAuth2AuthorizationServerSecurityConfiguration"
                        )
                },
                issueDetector,
                configuration,
                AUTHORIZATION_SERVER_PROJECT
        );
    }
}
