package cz.muni.jena.issue.detectors.compilation_unit.dependency;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithRange;
import cz.muni.jena.configuration.di.Annotation;
import cz.muni.jena.configuration.di.DIConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.CallableDec;
import cz.muni.jena.issue.language.elements.NodeWithAnnotation;
import cz.muni.jena.issue.language.elements.ResolvableNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component("concreteClassInjection")
public class ConcreteClassInjectionDetector implements DIIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.CONCRETE_CLASS_INJECTION;

    @Override
    @NonNull
    public Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, DIConfiguration configuration)
    {
        List<Annotation> injectionAnnotations = configuration.injectionAnnotations();
        Stream<? extends NodeWithRange<?>> constructorConcreteClassInjections =
                Stream.<CallableDeclaration<? extends CallableDeclaration<?>>>concat(
                                classOrInterfaceDeclaration.getConstructors().stream(),
                                classOrInterfaceDeclaration.getMethods().stream()
                        )
                        .map(callableDeclaration -> new CallableDec<>(
                                callableDeclaration,
                                injectionAnnotations
                        ))
                        .filter(CallableDec::isThereConcreteClassInjection)
                        .map(CallableDec::callableDeclaration);

        Stream<FieldDeclaration> fieldConcreteClassInjections = classOrInterfaceDeclaration.findAll(FieldDeclaration.class)
                .stream()
                .map(NodeWithAnnotation::new)
                .filter(node -> node.hasAnyOfTheseAnnotations(injectionAnnotations))
                .map(NodeWithAnnotation::node)
                .filter(ResolvableNode::isTypeDecClass);

        return Stream.concat(
                        constructorConcreteClassInjections,
                        fieldConcreteClassInjections
                )
                .map(
                        nodeWithRange -> Issue.fromNodeWithRange(
                                nodeWithRange,
                                ISSUE_TYPE,
                                classOrInterfaceDeclaration
                        )
                );
    }

    @Override
    @NonNull
    public IssueCategory getIssueCategory()
    {
        return ISSUE_TYPE.getCategory();
    }
}
