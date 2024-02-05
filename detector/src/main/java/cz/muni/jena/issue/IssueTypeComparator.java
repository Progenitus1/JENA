package cz.muni.jena.issue;

import java.util.Comparator;

public class IssueTypeComparator implements Comparator<IssueType>
{
    @Override
    public final int compare(IssueType issueType, IssueType otherIssueType)
    {
        int categoryComparison = issueType.getCategory().toString().compareTo(otherIssueType.getCategory().toString());
        if (categoryComparison == 0)
        {
            return issueType.getOrder().compareTo(otherIssueType.getOrder());
        }
        return categoryComparison;
    }
}
