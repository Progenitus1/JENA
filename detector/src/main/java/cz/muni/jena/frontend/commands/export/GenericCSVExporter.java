package cz.muni.jena.frontend.commands.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.function.BiConsumer;

@Component
public class GenericCSVExporter
{
    public <T> void exportData(
            String fileName,
            String[] header,
            Collection<T> exportedData,
            BiConsumer<T, CSVPrinter> dataPrinter
    )
    {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(header)
                .build();

        try (final CSVPrinter printer = new CSVPrinter(new FileWriter(fileName), csvFormat))
        {
            exportedData.forEach(data -> dataPrinter.accept(data, printer));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
