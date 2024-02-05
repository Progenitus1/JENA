package cz.muni.jena.issue.language.elements;

import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;

import java.util.Optional;
import java.util.stream.Stream;

public record ResolvableNode<T>(Resolvable<T> resolvable)
{
    public Stream<T> resolve()
    {
        synchronized (ResolvableNode.class)
        {
            try
            {
                return Stream.of(resolvable.resolve());
            } catch (RuntimeException e)
            {
                return Stream.of();
            }
        }
    }

    public static <T> Stream<T> resolve(Resolvable<T> resolvable)
    {
        return new ResolvableNode<T>(resolvable).resolve();
    }

    public static <T extends ResolvedValueDeclaration> boolean isTypeDecClass(Resolvable<T> resolvable)
    {
        return ResolvableNode.resolve(resolvable)
                .map(ResolvedValueDeclarationWrapper::getType)
                .flatMap(Optional::stream)
                .filter(ResolvedType::isReferenceType)
                .map(ResolvedType::asReferenceType)
                .map(ResolvedReferenceType::getTypeDeclaration)
                .flatMap(Optional::stream)
                .anyMatch(ResolvedTypeDeclaration::isClass);
    }
}
