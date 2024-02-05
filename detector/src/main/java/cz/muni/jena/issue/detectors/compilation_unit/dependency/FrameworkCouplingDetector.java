package cz.muni.jena.issue.detectors.compilation_unit.dependency;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import cz.muni.jena.configuration.di.Annotation;
import cz.muni.jena.configuration.di.DIConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.NodeWithAnnotation;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Stream;

@Component("frameworkCoupling")
public class FrameworkCouplingDetector implements DIIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.FRAMEWORK_COUPLING;

    @Override
    @NonNull
    public Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, DIConfiguration configuration)
    {
        Stream<NodeWithAnnotations<? extends Node>> possibleInjectionPlaces = Stream.concat(
                classOrInterfaceDeclaration.findAll(FieldDeclaration.class).stream(),
                Stream.<NodeWithAnnotations<? extends Node>>concat(
                        classOrInterfaceDeclaration.findAll(MethodDeclaration.class).stream(),
                        classOrInterfaceDeclaration.findAll(ConstructorDeclaration.class).stream()
                )
        );

        Collection<Annotation> injectionAnnotationsCoupledWithFramework = configuration.injectionAnnotations()
                .stream()
                .filter(Annotation::coupledToFramework)
                .toList();
        Stream<NodeWithAnnotations<? extends Node>> nodesWithInjectionCoupledToFramework = possibleInjectionPlaces
                .map(NodeWithAnnotation::new)
                .filter(node -> node.hasAnyOfTheseAnnotations(injectionAnnotationsCoupledWithFramework))
                .map(NodeWithAnnotation::node);

        return nodesWithInjectionCoupledToFramework.map(
                node -> Issue.fromNodeWithRange(((Node)node), ISSUE_TYPE, classOrInterfaceDeclaration)
        );
    }

    @Override
    public @NonNull IssueCategory getIssueCategory()
    {
        return ISSUE_TYPE.getCategory();
    }
}
