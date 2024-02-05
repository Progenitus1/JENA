package cz.muni.jena.configuration.di;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public record Annotation(
        String simpleName,
        String fullyQualifiedName,
        Boolean coupledToFramework
)
{
    @JsonCreator
    public Annotation(@JsonProperty("fullyQualifiedName")String fullyQualifiedName,
                      @JsonProperty("coupledToFramework")Boolean coupledToFramework)
    {
        this(extractSimpleName(fullyQualifiedName), fullyQualifiedName, Optional.ofNullable(coupledToFramework).orElse(false));
    }

    public Annotation(String fullyQualifiedName)
    {
        this(fullyQualifiedName, false);
    }

    private static String extractSimpleName(String fullyQualifiedName)
    {
        if (!fullyQualifiedName.contains("."))
        {
            throw new IllegalArgumentException(fullyQualifiedName + " doesn't contain . therefore it is clearly not fully qualified name.");
        }
        String[] nameParts = fullyQualifiedName.split("\\.");
        return nameParts[nameParts.length - 1];
    }

    public boolean hasSameFullyQualifiedName(String fullyQualifiedName)
    {
        return this.fullyQualifiedName.equals(fullyQualifiedName);
    }
}
