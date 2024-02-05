package cz.muni.jena.issue.detectors.compilation_unit.dependency;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.configuration.di.DIConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.detectors.compilation_unit.SpecificIssueDetector;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.stream.Stream;

public interface DIIssueDetector extends SpecificIssueDetector
{
    @NonNull
    default Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Configuration configuration)
    {
        return findIssues(classOrInterfaceDeclaration, configuration.diConfiguration());
    }

    @NonNull
    default List<Issue> findIssues(List<ClassOrInterfaceDeclaration> compilationUnits, DIConfiguration configuration)
    {
        return compilationUnits.stream()
                .flatMap(classOrInterfaceDeclaration -> findIssues(classOrInterfaceDeclaration, configuration))
                .toList();
    }

    @NonNull
    Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, DIConfiguration configuration);
}
