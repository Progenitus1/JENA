package cz.muni.jena.frontend.commands.evolution;

import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueDao;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.TableBuilder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class AntipatternEvolutionService
{
    private final IssueDao issueDao;

    @Inject
    public AntipatternEvolutionService(IssueDao issueDao)
    {
        this.issueDao = issueDao;
    }

    public String analyzeEvolutionOfAntipatterns()
    {
        Map<IssueGrouping, Map<IssueLocation, Long>> projectsIssueMap = issueDao.findAll().stream()
                .collect(
                        Collectors.groupingBy(
                                IssueGrouping::from,
                                Collectors.groupingBy(IssueLocation::from, Collectors.counting())
                        )
                );

        List<String[]> rows = projectsIssueMap.entrySet()
                .stream()
                .flatMap(this::analyzeReleaseAntipatternDifferences)
                .toList();
        String[][] rowsWithHeader = new String[rows.size() + 1][];
        rowsWithHeader[0] = new String[]{
                "Old release" + '\t',
                "New release" + '\t',
                "Issue category" + '\t',
                "Issue retention"
        };
        for(int i = 0; i < rows.size(); i++)
        {
            rowsWithHeader[i + 1] = rows.get(i);
        }
        return new TableBuilder(new ArrayTableModel(rowsWithHeader)).build().render(100);
    }

    private Stream<String[]> analyzeReleaseAntipatternDifferences(
            Map.Entry<IssueGrouping, Map<IssueLocation, Long>> releases
    )
    {
        Map<String, Map<IssueSpecificLocation, Long>> groupedReleases = releases.getValue()
                .entrySet()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                entry -> entry.getKey().projectLabel(),
                                Collectors.toMap(
                                        entry -> IssueSpecificLocation.from(entry.getKey()),
                                        Map.Entry::getValue,
                                        Long::sum
                                )
                        ));
        List<Map.Entry<String, Map<IssueSpecificLocation, Long>>> sortedReleases = groupedReleases.entrySet()
                .stream()
                .sorted((release1, release2) -> release2.getKey().compareTo(release1.getKey()))
                .toList();
        return IntStream.range(0, sortedReleases.size() - 1)
                .boxed()
                .map(index -> analyzeReleaseAntipatternDifferences(
                        sortedReleases.get(index).getValue(),
                        sortedReleases.get(index + 1).getValue(),
                        sortedReleases.get(index).getKey(),
                        sortedReleases.get(index + 1).getKey(),
                        releases.getKey().issueCategory()
                ));
    }

    private String[] analyzeReleaseAntipatternDifferences(
            Map<IssueSpecificLocation, Long> release1,
            Map<IssueSpecificLocation, Long> release2,
            String release1Name,
            String release2Name,
            IssueCategory issueCategory
    )
    {
        int issuesMatching = 0;
        for (Map.Entry<IssueSpecificLocation, Long> oldProjectIssues : release1.entrySet())
        {
            Long issueCountOfMatchingClassOrType = Optional.ofNullable(release2.get(oldProjectIssues.getKey()))
                    .orElse(0L);
            issuesMatching += Long.min(issueCountOfMatchingClassOrType, oldProjectIssues.getValue());
        }
        long issuesTotalInOldRelease = release1.values().stream().reduce(0L, Long::sum);
        return new String[]{
                release1Name + '\t',
                release2Name + '\t',
                issueCategory.toString() + '\t',
                String.valueOf(((double) issuesMatching) / ((double) issuesTotalInOldRelease))
        };
    }
}
