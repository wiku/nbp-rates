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
    public static void main( String[] args )
    {
        AppOptionsParser argsParser = new AppOptionsParser("java -jar nbp-rates-1.0-SNAPSHOT-jar-with-dependencies.jar");
        Optional<AppOptions> optionalOptions = argsParser.getOptions(args);
        if( optionalOptions.isPresent() )
        {
            printRatesForFile(optionalOptions.get(), System.out);
        }
        else
        {
            System.exit(1);
        }
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

                        BigDecimal exchangeRate = fetchRequestedRate(options, fetcher, date, symbol);
                        printRateToOutput(options, out, date, symbol, exchangeRate);
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

    private static void printRateToOutput( AppOptions options,
            PrintStream out,
            String date,
            String symbol,
            BigDecimal rate )
    {
        if( options.isFullOutput() )
        {
            out.printf("%s;%s;%s%n", date, symbol, rate);
        }
        else
        {
            out.println(rate);
        }
    }

    private static BigDecimal fetchRequestedRate( AppOptions options,
            RateFetcher fetcher,
            String date,
            String symbol ) throws NBPRateFetcherException
    {
        BigDecimal rate;
        if( options.isFetchForPreviousDay() )
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
