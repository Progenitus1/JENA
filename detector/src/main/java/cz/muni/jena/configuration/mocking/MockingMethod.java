package cz.muni.jena.configuration.mocking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public record MockingMethod(
        String fullyQualifiedName,
        ArgumentType argumentType,
        Boolean argumentHasToBeFinal
)
{
    @JsonCreator
    public MockingMethod(
            @JsonProperty("fullyQualifiedName")String fullyQualifiedName,
            @JsonProperty("argumentType")ArgumentType argumentType,
            @JsonProperty("argumentHasToBeFinal")Boolean argumentHasToBeFinal
    )
    {
        this.fullyQualifiedName = fullyQualifiedName;
        this.argumentType = argumentType;
        this.argumentHasToBeFinal = Optional.ofNullable(argumentHasToBeFinal).orElse(true);
    }
}
