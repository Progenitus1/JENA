package cz.muni.jena.issue.detectors.compilation_unit.security;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.detectors.compilation_unit.SpecificIssueDetector;
import cz.muni.jena.issue.language.elements.ResolvableNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Component("signingJWTWithFixedSecretDetector")
public class SigningJWTWithFixedSecretDetector implements SpecificIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.SIGNING_JWT_WITH_FIXED_SECRET;
    private static final String STRING_TYPE = "java.lang.String";

    @Override
    public @NonNull Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Configuration configuration)
    {
        Set<String> jwtSigningMethods = configuration.securityConfiguration().jwtSigningMethods();
        return classOrInterfaceDeclaration.findAll(MethodCallExpr.class)
                .stream()
                .filter(
                        methodCall -> ResolvableNode.resolve(methodCall)
                                .map(ResolvedMethodDeclaration::getQualifiedName)
                                .anyMatch(jwtSigningMethods::contains)
                )
                .flatMap(methodCallExpr -> methodCallExpr.getArguments().stream())
                .filter(
                        argument -> argument.isStringLiteralExpr()
                                || Stream.of(argument).filter(Expression::isNameExpr)
                                .map(Expression::asNameExpr)
                                .flatMap(ResolvableNode::resolve)
                                .filter(ResolvedDeclaration::isField)
                                .map(ResolvedDeclaration::asField)
                                .filter(field -> field.getType().describe().equals(STRING_TYPE))
                                .map(resolvedValueDeclaration -> resolvedValueDeclaration.toAst(Node.class))
                                .flatMap(Optional::stream)
                                .anyMatch(
                                        node -> node.findAll(Modifier.class)
                                                .stream()
                                                .map(Modifier::getKeyword)
                                                .anyMatch(Modifier.Keyword.FINAL::equals)
                                )

                )
                .map(methodCallExpr -> Issue.fromNodeWithRange(
                        methodCallExpr,
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
