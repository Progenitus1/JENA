package cz.muni.jena.frontend.commands.evolution;

import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueType;

public record IssueLocation(IssueType issueType, String classFullyQualifiedName, String projectLabel)
{
    public static IssueLocation from(Issue issue)
    {
        return new IssueLocation(issue.getIssueType(), issue.getFullyQualifiedName(), issue.getProjectLabel());
    }
}
