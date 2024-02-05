package cz.muni.jena.frontend.commands.evolution;

import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;

public record IssueGrouping(IssueCategory issueCategory, String project)
{
    public static IssueGrouping from(Issue issue)
    {
        return new IssueGrouping(
                issue.getIssueType().getCategory(),
                issue.getProjectLabel().replaceAll("\\d", "")
        );
    }
}
