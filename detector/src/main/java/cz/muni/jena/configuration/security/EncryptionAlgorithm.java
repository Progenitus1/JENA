package cz.muni.jena.configuration.security;

public record EncryptionAlgorithm(
        String name,
        int defaultStrength,
        int strengthRequired
)
{
}
