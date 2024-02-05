package cz.muni.jena.frontend.commands.copy.config;

import cz.muni.jena.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Command
public class CopyConfigCommand
{

    private static final String PATH_DESCRIPTION = "Path where the configuration should be exported";
    private static final String COPY_CONFIG_DESCRIPTION = "Export the default configuration file. After you export it then" +
            " you can edit it and use it's absolute path as a parameter for detectIssues command.";

    @Command(command = "copyConfig", description = COPY_CONFIG_DESCRIPTION)
    public String copyConfig(
            @Option(longNames = "path", shortNames = 'p', required = true, description = PATH_DESCRIPTION) String path
    )
    {
        URL configurationFileURL = Configuration.getConfigurationURL();
        try
        {
            FileUtils.copyURLToFile(configurationFileURL, new File(path));
        } catch (IOException e)
        {
            return "Copying the default configuration file failed. Consider double checking if the path parameter is legal file name.";
        }
        return "Default configuration file has been copied to: " + path;
    }
}
