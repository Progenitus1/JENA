package cz.muni.jena.configuration.mocking;

import java.util.List;
import java.util.Set;

public record MockingConfiguration(
        Set<String> exceptions,
        List<MockingMethod> mockingMethods
)
{
}
