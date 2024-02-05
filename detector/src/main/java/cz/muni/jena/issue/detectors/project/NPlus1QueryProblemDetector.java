package cz.muni.jena.issue.detectors.project;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.resolution.declarations.ResolvedAnnotationDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.configuration.di.Annotation;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.NodeWithAnnotation;
import cz.muni.jena.issue.language.elements.NodeWrapper;
import cz.muni.jena.issue.language.elements.ResolvableNode;
import cz.muni.jena.parser.CompilationUnitParser;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("nPlus1QueryProblemDetector")
public class NPlus1QueryProblemDetector implements ProjectIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.N_PLUS1_QUERY_PROBLEM;
    private static final Annotation ENTITY = new Annotation("jakarta.persistence.Entity");
    private static final Set<Annotation> ENTITY_RELATIONS = Set.of(
            new Annotation("jakarta.persistence.ManyToOne"),
            new Annotation("jakarta.persistence.OneToMany")
    );
    private static final String QUERY_ANNOTATION = "org.springframework.data.jpa.repository.Query";

    @Override
    public Stream<Issue> findIssues(String projectPath, Configuration configuration)
    {
        Set<String> queryMethods = configuration.persistenceConfiguration().queryMethods();
        List<ClassOrInterfaceDeclaration> allClassOrInterfaceDeclarations = new CompilationUnitParser(projectPath)
                .findAllClassOrInterfaceDeclaration();
        Pattern nPlusOneQueryRegex = Pattern.compile(configuration.persistenceConfiguration().nPlusOneQueryRegex());
        Set<String> entitiesWithForeignRelations = findEntitiesWithForeignRelations(allClassOrInterfaceDeclarations);
        return allClassOrInterfaceDeclarations.stream().flatMap(
                classOrInterfaceDeclaration -> Stream.concat(
                                findMethodCallsWithNPlusOneQueryProblem(
                                        queryMethods,
                                        nPlusOneQueryRegex,
                                        entitiesWithForeignRelations,
                                        classOrInterfaceDeclaration
                                ),
                                findQueryAnnotationsWithNPlusOneQueryProblem(
                                        nPlusOneQueryRegex,
                                        entitiesWithForeignRelations,
                                        classOrInterfaceDeclaration
                                )
                        )
                        .map(methodCallExpr -> Issue.fromNodeWithRange(
                                methodCallExpr,
                                IssueType.N_PLUS1_QUERY_PROBLEM,
                                classOrInterfaceDeclaration
                        ))
        );
    }

    private Stream<AnnotationExpr> findQueryAnnotationsWithNPlusOneQueryProblem(
            Pattern nPlusOneQueryRegex,
            Set<String> entitiesWithForeignRelations,
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration
    )
    {
        return classOrInterfaceDeclaration.findAll(AnnotationExpr.class)
                .stream()
                .filter(
                        annotation -> ResolvableNode.resolve(annotation)
                                .map(ResolvedAnnotationDeclaration::getQualifiedName)
                                .anyMatch(QUERY_ANNOTATION::equals)
                )
                .filter(
                        annotation -> hasQueryWIthNPlusOneProblem(
                                nPlusOneQueryRegex,
                                entitiesWithForeignRelations,
                                annotation
                        )
                );
    }

    private Stream<MethodCallExpr> findMethodCallsWithNPlusOneQueryProblem(
            Set<String> queryMethods,
            Pattern nPlusOneQueryRegex,
            Set<String> entitiesWithForeignRelations,
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration
    )
    {
        return classOrInterfaceDeclaration.findAll(MethodCallExpr.class)
                .stream()
                .filter(
                        methodCallExpr -> ResolvableNode.resolve(methodCallExpr)
                                .map(ResolvedMethodDeclaration::getQualifiedName)
                                .anyMatch(queryMethods::contains)
                )
                .filter(
                        methodCallExpr -> hasQueryWIthNPlusOneProblem(
                                nPlusOneQueryRegex,
                                entitiesWithForeignRelations,
                                methodCallExpr
                        )
                );
    }

    private boolean hasQueryWIthNPlusOneProblem(
            Pattern nPlusOneQueryRegex,
            Set<String> entitiesWithForeignRelations,
            Node node
    )
    {
        return new NodeWrapper<>(node).findStringsMatchingRegexInNode(nPlusOneQueryRegex)
                .anyMatch(
                        stringLiteralExpr -> nPlusOneQueryRegex
                                .matcher(stringLiteralExpr.getValue().toLowerCase(Locale.ROOT))
                                .results()
                                .map(MatchResult::group)
                                .map(match -> match.split("\\s")[0])
                                .anyMatch(entitiesWithForeignRelations::contains)
                );
    }

    private Set<String> findEntitiesWithForeignRelations(List<ClassOrInterfaceDeclaration> classes)
    {
        return classes.stream().filter(
                        classOrInterfaceDeclaration -> NodeWithAnnotation.hasAnyOfTheseAnnotations(
                                classOrInterfaceDeclaration,
                                Set.of(ENTITY)
                        )
                ).filter(
                        classOrInterfaceDeclaration -> classOrInterfaceDeclaration.findAll(FieldDeclaration.class)
                                .stream()
                                .anyMatch(
                                        fieldDeclaration -> NodeWithAnnotation.hasAnyOfTheseAnnotations(
                                                fieldDeclaration,
                                                ENTITY_RELATIONS
                                        )
                                )
                )
                .map(ClassOrInterfaceDeclaration::getName)
                .map(SimpleName::asString)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Override
    public @NonNull IssueCategory getIssueCategory()
    {
        return ISSUE_TYPE.getCategory();
    }
}
