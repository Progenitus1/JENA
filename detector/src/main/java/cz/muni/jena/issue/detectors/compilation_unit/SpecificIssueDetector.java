package cz.muni.jena.issue.detectors.compilation_unit;

import cz.muni.jena.issue.IssueCategory;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface SpecificIssueDetector extends IssueDetector
{
    @NonNull
    IssueCategory getIssueCategory();
}
