package cz.muni.jena.issue.detectors.compilation_unit.security;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.configuration.security.EncryptionAlgorithm;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.detectors.compilation_unit.SpecificIssueDetector;
import cz.muni.jena.issue.language.elements.ConstructorCall;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component("insecureDefaultConfigurationDetector")
public class InsecureDefaultConfigurationDetector implements SpecificIssueDetector
{

    private static final IssueType ISSUE_TYPE = IssueType.INSECURE_DEFAULT_CONFIGURATION;

    @Override
    public @NonNull Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Configuration configuration)
    {
        List<EncryptionAlgorithm> encryptionAlgorithms = configuration.securityConfiguration().encryptionAlgorithms();
        return classOrInterfaceDeclaration.findAll(ObjectCreationExpr.class)
                .stream()
                .map(ConstructorCall::new)
                .filter(
                        constructorCall -> encryptionAlgorithms.stream()
                                .anyMatch(constructorCall::hasThisInsecureDefaultConfiguration)
                )
                .map(ConstructorCall::objectCreationExpr)
                .map(methodCallExpr -> Issue.fromNodeWithRange(
                        methodCallExpr,
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
