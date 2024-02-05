package cz.muni.jena.issue.detectors.compilation_unit.security.token_lifetime;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodLikeDeclaration;
import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.detectors.compilation_unit.SpecificIssueDetector;
import cz.muni.jena.issue.language.elements.ResolvableNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

@Component("lifelongValidAccessTokensDetector")
public class LifelongValidAccessTokensDetector implements SpecificIssueDetector
{
    private static final IssueType ISSUE_TYPE = IssueType.LIFELONG_ACCESS_TOKENS;
    private static final String TOKEN_LIFETIME_SETTER = "org.springframework.security.oauth2.server.authorization.settings.TokenSettings.Builder.accessTokenTimeToLive";
    private static final String TOKEN_SETTINGS_METHOD = "org.springframework.security.oauth2.server.authorization.client.RegisteredClient.Builder.tokenSettings";

    @Override
    public @NonNull Stream<Issue> findIssues(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Configuration configuration)
    {
        Duration maxLifetimeDuration = configuration.securityConfiguration().tokenLifetimeSettings().toDuration();
        return classOrInterfaceDeclaration.findAll(MethodCallExpr.class)
                .stream()
                .filter(methodCallExpr -> ResolvableNode.resolve(methodCallExpr)
                        .map(ResolvedMethodDeclaration::getQualifiedName)
                        .anyMatch(TOKEN_SETTINGS_METHOD::equals)
                )
                .flatMap(methodCallExpr -> methodCallExpr.findAll(MethodCallExpr.class).stream())
                .filter(
                        method -> ResolvableNode.resolve(method)
                        .map(ResolvedMethodLikeDeclaration::getQualifiedName)
                        .anyMatch(TOKEN_LIFETIME_SETTER::equals)
                )
                .flatMap(
                        methodCallExpr -> methodCallExpr.getArguments()
                                .stream()
                )
                .filter(Expression::isMethodCallExpr)
                .map(Expression::asMethodCallExpr)
                .map(DurationFactoryMethods::findUnit)
                .flatMap(Optional::stream)
                .filter(
                        unitMethodPair -> unitMethodPair.getLeft().getArguments()
                                .stream()
                                .filter(Expression::isIntegerLiteralExpr)
                                .map(Expression::asIntegerLiteralExpr)
                                .map(IntegerLiteralExpr::asNumber)
                                .map(Number::longValue)
                                .anyMatch(
                                        tokenLifeSpan ->
                                                Duration.of(tokenLifeSpan, unitMethodPair.getRight())
                                                        .compareTo(maxLifetimeDuration) > 0
                                )
                )
                .map(methodCallExpr -> Issue.fromNodeWithRange(
                        methodCallExpr.getLeft(),
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
