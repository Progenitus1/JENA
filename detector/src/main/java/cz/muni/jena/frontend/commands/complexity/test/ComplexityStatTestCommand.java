package cz.muni.jena.frontend.commands.complexity.test;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import javax.inject.Inject;

@Command
public class ComplexityStatTestCommand
{
    private static final String COMPLEXITY_TEST_DESCRIPTION = "Complexity test command is a statistical test for discerning " +
            "whether there is a relationship between cyclomatic complexity and the amount of anti-patterns in the code.";
    private static final String EXPORT_PATH_DESCRIPTION = "Path to directory where should we export data to for external analysis";
    private final ComplexityTestService complexityTestService;

    @Inject
    public ComplexityStatTestCommand(ComplexityTestService complexityTestService)
    {
        this.complexityTestService = complexityTestService;
    }

    @Command(command = "complexityTest", description = COMPLEXITY_TEST_DESCRIPTION)
    public String testComplexityAntipatternCorrelation(
            @Option(longNames = "exportPath", shortNames = 'p', description = EXPORT_PATH_DESCRIPTION) String exportPath
    )
    {
        return complexityTestService.testComplexityAntipatternRelationshipInClasses(exportPath);
    }
}
