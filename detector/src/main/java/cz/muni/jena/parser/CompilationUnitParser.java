package cz.muni.jena.parser;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CompilationUnitParser
{
    private final Path path;
    private final TypeSolver typeSolver;

    public CompilationUnitParser(Path path, Supplier<List<TypeSolver>> typeSolvers)
    {
        this.path = path;
        this.typeSolver = new CombinedTypeSolver(typeSolvers.get());
    }

    public CompilationUnitParser(String path)
    {
        this(FileSystems.getDefault().getPath(path));
    }

    public CompilationUnitParser(Path path)
    {
        this(path, new TypeSolverSupplier(path));
    }

    public List<CompilationUnit> parseCompilationUnits()
    {
        ParserConfiguration parserConfig = new ParserConfiguration();
        JavaSymbolSolver symbolResolver = new JavaSymbolSolver(typeSolver);
        parserConfig.setSymbolResolver(symbolResolver);
        ProjectRoot projectRoot = new SymbolSolverCollectionStrategy(parserConfig).collect(path);

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        for (SourceRoot sr : projectRoot.getSourceRoots())
        {
            try
            {
                List<ParseResult<CompilationUnit>> parseResults = sr.tryToParse();
                for (ParseResult<CompilationUnit> parseResult : parseResults)
                {
                    if (!parseResult.isSuccessful())
                    {
                        continue;
                    }
                    parseResult.getResult().ifPresent(compilationUnits::add);
                }
            } catch (IOException ignored)
            {

            }
        }

        return compilationUnits;
    }

    public List<ClassOrInterfaceDeclaration> findAllClassOrInterfaceDeclaration()
    {
        return parseCompilationUnits().stream()
                .flatMap(compilationUnit -> compilationUnit.findAll(ClassOrInterfaceDeclaration.class).stream())
                .toList();
    }
}
