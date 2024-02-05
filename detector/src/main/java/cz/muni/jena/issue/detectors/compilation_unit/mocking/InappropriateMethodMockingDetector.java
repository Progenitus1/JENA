package cz.muni.jena.issue.detectors.compilation_unit.mocking;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import cz.muni.jena.configuration.mocking.MockingConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.MethodCall;
import cz.muni.jena.issue.language.elements.ResolvableNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component("inappropriateMethodMockingDetector")
public class InappropriateMethodMockingDetector implements MockingIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.INAPPROPRIATE_METHOD_MOCKING;

    @Override
    public @NonNull IssueCategory getIssueCategory()
    {
        return ISSUE_TYPE.getCategory();
    }

    @Override
    public @NonNull Stream<Issue> findIssues(
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration,
            MockingConfiguration configuration
    )
    {
        return classOrInterfaceDeclaration.findAll(MethodCallExpr.class)
                .stream()
                .map(MethodCall::new)
                .filter(
                        methodCallExpr -> ResolvableNode.resolve(methodCallExpr.methodCallExpr())
                                .map(ResolvedMethodDeclaration::getQualifiedName)
                                .anyMatch(
                                        qualifiedName -> configuration
                                                .mockingMethods()
                                                .stream()
                                                .filter(mockingMethod -> qualifiedName.equals(mockingMethod.fullyQualifiedName()))
                                                .anyMatch(methodCallExpr::hasMatchingArguments)
                                )
                )
                .map(MethodCall::methodCallExpr)
                .map(
                        methodCall -> Issue.fromNodeWithRange(
                                methodCall,
                                IssueType.INAPPROPRIATE_METHOD_MOCKING,
                                classOrInterfaceDeclaration
                        )
                );
    }
}
