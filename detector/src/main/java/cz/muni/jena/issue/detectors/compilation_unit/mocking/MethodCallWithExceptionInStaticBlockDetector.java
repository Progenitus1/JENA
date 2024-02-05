package cz.muni.jena.issue.detectors.compilation_unit.mocking;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import cz.muni.jena.configuration.mocking.MockingConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.NodeWrapper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component("staticBlockDetector")
public class MethodCallWithExceptionInStaticBlockDetector implements MockingIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.INAPPROPRIATE_METHOD_CALL_IN_STATIC_BLOCK;

    @Override
    public @NonNull Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, MockingConfiguration configuration)
    {
        return classOrInterfaceDeclaration.findAll(InitializerDeclaration.class)
                .stream()
                .filter(InitializerDeclaration::isStatic)
                .flatMap(block -> block.findAll(MethodCallExpr.class).stream())
                .map(NodeWrapper::new)
                .flatMap(
                        nodeWithBlockStatement -> nodeWithBlockStatement
                                .findMethodCallsWithAnyOfTheseExceptionInSignature(
                                        configuration.exceptions(),
                                        classOrInterfaceDeclaration,
                                        ISSUE_TYPE
                                )
                );
    }

    @Override
    public @NonNull IssueCategory getIssueCategory()
    {
        return ISSUE_TYPE.getCategory();
    }
}
