package cz.muni.jena.issue.detectors.compilation_unit.dependency;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import cz.muni.jena.configuration.di.DIConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.ResolvableNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component("directContainerCall")
public class DirectContainerCallDetector implements DIIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.DIRECT_CONTAINER_CALL;

    @Override
    @NonNull
    public Stream<Issue> findIssues(ClassOrInterfaceDeclaration classDeclaration, DIConfiguration configuration)
    {
        Stream<MethodCallExpr> directContainerCalls = classDeclaration.findAll(MethodCallExpr.class).stream()
                .filter(methodCallExpr -> Stream.of(methodCallExpr)
                        .flatMap(ResolvableNode::resolve)
                        .anyMatch(
                                resolvedMethodDeclaration -> configuration.directContainerCallMethods()
                                        .contains(resolvedMethodDeclaration.getQualifiedName())
                        )
                );

        return directContainerCalls
                .map(method -> Issue.fromNodeWithRange(method, ISSUE_TYPE, classDeclaration));
    }

    @Override
    @NonNull
    public IssueCategory getIssueCategory()
    {
        return ISSUE_TYPE.getCategory();
    }
}
