package cz.muni.jena.configuration.service_layer;

import java.util.Set;

public record ServiceLayerConfiguration(
        int maxServiceMethods,
        int minServiceMethods,
        Set<String> serviceAnnotations
)
{
}
