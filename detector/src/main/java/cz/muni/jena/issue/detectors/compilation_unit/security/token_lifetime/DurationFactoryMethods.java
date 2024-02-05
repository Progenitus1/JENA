package cz.muni.jena.issue.detectors.compilation_unit.security.token_lifetime;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodLikeDeclaration;
import cz.muni.jena.issue.language.elements.ResolvableNode;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

public enum DurationFactoryMethods
{
    NANOS("java.time.Duration.ofNanos", ChronoUnit.NANOS),
    MILLIS("java.time.Duration.ofMillis", ChronoUnit.MILLIS),
    SECONDS("java.time.Duration.ofSeconds", ChronoUnit.SECONDS),
    MINUTE("java.time.Duration.ofMinutes", ChronoUnit.MINUTES),
    HOURS("java.time.Duration.ofHours", ChronoUnit.HOURS),
    DAY("java.time.Duration.ofDays", ChronoUnit.DAYS);

    private final String methodName;
    private final ChronoUnit unit;

    DurationFactoryMethods(String methodName, ChronoUnit unit)
    {
        this.methodName = methodName;
        this.unit = unit;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public ChronoUnit getUnit()
    {
        return unit;
    }

    public static Optional<ImmutablePair<MethodCallExpr, ChronoUnit>> findUnit(MethodCallExpr methodCallExpr)
    {
        Optional<String> qualifiedName = ResolvableNode.resolve(methodCallExpr)
                .map(ResolvedMethodLikeDeclaration::getQualifiedName)
                .findFirst();
        return Arrays.stream(DurationFactoryMethods.values())
                .filter(factoryMethods -> qualifiedName.map(factoryMethods.methodName::equals).orElse(false))
                .map(DurationFactoryMethods::getUnit)
                .findFirst()
                .map(unit -> ImmutablePair.of(methodCallExpr, unit));
    }
}
