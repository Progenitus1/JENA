package cz.muni.jena.frontend.commands.export;

import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueClass;
import cz.muni.jena.issue.IssueMethod;

import java.io.IOException;
import java.util.Collection;

public class AllDataSeparateCSVExporter implements CSVExporter
{
    private static final String[] CLASS_HEADER = {
            "class_id",
            "class_complexity",
            "class_name",
            "project_label"
    };
    private static final String[] METHOD_HEADER = {
            "method_id",
            "method_complexity",
            "method_name",
            "project_label"
    };

    private final Collection<IssueClass> issueClasses;
    private final Collection<IssueMethod> issueMethods;
    private final IssuesCSVExporter issuesCSVExporter;
    private final GenericCSVExporter genericCSVExporter;

    public AllDataSeparateCSVExporter(
            Collection<IssueClass> issueClasses,
            Collection<IssueMethod> issueMethods,
            IssuesCSVExporter issuesCSVExporter,
            GenericCSVExporter genericCSVExporter)
    {
        this.issueClasses = issueClasses;
        this.issueMethods = issueMethods;
        this.issuesCSVExporter = issuesCSVExporter;
        this.genericCSVExporter = genericCSVExporter;
    }

    @Override
    public String exportDataToCSVFile(String exportDirectory, Collection<Issue> issues)
    {
        issuesCSVExporter.exportDataToCSVFile(exportDirectory, issues);
        exportClasses(exportDirectory);
        exportMethods(exportDirectory);
        return "3 export files should be in this directory: " + exportDirectory;
    }

    private void exportClasses(String exportDirectory)
    {
        genericCSVExporter.exportData(
                exportDirectory + "/Classes.csv",
                CLASS_HEADER, issueClasses, (issueClass, printer) ->
                {
                    try
                    {
                        printer.printRecord(
                                issueClass.getId(),
                                issueClass.getComplexity(),
                                issueClass.getName(),
                                issueClass.getProjectLabel()
                        );
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
        );
    }

    private void exportMethods(String exportDirectory)
    {
        genericCSVExporter.exportData(
                exportDirectory + "/methods.csv",
                METHOD_HEADER, issueMethods, (issueMethod, printer) ->
                {
                    try
                    {
                        printer.printRecord(
                                issueMethod.getId(),
                                issueMethod.getComplexity(),
                                issueMethod.getName(),
                                issueMethod.getProjectLabel()
                        );
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
        );
    }
}
