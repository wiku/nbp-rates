package com.wiku;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ArgsParserTest
{
    private AppOptionsParser parser = new AppOptionsParser("java -jar test.jar");

    @Test
    public void canParseCorrectCLIArguments() throws ParseException
    {
        String[] args = {"-in", "input.txt", "-f", "-p", "-v"};

        AppOptions options = parser.getOptions(args);

        assertEquals(new AppOptions("input.txt", true, true, true), options);
    }

    @Test(expected = ParseException.class)
    public void throwsExceptionWhenMandatoryOptionNotGiven() throws ParseException
    {
        String[] args = {"-f"};

        AppOptions options = parser.getOptions(args);
    }

    @Test
    public void whenFullOptionNotGivenshouldCreateOptionWithValueFalse() throws ParseException
    {
        String[] args = {"-in", "input.txt" };

        AppOptions options = parser.getOptions(args);

        assertEquals(new AppOptions("input.txt", false, false, false), options);
    }

}
