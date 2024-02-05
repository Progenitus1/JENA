package cz.muni.jena;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class Preconditions
{
    public static void verifyCorrectWorkingDirectory()
    {
        assertThat(System.getProperty("user.dir")).endsWith(File.separator + "detector");
    }
}
