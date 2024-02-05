package cz.muni.jena.frontend.commands.export;

import cz.muni.jena.issue.Issue;

import java.util.Collection;

public interface CSVExporter
{
    String exportDataToCSVFile(String exportDirectory, Collection<Issue> issues);
}
