package cz.muni.jena.frontend.commands.evolution;

import org.springframework.shell.command.annotation.Command;

import javax.inject.Inject;

@Command
public class AntipatternEvolutionCommand
{
    private static final String ANTIPATTERN_EVOLUTION_DESCRIPTION = "Anti-pattern evolution command explores what percentage" +
            " of anti-patterns is retained from old releases to new releases of project." +
            " For its proper functioning the releases have to be properly labeled." +
            " They have to have some shared prefix and the suffix has to be number" +
            " and the sufix numbers have to decrement from the oldest release to newest." +
            " Example of proper labeling:" +
            " thingsboard5(this is the oldest), thingsboard4, thingsboard3, thingsboard2, thingsboard1(this is the newest).";
    private final AntipatternEvolutionService antipatternEvolutionService;

    @Inject
    public AntipatternEvolutionCommand(AntipatternEvolutionService antipatternEvolutionService)
    {
        this.antipatternEvolutionService = antipatternEvolutionService;
    }

    @Command(command = "evolutionOfAntipatterns", description = ANTIPATTERN_EVOLUTION_DESCRIPTION)
    public String analyzeEvolutionOfAntipatterns()
    {
        return antipatternEvolutionService.analyzeEvolutionOfAntipatterns();
    }
}
