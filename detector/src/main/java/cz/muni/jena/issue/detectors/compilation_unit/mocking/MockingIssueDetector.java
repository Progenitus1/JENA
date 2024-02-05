package cz.muni.jena.issue.detectors.compilation_unit.mocking;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.configuration.mocking.MockingConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.detectors.compilation_unit.SpecificIssueDetector;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public interface MockingIssueDetector extends SpecificIssueDetector
{
    @NonNull
    default Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Configuration configuration)
    {
        return findIssues(classOrInterfaceDeclaration, configuration.mockingConfiguration());
    }

    @NonNull
    default List<Issue> findIssues(List<CompilationUnit> compilationUnits, MockingConfiguration configuration)
    {
        List<Issue> issues = new ArrayList<>();
        for (CompilationUnit compilationUnit : compilationUnits)
        {
            issues.addAll(findIssues(compilationUnit, configuration));
        }

        return issues;
    }

    @NonNull
    Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, MockingConfiguration configuration);

    @NonNull
    default List<Issue> findIssues(CompilationUnit compilationUnit, MockingConfiguration configuration)
    {
        return compilationUnit
                .findAll(ClassOrInterfaceDeclaration.class, declaration -> !declaration.isInterface())
                .stream()
                .flatMap(classOrInterfaceDeclaration -> findIssues(
                        classOrInterfaceDeclaration,
                        configuration
                ))
                .toList();
    }
}
