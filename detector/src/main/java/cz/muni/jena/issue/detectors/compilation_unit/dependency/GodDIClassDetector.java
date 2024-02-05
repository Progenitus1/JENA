package cz.muni.jena.issue.detectors.compilation_unit.dependency;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import cz.muni.jena.configuration.di.DIConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.Class;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component("godDIClass")
public class GodDIClassDetector implements DIIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.GOD_DI_CLASS;

    @Override
    public @NonNull Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, DIConfiguration configuration)
    {
        return Stream.of(classOrInterfaceDeclaration)
                .map(Class::new)
                .filter(processedClass -> processedClass.findInjectedFields(
                        configuration.injectionAnnotations()).count() > configuration.maxNumberOfInjections()
                )
                .map(Class::classOrInterfaceDeclaration)
                .map(processedClass -> Issue.fromNodeWithRange(null, ISSUE_TYPE, processedClass));
    }

    @Override
    public @NonNull IssueCategory getIssueCategory()
    {
        return ISSUE_TYPE.getCategory();
    }
}
