package cz.muni.jena.issue.detectors.compilation_unit.dependency;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithRange;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import cz.muni.jena.configuration.di.Annotation;
import cz.muni.jena.configuration.di.DIConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.Class;
import cz.muni.jena.issue.language.elements.ResolvableNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("openWindowInjection")
public class OpenWindowInjectionDetector implements DIIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.OPEN_WINDOW_INJECTION;

    @Override
    @NonNull
    public Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, DIConfiguration configuration)
    {
        List<Annotation> injectionAnnotations = configuration.injectionAnnotations();
        Class classWrapper = new Class(classOrInterfaceDeclaration);
        Set<String> injectedFields = classWrapper.findInjectedFields(injectionAnnotations)
                .flatMap(ResolvableNode::resolve)
                .map(ResolvedDeclaration::getName)
                .collect(Collectors.toSet());

        Stream<NodeWithRange<?>> callsWithOpenWindow = classWrapper.findCallsLeakingInjectedFields(injectedFields, injectionAnnotations);
        Stream<ReturnStmt> openWindowReturns = classWrapper.findReturnStatementsLeakingInjectedFields(injectedFields);
        return Stream.concat(callsWithOpenWindow, openWindowReturns)
                .map(method -> Issue.fromNodeWithRange(method, ISSUE_TYPE, classOrInterfaceDeclaration));
    }

    @Override
    public @NonNull IssueCategory getIssueCategory()
    {
        return ISSUE_TYPE.getCategory();
    }
}
