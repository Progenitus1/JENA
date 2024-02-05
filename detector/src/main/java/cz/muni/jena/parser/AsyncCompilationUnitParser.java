package cz.muni.jena.parser;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AsyncCompilationUnitParser
{
    private final Path path;
    private final Supplier<TypeSolver> typeSolverSupplier;

    public AsyncCompilationUnitParser(
            Path path,
            Supplier<TypeSolver> typeSolverSupplier
    )
    {
        this.path = path;
        this.typeSolverSupplier = typeSolverSupplier;
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
        JavaSymbolSolver symbolResolver = new JavaSymbolSolver(typeSolverSupplier.get());
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
