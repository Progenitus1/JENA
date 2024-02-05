package cz.muni.jena.issue.language.elements;

import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;

import java.util.Optional;

public class ResolvedValueDeclarationWrapper
{
    public static Optional<ResolvedType> getType(ResolvedValueDeclaration resolvedValueDeclaration)
    {
        synchronized (ResolvableNode.class)
        {
            try
            {
                return Optional.of(resolvedValueDeclaration.getType());
            } catch (RuntimeException e)
            {
                return Optional.empty();
            }
        }
    }
}
