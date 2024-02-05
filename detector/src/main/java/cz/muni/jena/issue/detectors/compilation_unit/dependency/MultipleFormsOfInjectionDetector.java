package cz.muni.jena.issue.detectors.compilation_unit.dependency;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import cz.muni.jena.configuration.di.Annotation;
import cz.muni.jena.configuration.di.DIConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.CallableDec;
import cz.muni.jena.issue.language.elements.ResolvableNode;
import cz.muni.jena.issue.language.elements.ResolvedFieldDec;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("multipleFormsOfInjection")
public class MultipleFormsOfInjectionDetector implements DIIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.MULTIPLE_FORMS_OF_INJECTION;

    @Override
    @NonNull
    public Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, DIConfiguration configuration)
    {
        List<Annotation> injectionAnnotations = configuration.injectionAnnotations();
        Stream<Map.Entry<ResolvedFieldDec, Integer>> fieldsWithFieldInjection = classOrInterfaceDeclaration
                .findAll(FieldDeclaration.class)
                .stream()
                .flatMap(ResolvableNode::resolve)
                .map(ResolvedFieldDec::new)
                .filter(resolvedFieldDec -> resolvedFieldDec.hasAnyOfTheseAnnotations(injectionAnnotations))
                .map(resolvedFieldDec -> Map.entry(resolvedFieldDec, 1));

        Stream<Map.Entry<ResolvedFieldDec, Integer>> fieldsInjectedInCallables =
                Stream.<CallableDeclaration<? extends CallableDeclaration<?>>>concat(
                        classOrInterfaceDeclaration.getConstructors().stream(),
                        classOrInterfaceDeclaration.getMethods().stream()
                )
                .map(callableDeclaration -> new CallableDec<>(callableDeclaration, injectionAnnotations))
                .filter(CallableDec::isInjectionCallable)
                .flatMap(
                        callableDec -> callableDec
                                .findInjectedFields()
                                .map(Map.Entry::getValue)
                                .map(ResolvedFieldDec::new)
                                .map(field -> Map.entry(field, 1))
                );

        Map<ResolvedFieldDec, Integer> numberOfInjectionsOfField = Stream.concat(fieldsInjectedInCallables, fieldsWithFieldInjection)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::sum
                ));

        return numberOfInjectionsOfField.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .map(ResolvedFieldDec::resolvedFieldDeclaration)
                .map(resolvedFieldDeclaration -> resolvedFieldDeclaration.toAst(FieldDeclaration.class))
                .flatMap(Optional::stream)
                .map(
                        fieldDeclaration -> Issue.fromNodeWithRange(
                                fieldDeclaration,
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
