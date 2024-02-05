package cz.muni.jena.issue.language.elements;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import cz.muni.jena.configuration.di.Annotation;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public record ResolvedFieldDec(ResolvedFieldDeclaration resolvedFieldDeclaration, String fullName)
{
    public ResolvedFieldDec(ResolvedFieldDeclaration resolvedFieldDeclaration)
    {
        this(resolvedFieldDeclaration, findFullName(resolvedFieldDeclaration));
    }

    private static String findFullName(ResolvedFieldDeclaration resolvedFieldDeclaration)
    {
        synchronized (ResolvableNode.class)
        {
            return resolvedFieldDeclaration.declaringType().getQualifiedName() + resolvedFieldDeclaration.getName();
        }
    }

    public static Stream<ResolvedFieldDeclaration> findUsedFields(ClassOrInterfaceDeclaration classDeclaration)
    {
        return classDeclaration.findAll(NameExpr.class).stream()
                .flatMap(ResolvableNode::resolve)
                .filter(ResolvedDeclaration::isField)
                .map(ResolvedDeclaration::asField);
    }

    public boolean hasAnyOfTheseAnnotations(Collection<Annotation> annotations)
    {
        return resolvedFieldDeclaration.toAst(FieldDeclaration.class)
                .map(NodeWithAnnotation::new)
                .map(node -> node.hasAnyOfTheseAnnotations(annotations))
                .orElse(false);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        ResolvedFieldDec otherResolvedFieldDec = (ResolvedFieldDec) o;
        return Objects.equals(fullName, otherResolvedFieldDec.fullName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(fullName);
    }
}
