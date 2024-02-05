package cz.muni.jena.parser;

public record ThreadExecutionLog(
        String threadName,
        long runningTimeInMilliseconds,
        int classesOrInterfacesAnalysed,
        int linesTotal
)
{
}
