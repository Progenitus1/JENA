package cz.muni.jena.configuration.security;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public record TokenLifetimeSettings(
        long maxTokenLifetime,
        ChronoUnit unit
)
{
    public Duration toDuration()
    {
        return Duration.of(maxTokenLifetime, unit);
    }
}
