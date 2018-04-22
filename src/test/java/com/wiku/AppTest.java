package com.wiku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class AppTest
{

    public static final List<String> EXPECTED_FILE_CONTENT = Arrays.asList(
            "2016-06-02;USD;3.9350",
            "2016-06-02;USD;3.9350",
            "2016-06-05;USD;3.9384",
            "2016-06-12;USD;3.8545",
            "2016-06-16;USD;3.9351",
            "2016-05-06;USD;3.8475");
    private static final String INPUT_FILE = "src/test/resources/input.txt";

    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test public void shouldFetchRatesForEachDateInFile() throws IOException
    {
        File tempFile = folder.newFile();
        PrintStream out = createPrintStreamToFile(tempFile);

        App.printRatesForFile(new AppOptions(INPUT_FILE, true, true), out);

        assertThatFileLinesEquals(tempFile, EXPECTED_FILE_CONTENT);
    }

    @Ignore // previous test version for demo purpose
    @Test public void shouldFetchRatesForEachDateInFileToSout() throws IOException
    {
        App.printRatesForFile(new AppOptions(INPUT_FILE, false, true), System.out);
    }

    private PrintStream createPrintStreamToFile( File tempFile ) throws FileNotFoundException
    {
        return new PrintStream(new FileOutputStream(tempFile));
    }

    private void assertThatFileLinesEquals( File file, List<String> expectedFileContent ) throws IOException
    {
        List<String> linesRead = Files.readAllLines(file.toPath());
        assertEquals(expectedFileContent, linesRead);
    }
}
