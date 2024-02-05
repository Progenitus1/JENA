package cz.muni.jena.frontend.commands.complexity.test;

import cz.muni.jena.frontend.commands.export.GenericCSVExporter;
import cz.muni.jena.issue.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.TableBuilder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Service
public class ComplexityTestService
{
    private final IssueDao issueDao;
    private final IssueMethodDao issueMethodDao;
    private final IssueClassDao issueClassDao;
    private final GenericCSVExporter genericCSVExporter;

    @Inject
    public ComplexityTestService(
            IssueDao issueDao,
            IssueMethodDao issueMethodDao,
            IssueClassDao issueClassDao,
            GenericCSVExporter genericCSVExporter
    )
    {
        this.issueDao = issueDao;
        this.issueMethodDao = issueMethodDao;
        this.issueClassDao = issueClassDao;
        this.genericCSVExporter = genericCSVExporter;
    }

    public String testComplexityAntipatternRelationshipInClasses(String exportPath)
    {
        return analyseCorrelationOfClassComplexityWithAntipatternCount(exportPath) + System.lineSeparator()
                + analyseCorrelationOfMethodComplexityWithAntipatternCount(exportPath);
    }

    private String analyseCorrelationOfClassComplexityWithAntipatternCount(String exportPath)
    {
        Map<IssueType, Map<IssueClass, Integer>> issueCountPerClass = issueCountGroupedByEntity(Issue::getIssueClass);
        issueClassDao.findAll().stream().collect(
                Collectors.toMap(
                        Function.identity(),
                        issueClass -> 0,
                        Integer::sum
                )
        ).forEach((key, value) -> issueCountPerClass.forEach(
                (key1, value1) -> value1.merge(key, value, Integer::sum)
        ));
        return presentCorrelations(
                issueCountPerClass,
                "Correlations of complexity and anti-pattern count for classes",
                IssueClass::getComplexity,
                Optional.ofNullable(exportPath).map(path -> path + File.separator + "Class").orElse(null)
        );
    }

    private <T> String presentCorrelations(
            Map<IssueType, Map<T, Integer>> issueCountPerEntity,
            String header,
            ToDoubleFunction<T> getComplexity,
            String exportPath
    )
    {
        String[][] rowsWithHeader = new String[1 + issueCountPerEntity.size()][];
        rowsWithHeader[0] = new String[]{"Issue type", "p-value ", "covariance"};
        int i = 1;
        Optional.ofNullable(exportPath).ifPresent(
                dataExportPath -> exportCCData(issueCountPerEntity, getComplexity, dataExportPath)
        );
        for (Map.Entry<IssueType, Map<T, Integer>> entry : issueCountPerEntity.entrySet())
        {
            String[] spearmanCorrelation = calculateSpearmanCorrelation(
                    mapToDoubleArray(entry.getValue(), getComplexity)
            );
            rowsWithHeader[i] = new String[]{
                    entry.getKey().getCategory() + entry.getKey().getOrder().toString() + " ",
                    spearmanCorrelation[0],
                    spearmanCorrelation[1]
            };
            i += 1;
        }
        return header + System.lineSeparator() + new TableBuilder(new ArrayTableModel(rowsWithHeader)).build().render(
                100);
    }

    private <T> void exportCCData(Map<IssueType, Map<T, Integer>> issueCountPerEntity, ToDoubleFunction<T> getComplexity, String dataExportPath)
    {
        genericCSVExporter.exportData(
                dataExportPath +  "CCData.csv",
                new String[]{"IssueType", "CC", "AntipatternCount"},
                issueCountPerEntity.entrySet(),
                (entry, printer) -> entry.getValue().forEach(
                        (key, value) ->
                        {
                            try
                            {
                                printer.printRecord(
                                        entry.getKey(),
                                        getComplexity.applyAsDouble(key),
                                        value
                                );
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                )
        );
    }

    private String analyseCorrelationOfMethodComplexityWithAntipatternCount(String exportPath)
    {
        Map<IssueType, Map<IssueMethod, Integer>> issueCountPerMethod = issueCountGroupedByEntity(Issue::getMethod);
        issueMethodDao.findAll().stream().collect(
                Collectors.toMap(
                        Function.identity(),
                        issueClass -> 0,
                        Integer::sum
                )
        ).forEach((key, value) -> issueCountPerMethod.forEach(
                (key1, value1) -> value1.merge(key, value, Integer::sum)
        ));
        return presentCorrelations(
                issueCountPerMethod,
                "Correlations of complexity and anti-pattern count for methods",
                IssueMethod::getComplexity,
                Optional.ofNullable(exportPath).map(path -> path + File.separator + "Method").orElse(null)
        );
    }

    private <T> Map<IssueType, Map<T, Integer>> issueCountGroupedByEntity(Function<Issue, T> issueToGroupedByEntity)
    {
        return issueDao.findAll().stream()
                .filter(issue -> issueToGroupedByEntity.apply(issue) != null)
                .collect(Collectors.groupingBy(Issue::getIssueType, Collectors.toMap(
                        issueToGroupedByEntity,
                        issue -> 1,
                        Integer::sum
                )));
    }

    private String[] calculateSpearmanCorrelation(double[][] data)
    {
        Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(data);
        PearsonsCorrelation rankCorrelation = new SpearmansCorrelation(matrix).getRankCorrelation();
        RealMatrix correlationPValues = rankCorrelation.getCorrelationPValues();
        return new String[]{
                round(correlationPValues.getData()[0][1]) + " ",
                String.valueOf(round(rankCorrelation.getCorrelationMatrix().getData()[0][1]))
        };
    }

    double round(double value)
    {
        return Math.round(value * Math.pow(10, 5)) / Math.pow(10, 5);
    }

    private <T> double[][] mapToDoubleArray(Map<T, Integer> issueCountPerClass, ToDoubleFunction<T> mapping)
    {
        List<Map.Entry<T, Integer>> entries = issueCountPerClass.entrySet().stream().toList();
        double[][] issuesPerClass = new double[issueCountPerClass.size()][2];
        for (int i = 0; i < entries.size(); i++)
        {
            double complexity = mapping.applyAsDouble(entries.get(i).getKey());
            Double issueCount = Double.valueOf(entries.get(i).getValue());
            issuesPerClass[i] = new double[]{
                    complexity,
                    issueCount
            };
        }
        return issuesPerClass;
    }
}
