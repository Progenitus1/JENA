package cz.muni.jena.configuration;

import cz.muni.jena.configuration.di.Annotation;
import cz.muni.jena.configuration.di.DIConfiguration;
import cz.muni.jena.configuration.mocking.ArgumentType;
import cz.muni.jena.configuration.mocking.MockingConfiguration;
import cz.muni.jena.configuration.mocking.MockingMethod;
import cz.muni.jena.configuration.persistence.PersistenceConfiguration;
import cz.muni.jena.configuration.security.EncryptionAlgorithm;
import cz.muni.jena.configuration.security.SecurityConfiguration;
import cz.muni.jena.configuration.security.TokenLifetimeSettings;
import cz.muni.jena.configuration.service_layer.ServiceLayerConfiguration;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static cz.muni.jena.Preconditions.verifyCorrectWorkingDirectory;
import static org.assertj.core.api.Assertions.assertThat;


class ConfigurationLoadingTest
{
    private static final String TEST_CONFIGURATION_PATH = "./src/test/java/cz/muni/jena/configuration/testConfiguration";

    @Test
    void jsonToAnnotationTest()
    {
        verifyCorrectWorkingDirectory();
        Optional<Configuration> configuration = Configuration.readConfiguration(TEST_CONFIGURATION_PATH);
        DIConfiguration diConfiguration = new DIConfiguration(
                List.of(
                        new Annotation("b.cd", false),
                        new Annotation("1.2", true)
                ),
                10,
                6,
                List.of(
                        new Annotation("e.gfg"),
                        new Annotation("5.6")
                ),
                Set.of("abcd")
        );
        MockingConfiguration mockingConfiguration = new MockingConfiguration(
                Set.of("java.io.IOException"),
                List.of(new MockingMethod("efd", ArgumentType.CLASS, true))
        );
        SecurityConfiguration securityConfiguration = new SecurityConfiguration(
                "user",
                "app",
                new TokenLifetimeSettings(
                        1L,
                        ChronoUnit.SECONDS
                ),
                List.of(
                        new EncryptionAlgorithm("name", 2, 5)
                ),
                Set.of("abc"),
                List.of("http")
        );
        assertThat(configuration)
                .isPresent()
                .contains(
                        new Configuration(
                                diConfiguration,
                                mockingConfiguration,
                                securityConfiguration,
                                new ServiceLayerConfiguration(2, 1, Set.of("dfe")),
                                new PersistenceConfiguration(Set.of("a.b.c"), "nPlusOne")
                        )
                );
    }
}
