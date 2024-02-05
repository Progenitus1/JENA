package cz.muni.jena.issue.detectors.compilation_unit.dependency;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import cz.muni.jena.configuration.di.DIConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.NodeWithAnnotation;
import cz.muni.jena.issue.language.elements.NodeWrapper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component("longProducerMethod")
public class LongProducerMethodDetector implements DIIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.LONG_PRODUCER_METHOD;

    @Override
    public @NonNull Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, DIConfiguration configuration)
    {
        return classOrInterfaceDeclaration.findAll(MethodDeclaration.class)
                .stream()
                .map(NodeWithAnnotation::new)
                .filter(node -> node.hasAnyOfTheseAnnotations(configuration.producerAnnotations()))
                .map(NodeWithAnnotation::node)
                .map(NodeWrapper::new)
                .filter(node -> node.calculateComplexity() > configuration.maxProducerMethodComplexity())
                .map(NodeWrapper::node)
                .map(methodDeclaration -> Issue.fromNodeWithRange(
                        methodDeclaration,
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
