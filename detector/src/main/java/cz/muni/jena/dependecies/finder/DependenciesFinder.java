package cz.muni.jena.dependecies.finder;


import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;

import java.io.IOException;
import java.util.List;

public interface DependenciesFinder
{
    List<JarTypeSolver> findJarTypeSolvers(String projectPath) throws IOException;
}
