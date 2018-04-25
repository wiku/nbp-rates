package com.wiku;

import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.*;

import java.util.Optional;

@RequiredArgsConstructor public class AppOptionsParser
{

    public static final String OPT_PREVIOUS = "p";
    public static final String OPT_FULL = "f";
    public static final String OPT_INPUT_FILE = "in";
    private final static Options OPTIONS = createOptions();

    private final String appCommand;

    public AppOptions getOptions( String[] args ) throws ParseException
    {

        CommandLineParser parser = new DefaultParser();
        CommandLine parsedOutput = parser.parse(OPTIONS, args);

        String inputFile = null;
        boolean printFullOutput = false;
        boolean previousDay = false;

        if( parsedOutput.hasOption(OPT_INPUT_FILE) )
        {
            inputFile = parsedOutput.getOptionValue("in");
        }
        if( parsedOutput.hasOption(OPT_FULL) )
        {
            printFullOutput = true;
        }
        if( parsedOutput.hasOption(OPT_PREVIOUS) )
        {
            previousDay = true;
        }

        return new AppOptions(inputFile, printFullOutput, previousDay);
    }

    public void printHelp()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(appCommand, OPTIONS);
    }

    private static Options createOptions()
    {
        Options options = new Options();
        options.addOption(Option.builder(OPT_INPUT_FILE)
                .hasArg()
                .required()
                .optionalArg(false)
                .argName("input_csv_file")
                .desc("input file (CSV, separated with ';')")
                .build());

        options.addOption(Option.builder(OPT_FULL)
                .desc("Print full output (input & rates, CSV separated by ';')")
                .build());

        options.addOption(Option.builder(OPT_PREVIOUS)
                .desc("Fetch exchange rates for the previous working date instead of the given day.")
                .build());

        return options;
    }
}
