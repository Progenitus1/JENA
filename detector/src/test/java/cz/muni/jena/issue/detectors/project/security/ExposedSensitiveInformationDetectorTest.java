package cz.muni.jena.issue.detectors.project.security;

import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.detectors.project.ExposedSensitiveInformationDetector;
import cz.muni.jena.issue.detectors.project.ProjectIssueDetector;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static cz.muni.jena.Preconditions.verifyCorrectWorkingDirectory;
import static org.assertj.core.api.Assertions.assertThat;

class ExposedSensitiveInformationDetectorTest
{
    private static final String ANTIPATTERNS_PROJECT = ".." + File.separator + "antipatterns";

    @Test
    void configurationLocationTest()
    {
        verifyCorrectWorkingDirectory();
        ExposedSensitiveInformationDetector issueDetector = new ExposedSensitiveInformationDetector();
        List<String> configurationFiles = issueDetector.findConfigurationFiles(
                ANTIPATTERNS_PROJECT,
                Configuration.readConfiguration()
        );
        assertThat(configurationFiles).hasSize(1);
    }

    @Test
    void exposedSensitiveInformationDetectorTest()
    {
        verifyCorrectWorkingDirectory();
        ProjectIssueDetector issueDetector = new ExposedSensitiveInformationDetector();
        List<Issue> issues = issueDetector.findIssues(
                ANTIPATTERNS_PROJECT,
                Configuration.readConfiguration()
        ).toList();
        assertThat(issues).isEqualTo(
                List.of(
                        new Issue(
                                IssueType.STORING_SECRETS_IN_INSECURE_PLACES,
                                10,
                                String.join(
                                        File.separator,
                                        "..",
                                        "antipatterns",
                                        "src",
                                        "main",
                                        "resources",
                                        "application.yml"
                                )
                        ),
                        new Issue(
                                IssueType.STORING_SECRETS_IN_INSECURE_PLACES,
                                11,
                                String.join(
                                        File.separator,
                                        "..",
                                        "antipatterns",
                                        "src",
                                        "main",
                                        "resources",
                                        "application.yml"
                                )
                        )
                )
        );
    }
}
