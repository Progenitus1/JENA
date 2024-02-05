package cz.muni.jena.issue.language.elements;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.nodeTypes.NodeWithRange;
import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodLikeDeclaration;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record ResolvableNodeWithArguments<
        T extends NodeWithArguments<?>
        & Resolvable<? extends ResolvedMethodLikeDeclaration>
        & NodeWithRange<?>
        >(T resolvableNodeWithArguments)
{
    public boolean belongsToClass(ClassOrInterfaceDeclaration classOrInterfaceDeclaration)
    {
        return resolve().map(
                        resolvedMethodDeclaration -> classOrInterfaceDeclaration.getName()
                                .asString()
                                .equals(resolvedMethodDeclaration.getClassName())
                )
                .orElse(false);
    }

    public boolean hasInjectedFieldParameter(Collection<String> injectedFieldNames)
    {
        return resolvableNodeWithArguments.getArguments().stream()
                .filter(Expression::isNameExpr).map(Expression::asNameExpr)
                .flatMap(ResolvableNode::resolve)
                .filter(ResolvedDeclaration::isField).map(ResolvedDeclaration::asField)
                .anyMatch(field -> injectedFieldNames.contains(field.getName()));
    }

    public Optional<ResolvedMethodLikeDeclaration> resolve()
    {
        synchronized (ResolvableNode.class)
        {
            try
            {
                return Optional.of(resolvableNodeWithArguments.resolve());
            } catch (RuntimeException e)
            {
                return Optional.empty();
            }
        }
    }

    public boolean hasParameterAssignedIntoInjectedField(Set<Parameter> parametersAssignedIntoInjectedFields)
    {
        Set<String> parametersNames = resolvableNodeWithArguments.getArguments().stream()
                .filter(Expression::isNameExpr).map(Expression::asNameExpr)
                .map(NameExpr::getName).map(SimpleName::asString).collect(Collectors.toSet());
        return parametersAssignedIntoInjectedFields.stream()
                .anyMatch(parameter -> parametersNames.contains(parameter.getName().asString()));
    }
}
