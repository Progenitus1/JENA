package cz.muni.jena.issue.language.elements;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import cz.muni.jena.configuration.security.EncryptionAlgorithm;

import java.util.List;

public record ConstructorCall(ObjectCreationExpr objectCreationExpr)
{
    public boolean hasThisInsecureDefaultConfiguration(EncryptionAlgorithm encryptionAlgorithm)
    {
        List<Integer> intArguments = objectCreationExpr.getArguments()
                .stream()
                .filter(Expression::isIntegerLiteralExpr)
                .map(Expression::asIntegerLiteralExpr)
                .map(IntegerLiteralExpr::asNumber)
                .map(Number::intValue)
                .toList();
        return ResolvableNode.resolve(objectCreationExpr)
                .map(ResolvedConstructorDeclaration::getQualifiedName)
                .anyMatch(name -> name.equals(encryptionAlgorithm.name()))
                && (
                intArguments.isEmpty() ?
                        encryptionAlgorithm.defaultStrength() < encryptionAlgorithm.strengthRequired()
                        :
                        intArguments
                                .stream()
                                .anyMatch(strength -> strength < encryptionAlgorithm.strengthRequired())

        );
    }
}
