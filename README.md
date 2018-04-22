# nbp-rates
Simple app to fetch official NBP rates of table A using the official API provided by NBP (Narodowy Bank Polski) (http://api.nbp.pl/api/exchangerates/rates/A).

Build instructions:
1. Pull repositoty using git or download the content
2. Run:
mvn clean compile assembly:single

Usage:
1) Create an input CSV file (separated with ";") containing requested date (in ISO format YYYY-MM-DD) and currency from table A, eg:

```
2016-06-02; USD
2016-06-05; EUR
2016-06-12; USD
```

2) Run the tool with selected options:

```
java -jar target/nbp-rates-1.0-SNAPSHOT-jar-with-dependencies.jar
 -f                     Print full output (input & rates, CSV separated by ';')
 -in <input_csv_file>   input file (CSV, separated with ';')
 -p                     Fetch exchange rates for the previous working day
                        instead of the given day.
```

__Note: Option ```-p``` is very important.__ If specified, it will fetch rates for the __previos__ day, instead of the requested date. Eg. If your date is Monday, you will get exchange rate from Friday! This is useful when you have a list of transactions made on specific dates, but need to fetch average exchange rates for the preceeding days.__

# Examples
Example to fetch rates for previous working date (with -p option).
Let's say, you have a transaction which occured on 20th of April 2018 (Friday). You need to fetch average exchange rates of USDPLN pair for a previous working day (Thursday 19th):

Your input.csv should contain only one line:
```
2018-04-19; USD
```
Command to run:
```
java -jar target/nbp-rates-1.0-SNAPSHOT-jar-with-dependencies.jar -in input.csv -f -p
```

Example output:
```
2018-04-19; USD; 3.3721
```

# Disclaimer
This is a free software which comes with an open source license and no warranty. It is still very likely to contain defects, and in such case the author is not in any way responsible for the incorrect results which might be provided by this software. It should not be used, for example, for the purpose of financial statement calculations or any other use cases in which the data provided by the software might cause financial or legal consequences to the user. It is here just for example purpose and you use it at your own risk. In case you need your data to be accurate, I advice you to seek other tools, or to always double-check the output of the program against the official NBP website. And if you find an issue with this software - please either fix it, or report it to the author.
