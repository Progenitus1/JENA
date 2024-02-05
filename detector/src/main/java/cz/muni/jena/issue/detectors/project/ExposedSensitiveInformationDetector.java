package cz.muni.jena.issue.detectors.project;

import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component("exposedSensitiveInformationDetector")
public class ExposedSensitiveInformationDetector implements ProjectIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.STORING_SECRETS_IN_INSECURE_PLACES;

    @Override
    public Stream<Issue> findIssues(String projectPath, Configuration configuration)
    {
        Pattern exposedSensitiveInformationRegex = Pattern.compile(
                configuration.securityConfiguration().sensitiveInformationRegex()
        );
        List<String> configurationFiles = findConfigurationFiles(projectPath, configuration);
        Map<String, List<String>> allLines = getLines(configurationFiles);

        return findAllLinesMatchingRegex(
                allLines,
                IssueType.STORING_SECRETS_IN_INSECURE_PLACES,
                List.of(exposedSensitiveInformationRegex)
        );
    }

    public List<String> findConfigurationFiles(String projectPath, Configuration configuration)
    {
        Pattern configFileRegex = Pattern.compile(configuration.securityConfiguration().configurationFileRegex());
        return findProjectFilesNames(projectPath)
                .stream()
                .filter(
                        input -> configFileRegex.matcher(input).find()
                )
                .toList();
    }

    @Override
    public @NonNull IssueCategory getIssueCategory()
    {
        return ISSUE_TYPE.getCategory();
    }
}
