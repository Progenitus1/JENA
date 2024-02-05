package cz.muni.jena.configuration.security;

import java.util.List;
import java.util.Set;

public record SecurityConfiguration(
        String sensitiveInformationRegex,
        String configurationFileRegex,
        TokenLifetimeSettings tokenLifetimeSettings,
        List<EncryptionAlgorithm> encryptionAlgorithms,
        Set<String> jwtSigningMethods,
        List<String> unsecureCommunicationRegexes
)
{
}
