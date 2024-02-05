package cz.muni.jena.issue.language.elements;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.resolution.declarations.ResolvedAnnotationDeclaration;
import cz.muni.jena.configuration.di.Annotation;

import java.util.Collection;

public record NodeWithAnnotation<T extends NodeWithAnnotations<? extends Node>>(T node)
{

    public boolean hasAnyOfTheseAnnotations(Collection<Annotation> annotations)
    {
        return annotations.stream().anyMatch(
                annotation -> node.getAnnotationByName(annotation.simpleName())
                        .stream()
                        .flatMap(ResolvableNode::resolve)
                        .map(ResolvedAnnotationDeclaration::getQualifiedName)
                        .anyMatch(annotation::hasSameFullyQualifiedName)
        );
    }

    public static <T extends NodeWithAnnotations<? extends Node>> boolean hasAnyOfTheseAnnotations(
            T node,
            Collection<Annotation> annotations
    )
    {
        return new NodeWithAnnotation<T>(node).hasAnyOfTheseAnnotations(annotations);
    }
}
