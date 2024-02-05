package cz.muni.jena.frontend.commands.evolution;

import cz.muni.jena.issue.IssueType;

public record IssueSpecificLocation(IssueType issueType, String classFullyQualifiedName)
{
    public static IssueSpecificLocation from(IssueLocation issueLocation)
    {
        return new IssueSpecificLocation(issueLocation.issueType(), issueLocation.classFullyQualifiedName());
    }
}
