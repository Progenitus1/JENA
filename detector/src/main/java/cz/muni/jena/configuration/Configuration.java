package cz.muni.jena.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import cz.muni.jena.configuration.di.DIConfiguration;
import cz.muni.jena.configuration.mocking.MockingConfiguration;
import cz.muni.jena.configuration.persistence.PersistenceConfiguration;
import cz.muni.jena.configuration.security.SecurityConfiguration;
import cz.muni.jena.configuration.service_layer.ServiceLayerConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public record Configuration(
        DIConfiguration diConfiguration,
        MockingConfiguration mockingConfiguration,
        SecurityConfiguration securityConfiguration,
        ServiceLayerConfiguration serviceLayerConfiguration,
        PersistenceConfiguration persistenceConfiguration
)
{
    public static Optional<Configuration> readConfiguration(String path)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader configurationReader = objectMapper.readerFor(Configuration.class);
        try
        {
            return Optional.of(configurationReader.readValue(new File(path)));
        } catch (IOException e)
        {
            return Optional.empty();
        }
    }

    public static Configuration readConfiguration()
    {
        try
        {
            URL configurationFileURL = getConfigurationURL();
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectReader configurationReader = objectMapper.readerFor(Configuration.class);
            return configurationReader.readValue(configurationFileURL);
        } catch (IOException e)
        {
            throw new IllegalStateException(
                    "Configuration couldn't be loaded, please check if they are in correct format.", e
            );
        }
    }

    public static URL getConfigurationURL()
    {
        return Configuration.class.getResource("/configuration");
    }
}
