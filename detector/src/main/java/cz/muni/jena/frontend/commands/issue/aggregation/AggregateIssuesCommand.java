package cz.muni.jena.frontend.commands.issue.aggregation;

import cz.muni.jena.issue.IssueTypeComparator;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.TableBuilder;

import javax.inject.Inject;
import java.util.Map;

@Command
public class AggregateIssuesCommand
{
    private static final String AGGREGATE_ISSUES_DESCRIPTION = "Aggregate issues command presents in structured way" +
            " what types if anti-patterns are in database.";
    private static final String LABEL_DESCRIPTION = "Label of the project for which we would like to aggregate";
    private final IssueAggregationDao issueAggregationDao;

    @Inject
    public AggregateIssuesCommand(IssueAggregationDao issueAggregationDao)
    {
        this.issueAggregationDao = issueAggregationDao;
    }

    @Command(command = "aggregateIssues", description = AGGREGATE_ISSUES_DESCRIPTION)
    public String aggregateIssues(
            @Option(longNames = "projectLabel", shortNames = 'l', description = LABEL_DESCRIPTION) String label
    )
    {
        String[][] rows = issueAggregationDao.aggregateIssues(label)
                .entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue()))
                .sorted(Map.Entry.comparingByKey(new IssueTypeComparator()))
                .map(entry -> new String[]{entry.getKey().toString() + '\t', entry.getValue().toString()})
                .toArray(String[][]::new);
        return new TableBuilder(new ArrayTableModel(rows)).build().render(50);
    }
}
