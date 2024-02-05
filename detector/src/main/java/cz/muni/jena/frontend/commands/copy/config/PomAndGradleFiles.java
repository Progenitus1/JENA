package cz.muni.jena.frontend.commands.copy.config;

import java.util.Collection;

public record PomAndGradleFiles(Collection<String> mavenFiles, Collection<String> gradleFiles)
{
}
