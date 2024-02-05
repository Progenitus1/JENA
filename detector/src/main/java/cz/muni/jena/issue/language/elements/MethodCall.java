package cz.muni.jena.issue.language.elements;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithFinalModifier;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import cz.muni.jena.configuration.mocking.ArgumentType;
import cz.muni.jena.configuration.mocking.MockingMethod;

import java.util.Optional;
import java.util.stream.Stream;

public record MethodCall(MethodCallExpr methodCallExpr)
{
    public boolean hasMatchingArguments(MockingMethod mockingMethod)
    {
        NodeList<Expression> arguments = methodCallExpr.getArguments();
        if (arguments.size() != 1)
        {
            return false;
        }
        if (mockingMethod.argumentType() == ArgumentType.METHOD_CALL)
        {
            return arguments.stream()
                    .filter(Expression::isMethodCallExpr)
                    .map(Expression::asMethodCallExpr)
                    .flatMap(ResolvableNode::resolve)
                    .map(resolvedMethodDeclaration -> resolvedMethodDeclaration.toAst(MethodDeclaration.class))
                    .flatMap(Optional::stream)
                    .anyMatch(NodeWithFinalModifier::isFinal);

        }
        if (mockingMethod.argumentType() == ArgumentType.CLASS)
        {
            return argumentIsFinalClass(arguments.get(0)) ||
                    (
                            !mockingMethod.argumentHasToBeFinal()
                            && arguments.stream().anyMatch(Expression::isClassExpr)
                    );
        }
        return false;
    }

    private boolean argumentIsFinalClass(Expression argument)
    {
        return Class.isFinalClass(Stream.of(argument)
                .filter(Expression::isClassExpr)
                .map(Expression::asClassExpr)
                .map(ClassExpr::getType)
                .flatMap(ResolvableNode::resolve)
                .filter(ResolvedType::isReferenceType)
                .map(ResolvedType::asReferenceType)
                .map(ResolvedReferenceType::getTypeDeclaration)
                .flatMap(Optional::stream));
    }

    public static ResolvedReferenceTypeDeclaration declaringType(ResolvedMethodDeclaration resolvedMethodDeclaration)
    {
        synchronized (ResolvableNode.class)
        {
            return resolvedMethodDeclaration.declaringType();
        }
    }
}
