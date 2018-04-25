package com.wiku;

import com.wiku.nbp.application.CachedRateFetcher;
import com.wiku.nbp.application.NBPRateFetcher;
import com.wiku.nbp.application.RateFetcher;
import com.wiku.nbp.application.RateFetcherException;
import com.wiku.nbp.infrastructure.sources.RateSourceFactory;
import com.wiku.rest.client.RestClient;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class App
{

    public static final String RUN_COMMAND = "java -jar nbp-rates-1.0-SNAPSHOT-jar-with-dependencies.jar";

    public static void main( String[] args )
    {
        AppOptionsParser argsParser = new AppOptionsParser(RUN_COMMAND);

        try
        {
            AppOptions options = argsParser.getOptions(args);
            printRatesForFile(options, System.out);
        }
        catch( ParseException e )
        {
            System.err.println("Invalid command line arguments: " + e.toString());
            argsParser.printHelp();
            System.exit(1);
        }
    }

    static void printRatesForFile( AppOptions options, PrintStream out )
    {
        RateFetcher fetcher = new CachedRateFetcher(new NBPRateFetcher(new RateSourceFactory(new RestClient())));
        try
        {
            Files.lines(Paths.get(options.getInputFile())).sequential().forEach(line -> {

                if( !line.isEmpty() )
                {
                    String dateString = "<unknown_date>";
                    String symbol = "<unknown_symbol>";
                    try
                    {
                        String[] lineSplit = line.split(";");
                        dateString = lineSplit[0].trim();
                        symbol = lineSplit[1].trim();

                        LocalDate date = LocalDate.parse(dateString);
                        fetchRateAndPrintToOutput(options, out, fetcher, date, symbol);
                    }
                    catch( Exception e )
                    {
                        printRateToOutput(out, dateString, symbol, "ERROR: " + e.toString(), options.isFullOutput());
                        if(options.isVerbose())
                            e.printStackTrace();
                    }
                }
            });
        }
        catch( IOException e )
        {
            System.err.println("Error occured while reading file: " + options.getInputFile() + ": " + e.getMessage());

            if( options.isVerbose() )
                e.printStackTrace();

            System.exit(1);
        }

    }

    private static void fetchRateAndPrintToOutput( AppOptions options,
            PrintStream out,
            RateFetcher fetcher,
            LocalDate date,
            String symbol ) throws RateFetcherException
    {

        BigDecimal exchangeRate = fetchRequestedRate(fetcher, date, symbol, options.isFetchForPreviousDay());
        printRateToOutput(out,
                date.format(DateTimeFormatter.ISO_DATE),
                symbol,
                exchangeRate.toPlainString(),
                options.isFullOutput());

    }

    private static BigDecimal fetchRequestedRate( RateFetcher fetcher,
            LocalDate date,
            String symbol,
            boolean isFetchForPreviousDay ) throws RateFetcherException
    {
        return fetcher.fetchRateForDay(symbol, isFetchForPreviousDay ? date.minusDays(1) : date);
    }

    private static void printRateToOutput( PrintStream out,
            String dateString,
            String symbol,
            String rate,
            boolean isFullOutput )
    {
        if( isFullOutput )
        {
            out.printf("%s;%s;%s%n", dateString, symbol, rate);
        }
        else
        {
            out.println(rate);
        }
    }

}
