package cz.muni.jena.frontend.commands.antipattern.correlation;

import org.springframework.shell.command.annotation.Command;

import javax.inject.Inject;

@Command
public class AntipatternCorrelationTestCommand
{
    private static final String ANTIPATTERN_CORRELATION_DESCRIPTION = "Explores what kinds of relationship are between the " +
            "occurrences of different types of anti-patterns.";
    private final AntipatternCorrelationTestService antipatternCorrelationTestService;

    @Inject
    public AntipatternCorrelationTestCommand(AntipatternCorrelationTestService antipatternCorrelationTestService)
    {
        this.antipatternCorrelationTestService = antipatternCorrelationTestService;
    }

    @Command(command = "antipatternCorrelationTest", description = ANTIPATTERN_CORRELATION_DESCRIPTION)
    public String testComplexityAntipatternCorrelation()
    {
        return antipatternCorrelationTestService.analyseAntipatternCorrelation();
    }
}
