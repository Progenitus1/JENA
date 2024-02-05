package cz.muni.jena.configuration.persistence;

import java.util.Set;

public record PersistenceConfiguration(
        Set<String> queryMethods,
        String nPlusOneQueryRegex
)
{
}
