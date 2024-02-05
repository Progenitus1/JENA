package cz.muni.jena.frontend.commands.export;

import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueClassDao;
import cz.muni.jena.issue.IssueDao;
import cz.muni.jena.issue.IssueMethodDao;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import javax.inject.Inject;
import java.util.List;

@Command
public class ExportCommand
{
    private static final String EXPORT_DATA_DESCRIPTION = "Exports the data from database to csv files.";
    private static final String EXPORT_DIRECTORY_DESCRIPTION = "Absolute path to the directory where export files will be written.";
    private static final String EXPORT_TYPE_DESCRIPTION = """
            Chooses what data should be exported. Possible options:
            JUST_ISSUES - Exports only issues with foreign keys to methods and classes.
            JOIN_ALL_DATA - Join issue, method and class table into single one and exports it.
            EVERYTHING_SEPARATELY - Exports issue, method and class tables separately. It includes more data than JOIN_ALL_DATA
            because it includes even the classes and methods without anti-patterns.
            """;
    private final IssueDao issueDao;
    private final IssueMethodDao issueMethodDao;
    private final IssueClassDao issueClassesDao;

    @Inject
    public ExportCommand(IssueDao issueDao, IssueMethodDao issueMethodDao, IssueClassDao issueClassDao)
    {
        this.issueDao = issueDao;
        this.issueMethodDao = issueMethodDao;
        this.issueClassesDao = issueClassDao;
    }

    @Command(command = "exportData", description = EXPORT_DATA_DESCRIPTION)
    public String detectIssues(
            @Option(longNames = "exportDirectory", shortNames = 'd', required = true, description = EXPORT_DIRECTORY_DESCRIPTION)
                    String exportDirectory,
            @Option(longNames = "exportType", shortNames = 't', defaultValue = "JUST_ISSUES", description = EXPORT_TYPE_DESCRIPTION)
                    ExportType exportType
    )
    {
        List<Issue> issues = issueDao.findAll();
        CSVExporter csvExporter = switch (exportType)
                {
                    case JUST_ISSUES -> new IssuesCSVExporter(new GenericCSVExporter());
                    case JOIN_ALL_DATA -> new JoinedDataCSVExporter(new GenericCSVExporter());
                    case EVERYTHING_SEPARATELY -> new AllDataSeparateCSVExporter(
                            issueClassesDao.findAll(),
                            issueMethodDao.findAll(),
                            new IssuesCSVExporter(new GenericCSVExporter()),
                            new GenericCSVExporter()
                    );
                };
        return csvExporter.exportDataToCSVFile(exportDirectory, issues);
    }
}
