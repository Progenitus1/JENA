package cz.muni.jena.issue.detectors.compilation_unit.dependency;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import cz.muni.jena.configuration.di.Annotation;
import cz.muni.jena.configuration.di.DIConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.Class;
import cz.muni.jena.issue.language.elements.ResolvableNode;
import cz.muni.jena.issue.language.elements.ResolvedFieldDec;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("unusedInjection")
public class UnusedInjectionDetector implements DIIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.UNUSED_INJECTION;

    @Override
    @NonNull
    public Stream<Issue> findIssues(ClassOrInterfaceDeclaration classDeclaration, DIConfiguration configuration)
    {
        List<Annotation> injectionAnnotations = configuration.injectionAnnotations();
        Set<String> usedFields = ResolvedFieldDec.findUsedFields(classDeclaration)
                .map(ResolvedFieldDeclaration::getName).collect(Collectors.toSet());
        return new Class(classDeclaration).findInjectedFields(injectionAnnotations)
                .filter(
                        field -> !ResolvableNode.resolve(field)
                                .findFirst()
                                .map(ResolvedDeclaration::getName)
                                .map(usedFields::contains)
                                .orElse(true)
                )
                .map(field -> Issue.fromNodeWithRange(field, ISSUE_TYPE, classDeclaration));
    }

    @Override
    public @NonNull IssueCategory getIssueCategory()
    {
        return ISSUE_TYPE.getCategory();
    }
}
