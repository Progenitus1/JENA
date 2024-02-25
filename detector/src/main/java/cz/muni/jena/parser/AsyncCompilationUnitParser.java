package cz.muni.jena.parser;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AsyncCompilationUnitParser
{
    private final Path path;
    private final TypeSolver typeSolver;

    public AsyncCompilationUnitParser(
            Path path,
            Supplier<List<TypeSolver>> typeSolversSupplier
    )
    {
        this.path = path;
        List<TypeSolver> typeSolvers = typeSolversSupplier.get();
        this.typeSolver = new CombinedTypeSolver(typeSolvers);
        logTypeSolvers(path, typeSolvers);
    }

    private void logTypeSolvers(Path path, List<TypeSolver> typeSolvers)
    {
        Logger logger = LoggerFactory.getLogger(TypeSolverSupplier.class);
        long jarTypeSolversCount = typeSolvers.stream()
                .filter(JarTypeSolver.class::isInstance)
                .count();
        if (jarTypeSolversCount == 0L)
        {
            logger.atError().log(
                    String.format(
                            """
                                    We failed to find any dependency Jars in %s.
                                    Without dependency Jars Jena will likely not function properly.
                                    """,
                            path.toAbsolutePath() + File.separator + "target" + File.separator + "dependency"
                    ));
        } else
        {
            logger.atInfo().log(String.format("We found %s dependency Jars.", jarTypeSolversCount));
        }
    }

    public AsyncCompilationUnitParser(String path)
    {
        this(FileSystems.getDefault().getPath(path));
    }

    public AsyncCompilationUnitParser(Path path)
    {
        this(path, new TypeSolverSupplier(path));
    }

    public void processCompilationUnits(SourceRoot.Callback callback)
    {
        ParserConfiguration parserConfig = new ParserConfiguration();
        JavaSymbolSolver symbolResolver = new JavaSymbolSolver(typeSolver);
        parserConfig.setSymbolResolver(symbolResolver);
        ProjectRoot projectRoot = new SymbolSolverCollectionStrategy(parserConfig).collect(path);
        Logger logger = LoggerFactory.getLogger(AsyncCompilationUnitParser.class);
        logger.atInfo().log(
                "We will analyze following modules: "
                        + System.lineSeparator()
                        + projectRoot.getSourceRoots()
                        .stream()
                        .map(SourceRoot::getRoot)
                        .map(Path::toAbsolutePath)
                        .map(Path::toString)
                        .collect(Collectors.joining(System.lineSeparator()))
        );
        for (SourceRoot sourceRoot : projectRoot.getSourceRoots())
        {
            try
            {
                sourceRoot.parseParallelized(callback);
            } catch (IOException ignored)
            {
                logger.atWarn().log("We weren't able to parse following module: " + sourceRoot.getRoot().toAbsolutePath());
            }
        }
    }
}
