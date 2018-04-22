package com.wiku;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ArgsParserTest
{
    private AppOptionsParser parser = new AppOptionsParser("java -jar test.jar");

    @Test
    public void canParseCorrectCLIArguments()
    {
        String[] args = {"-in", "input.txt", "-f", "-p"};

        Optional<AppOptions> options = parser.getOptions(args);

        assertEquals(new AppOptions("input.txt", true, true), options.get());
    }

    @Test
    public void canPrintHelpWhenMandatoryInputFileNotGiven()
    {
        String[] args = {"-f"};

        Optional<AppOptions> options = parser.getOptions(args);

        assertFalse(options.isPresent());
    }

    @Test
    public void whenFullOptionNotGivenshouldCreateOptionWithValueFalse()
    {
        String[] args = {"-in", "input.txt" };

        Optional<AppOptions> options = parser.getOptions(args);

        assertEquals(new AppOptions("input.txt", false, false), options.get());
    }

}
