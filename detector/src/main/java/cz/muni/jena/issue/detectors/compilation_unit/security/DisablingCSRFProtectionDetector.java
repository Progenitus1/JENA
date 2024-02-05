package cz.muni.jena.issue.detectors.compilation_unit.security;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodLikeDeclaration;
import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.detectors.compilation_unit.SpecificIssueDetector;
import cz.muni.jena.issue.language.elements.ResolvableNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component("disablingCSRFProtectionDetector")
public class DisablingCSRFProtectionDetector implements SpecificIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.DISABLING_CSRF_PROTECTION;
    private static final String DISABLE_CSRF_METHOD = "org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer.disable";

    @Override
    public @NonNull Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Configuration configuration)
    {
        return Stream.concat(
                        classOrInterfaceDeclaration.findAll(MethodCallExpr.class)
                                .stream(),
                        classOrInterfaceDeclaration.findAll(MethodReferenceExpr.class)
                                .stream()
                )
                .filter(
                        method -> ResolvableNode.resolve(method)
                                .map(ResolvedMethodLikeDeclaration::getQualifiedName)
                                .anyMatch(DISABLE_CSRF_METHOD::equals)
                )
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
