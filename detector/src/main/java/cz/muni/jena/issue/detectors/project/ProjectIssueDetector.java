package cz.muni.jena.issue.detectors.project;

import cz.muni.jena.configuration.Configuration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface ProjectIssueDetector
{
    Stream<Issue> findIssues(String projectPath, Configuration configuration);

    default Map<String, List<String>> getLines(List<String> filesNames)
    {
        Map<String, List<String>> linesMap = new HashMap<>();
        for (String filePath : filesNames)
        {
            try
            {
                Path path = Path.of(filePath);
                List<String> strings = Files.readAllLines(path);
                linesMap.put(filePath, strings);
            } catch (java.io.IOException ignored)
            {
            }
        }
        return linesMap;
    }

    default Stream<Issue> findAllLinesMatchingRegex(
            Map<String, List<String>> allLines,
            IssueType issueType,
            List<Pattern> regexes
    )
    {
        return allLines
                .entrySet()
                .stream()
                .flatMap(
                        entry -> IntStream.range(0, entry.getValue().size())
                                .boxed()
                                .map(i -> new ImmutableTriple<>(i, entry.getValue().get(i), entry.getKey()))
                )
                .filter(numberedLine -> regexes.stream().anyMatch(
                        regex -> regex.matcher(numberedLine.getMiddle()).find()
                ))
                .map(
                        numberedLine -> new Issue(
                                issueType,
                                numberedLine.left + 1,
                                numberedLine.getRight()
                        )
                );
    }

    default List<String> findProjectFilesNames(String projectPath)
    {
        try (Stream<Path> stream = Files.walk(Paths.get(projectPath)))
        {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::toString)
                    .toList();
        } catch (RuntimeException | IOException e)
        {
            return List.of();
        }
    }

    @NonNull IssueCategory getIssueCategory();
}
