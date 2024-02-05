package cz.muni.jena.dependecies.finder;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FolderDependenciesFinder implements DependenciesFinder
{
    @Override
    public List<JarTypeSolver> findJarTypeSolvers(String projectPath)
    {
        List<JarTypeSolver> jarTypeSolvers = new ArrayList<>();
        File projectDirectory = Paths.get(projectPath, "target", "dependency").toFile();
        List<String> jars = Optional.ofNullable(
                        projectDirectory.list((current, name) -> new File(current, name).isFile())
                )
                .stream()
                .flatMap(Arrays::stream)
                .filter(path -> path.endsWith(".jar"))
                .toList();
        Logger logger = LoggerFactory.getLogger(FolderDependenciesFinder.class);
        for (String jar : jars)
        {
            Path pathToJar = Paths.get(projectDirectory.getAbsolutePath(), jar);
            try
            {
                jarTypeSolvers.add(new JarTypeSolver(pathToJar));
            } catch (IOException ignored)
            {
                logger.atWarn().log("Jena failed to read following jar: " + pathToJar);
            }
        }

        return jarTypeSolvers;
    }
}
