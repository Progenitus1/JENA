package cz.muni.jena.issue.detectors.compilation_unit.dependency;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import cz.muni.jena.configuration.di.Annotation;
import cz.muni.jena.configuration.di.DIConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.AssignExpression;
import cz.muni.jena.issue.language.elements.Class;
import cz.muni.jena.issue.language.elements.ResolvableNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("openDoorInjection")
public class OpenDoorInjectionDetector implements DIIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.OPEN_DOOR_INJECTION;

    @Override
    public @NonNull Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, DIConfiguration configuration)
    {
        List<Annotation> injectionAnnotations = configuration.injectionAnnotations();
        Set<String> injectedFields = new Class(classOrInterfaceDeclaration)
                .findInjectedFields(injectionAnnotations)
                .flatMap(ResolvableNode::resolve)
                .map(ResolvedDeclaration::getName)
                .collect(Collectors.toSet());

        return classOrInterfaceDeclaration.getMethods()
                .stream()
                .flatMap(
                        methodDeclaration -> methodDeclaration.findAll(AssignExpr.class)
                                .stream()
                                .map(AssignExpression::new)
                                .flatMap(
                                        assignExpression -> assignExpression.findFieldAssigmentOfInjectedField(injectedFields)
                                )
                )
                .map(
                        assignExpr -> Issue.fromNodeWithRange(
                                assignExpr,
                                ISSUE_TYPE,
                                classOrInterfaceDeclaration
                        )
                );
    }

    @Override
    public @NonNull IssueCategory getIssueCategory()
    {
        return ISSUE_TYPE.getCategory();
    }
}
