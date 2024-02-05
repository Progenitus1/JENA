package cz.muni.jena.frontend.commands.issue.aggregation;

import cz.muni.jena.issue.IssueType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IssueAggregationDao
{
    @PersistenceContext
    private EntityManager entityManager;

    public Map<IssueType, Long> aggregateIssues(String projectLabel)
    {
        Map<IssueType, Long> aggregatedIssues = Arrays.stream(IssueType.values())
                .collect(Collectors.toMap(
                        issueType -> issueType,
                        issueType -> 0L
                ));
        Optional.ofNullable(projectLabel).map(
                label -> entityManager.createQuery(
                                "select i.issueType, count(i) from Issue i where i.projectLabel = :label group by i.issueType",
                                Tuple.class
                        )
                        .setParameter("label", label)
                )
                .orElse(entityManager.createQuery(
                        "select i.issueType, count(i) from Issue i group by i.issueType",
                        Tuple.class))
                .getResultList()
                .forEach(tuple -> aggregatedIssues.put(
                        tuple.get(0, IssueType.class),
                        tuple.get(1, Long.class)
                ));
        return aggregatedIssues;
    }
}
