package com.wiku;

import com.wiku.nbp.CachedRateFetcher;
import com.wiku.nbp.NBPRateFetcher;
import com.wiku.nbp.NBPRateFetcherException;
import com.wiku.nbp.RateFetcher;
import com.wiku.rest.client.RestClient;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class App
{

    public static final String RUN_COMMAND = "java -jar nbp-rates-1.0-SNAPSHOT-jar-with-dependencies.jar";

    public static void main( String[] args )
    {
        AppOptionsParser argsParser = new AppOptionsParser(RUN_COMMAND);
        Optional<AppOptions> optionalOptions = argsParser.getOptions(args);
        optionalOptions.ifPresent(( options ) -> printRatesForFile(options, System.out));
    }

    static void printRatesForFile( AppOptions options, PrintStream out )
    {
        RateFetcher fetcher = new CachedRateFetcher(new NBPRateFetcher(new RestClient()));

        try
        {
            Files.lines(Paths.get(options.getInputFile())).sequential().forEach(line -> {

                if( !line.isEmpty() )
                {
                    try
                    {
                        String[] lineSplit = line.split(";");
                        String date = lineSplit[0].trim();
                        String symbol = lineSplit[1].trim();

                        fetchRateAndPrintToOutput(options, out, fetcher, date, symbol);
                    }
                    catch( Exception e )
                    {
                        System.err.println("Failed to fetch currency rate for line: " + line + ": " + e.getMessage());
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            });
        }
        catch( IOException e )
        {
            System.err.println("Error occured while reading file: " + options.getInputFile() + ": " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static void fetchRateAndPrintToOutput( AppOptions options,
            PrintStream out,
            RateFetcher fetcher,
            String date,
            String symbol )
    {
        try
        {
            BigDecimal exchangeRate = fetchRequestedRate(fetcher, date, symbol, options.isFetchForPreviousDay());
            printRateToOutput(out, date, symbol, exchangeRate.toPlainString(), options.isFullOutput());
        }
        catch( NBPRateFetcherException e )
        {
            printRateToOutput(out, date, symbol, "ERROR: " + e.getMessage(), options.isFullOutput());
        }
    }

    private static void printRateToOutput( PrintStream out,
            String date,
            String symbol,
            String rate,
            boolean isFullOutput )
    {
        if( isFullOutput )
        {
            out.printf("%s;%s;%s%n", date, symbol, rate);
        }
        else
        {
            out.println(rate);
        }
    }

    private static BigDecimal fetchRequestedRate( RateFetcher fetcher,
            String date,
            String symbol,
            boolean isFetchForPreviousDay ) throws NBPRateFetcherException
    {
        BigDecimal rate;
        if( isFetchForPreviousDay )
        {
            rate = fetcher.fetchRateForPreviousWorkingDay(symbol, date);
        }
        else
        {
            rate = fetcher.fetchRateForDay(symbol, date);
        }
        return rate;
    }
}
