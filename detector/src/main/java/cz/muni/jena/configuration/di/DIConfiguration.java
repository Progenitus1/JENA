package cz.muni.jena.configuration.di;

import java.util.List;
import java.util.Set;

public record DIConfiguration(
        List<Annotation> injectionAnnotations,
        int maxNumberOfInjections,
        int maxProducerMethodComplexity,
        List<Annotation> producerAnnotations,
        Set<String> directContainerCallMethods
)
{
}
