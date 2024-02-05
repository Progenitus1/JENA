package cz.muni.jena.issue.detectors.compilation_unit.security;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.detectors.compilation_unit.SpecificIssueDetector;
import cz.muni.jena.issue.language.elements.NodeWrapper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component("insecureCommunicationDetector")
public class InsecureCommunicationDetector implements SpecificIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.INSECURE_COMMUNICATION;

    @Override
    public @NonNull Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Configuration configuration)
    {
        NodeWrapper<ClassOrInterfaceDeclaration> node = new NodeWrapper<>(classOrInterfaceDeclaration);
        return configuration.securityConfiguration()
                .unsecureCommunicationRegexes()
                .stream()
                .map(Pattern::compile)
                .flatMap(node::findStringsMatchingRegexInNode)
                .map(method -> Issue.fromNodeWithRange(
                        method,
                        ISSUE_TYPE,
                        classOrInterfaceDeclaration
                ));
    }

    @Override
    public @NonNull IssueCategory getIssueCategory()
    {
        return ISSUE_TYPE.getCategory();
    }
}
