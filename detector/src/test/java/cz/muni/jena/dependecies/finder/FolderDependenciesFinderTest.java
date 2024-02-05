package cz.muni.jena.dependecies.finder;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static cz.muni.jena.Preconditions.verifyCorrectWorkingDirectory;
import static org.assertj.core.api.Assertions.assertThat;

class FolderDependenciesFinderTest
{
    private static final String MAVEN_PROJECT = ".." + File.separator +"antipatterns";
    private static final String GRADLE_PROJECT = ".." + File.separator + "TestGradleProject";

    @Test
    void findJarTypeSolversInMavenProject()
    {
        verifyCorrectWorkingDirectory();
        List<JarTypeSolver> jarTypeSolvers = new FolderDependenciesFinder().findJarTypeSolvers(MAVEN_PROJECT);
        assertThat(jarTypeSolvers).isNotEmpty();
    }

    @Test
    void findJarTypeSolversInGradleProject()
    {
        verifyCorrectWorkingDirectory();
        List<JarTypeSolver> jarTypeSolvers = new FolderDependenciesFinder().findJarTypeSolvers(GRADLE_PROJECT);
        assertThat(jarTypeSolvers).hasSize(2);
    }
}
