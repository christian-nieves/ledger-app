package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class FinancialTracker {

    private static final ArrayList<Transaction> transactions = new ArrayList<>(); // This is the array list for all the transactions
    private static final String FILE_NAME = "transactions.csv"; // csv file store in string

    private static final String DATE_PATTERN = "yyyy-MM-dd"; // date pattern
    private static final String TIME_PATTERN = "HH:mm:ss"; // time pattern
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN; // date + time pattern

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN); // date formatter to convert what the user input to the format
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN); // time formatter to convert what the user input to the format
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN); // date + time formatter to convert what the user inputs to the format

    public static void main(String[] args) {
        loadTransactions(FILE_NAME); // calls loadTransactions method

        Scanner scanner = new Scanner(System.in); // creates new scanner to receive user input
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp"); // home screen that displays options for user to choose from
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim(); // receives user input for what they want to do next

            switch (input.toUpperCase()) { // switch case that does the next step depending on what the user entered
                case "D" -> addDeposit(scanner); // if the user types "D" it will run the addDeposit method
                case "P" -> addPayment(scanner); // if the user types "P" it will run the addPayment method
                case "L" -> ledgerMenu(scanner); // if the user types "L" it will run the ledgerMenu method
                case "X" -> running = false; // if the user types "X" it will close the application
                default -> System.out.println("Invalid option"); // if the user types something other than "D", "P". "L", or "X" it will print invalid option
            }
        }
        scanner.close();
    }

    public static void loadTransactions(String fileName) {

        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName)); // creates a buffered reader to read the transactions.csv file
            while ((line = bufferedReader.readLine()) != null) { // while loop that reads each line until it is null/empty
                String[] parts = line.split("\\|"); // splits line whenever it reads "|"
                LocalDate date = LocalDate.parse(parts[0]); // stores each piece into an array
                LocalTime time = LocalTime.parse(parts[1]);
                String description = parts[2];
                String vendor = parts[3];
                double amount = Double.parseDouble(parts[4]);
                transactions.add(new Transaction(date, time, description, vendor, amount)); // takes each part of the array and creates a new transaction (adds it to array list)
            }
            bufferedReader.close(); // closes buffered reader

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage()); // prints error message in red if something goes wrong
        }
    }

    private static void addDeposit(Scanner scanner) {
        System.out.println("Enter the date and time (yyyy-MM-dd HH:mm:ss): "); // asks user for date and time
        String dateTime = scanner.nextLine().trim(); // stores date and trim and trims extra space

        System.out.println("Enter the description: "); // asks user for description
        String description = scanner.nextLine().trim(); // stores description and trims extra space

        System.out.println("Enter the vendor: "); // asks user for the vendor
        String vendor = scanner.nextLine().trim(); // stores vendor name and trims extra space

        System.out.println("Enter the amount: "); // asks user for the amount
        double amount = Double.parseDouble(scanner.nextLine().trim()); // stores amount and trims extra space

        if (amount <= 0) { // if statement that doesn't allow user to enter an amount that is under 0
            System.out.println("Amount must be positive. Please try again."); // prints out message to inform the user
            return; // returns back to the screen
        }

        String[] dateTimeParts = dateTime.split(" "); // turns dateTime into an array and splits it when there is a space
        LocalDate date = LocalDate.parse(dateTimeParts[0], DATE_FMT); // stores date
        LocalTime time = LocalTime.parse(dateTimeParts[1], TIME_FMT); // stores time

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("transactions.csv", true)); // creates a buffered writer and file writer
            bufferedWriter.write(date + "|" + time + "|" + description + "|" + vendor + "|" + amount);                // to write the new transaction in the csv file
            bufferedWriter.newLine();
            bufferedWriter.close();
            transactions.add(new Transaction(date, time, description, vendor, amount)); // adds new transaction that was wrote in the csv file to the transactions array list
            System.out.println("Deposit Added!");

        } catch (Exception e) {
            System.err.println("There was an error adding your deposit. Please try again later."); // prints error message in red if something goes wrong
        }
    }

    private static void addPayment(Scanner scanner) {
        System.out.println("Enter the date and time (yyyy-MM-dd HH:mm:ss): "); // addPayment is very similar to addDeposit but there are minor differences
        String dateTime = scanner.nextLine().trim();

        System.out.println("Enter the description: ");
        String description = scanner.nextLine().trim();

        System.out.println("Enter the vendor: ");
        String vendor = scanner.nextLine().trim();

        System.out.println("Enter the amount: ");
        double amount = Double.parseDouble(scanner.nextLine().trim());

        if (amount <= 0) {
            System.out.println("Amount must be entered as a positive number. Please try again."); // tells user to enter amount as a positive number
            return;
        }

        double negativeAmount = amount * -1; // this converts the amount that the user entered from a positive number to a negative number

        String[] dateTimeParts = dateTime.split(" ");
        LocalDate date = LocalDate.parse(dateTimeParts[0], DATE_FMT);
        LocalTime time = LocalTime.parse(dateTimeParts[1], TIME_FMT);

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("transactions.csv", true));
            bufferedWriter.write(date + "|" + time + "|" + description + "|" + vendor + "|" + negativeAmount); // instead of amount it is now negative amount
            bufferedWriter.newLine();
            bufferedWriter.close();
            transactions.add(new Transaction(date, time, description, vendor, negativeAmount)); // adds the same transaction that was wrote in csv file to the transactions array list
            System.out.println("Payment Successful!");

        } catch (Exception e) {
            System.err.println("There was an error adding your payment. Please try again later."); // prints error message in red if something goes wrong
        }
    }

    private static void ledgerMenu(Scanner scanner) {
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime).reversed()); // sorts transactions from latest to oldest
        boolean running = true;                                                                                       // starts off by comparing the date, and then it compares the time
                                                                                                                      // reverses list since default is oldest to latest
        while (running) {
            System.out.println("Ledger"); // ledger menu with options on what the user wants to select
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim(); // takes user input and trims any extra space

            switch (input.toUpperCase()) {  // switch case that does the next step depending on what the user entered
                case "A" -> displayLedger(); // runs displayLedger method if the user entered "A"
                case "D" -> displayDeposits(); // runs displayDeposits method if the user entered "D"
                case "P" -> displayPayments(); // runs displayPayments method if the user entered "P"
                case "R" -> reportsMenu(scanner); // runs reportsMenu method if the user entered "R"
                case "H" -> running = false; // goes back to the home page if the user enters "H"
                default -> System.out.println("Invalid option"); // if the user types something other than "A", "D". "P", "R", or "H" it will print invalid option
            }
        }
    }

    private static void displayLedger() {
        System.out.println("All transactions: ");
        for (Transaction transaction : transactions) { // for each loop that goes through every transaction and prints it
            System.out.println(transaction);
        }
    }

    private static void displayDeposits() {
        System.out.println("All deposits: ");
        for (Transaction transaction : transactions) { // for each loop that goes through every transaction and only prints transactions that have a positive amount
            if (transaction.getAmount() > 0) { // if statement that prevents any negative amount to print
                System.out.println(transaction);
            }
        }
    }

    private static void displayPayments() {
        System.out.println("All payments: ");
        for (Transaction transaction : transactions) { // for each loop that goes through every transaction and only prints transactions that have a negative amount
            if (transaction.getAmount() < 0) { // if statement that prevents any positive amount to print
                System.out.println(transaction);
            }
        }
    }

    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports"); // reports menu giving the user options to select what report the user wants to see
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim(); // takes user input and trims any extra space

            switch (input) { // switch case that does the next step depending on what the user entered
                case "1" -> { // shows all transactions from the first of the month to the current date
                    LocalDate start = LocalDate.now().withDayOfMonth(1);
                    LocalDate end = LocalDate.now();
                    filterTransactionsByDate(start, end);
                }
                case "2" -> { // shows all transactions from the previous month
                    LocalDate firstOfThisMonth = LocalDate.now().withDayOfMonth(1); // creates variable for first of this month
                    LocalDate start = firstOfThisMonth.minusMonths(1); // goes from first of this month to first of last month
                    LocalDate end = firstOfThisMonth.minusDays(1);
                    filterTransactionsByDate(start, end);
                }
                case "3" -> { // shows all transactions from the first of this year to the current date
                    LocalDate start = LocalDate.now().withDayOfYear(1); // starts at first of year
                    LocalDate end = LocalDate.now(); // ends at current date
                    filterTransactionsByDate(start, end);
                }
                case "4" -> { // shows all transactions from the previous year
                    LocalDate firstOfThisYear = LocalDate.now().withDayOfYear(1); // creates variable for first day of this year
                    LocalDate start = firstOfThisYear.minusYears(1); // uses minusYears to go to first day of previous year
                    LocalDate end = firstOfThisYear.minusDays(1); // uses minusDays to go to last day of previous year
                    filterTransactionsByDate(start, end);
                }
                case "5" -> { // user searches for transactions by vendor
                    try {
                        System.out.println("Search by vendor: ");
                        String vendor = scanner.nextLine();
                        filterTransactionsByVendor(vendor); // calls method and only shows transactions that match what the user entered

                        } catch (Exception e) {
                        System.err.println("This vendor does not exist. Please try again.");
                    }                     // prints an message in red text telling the user that the vendor doesn't exist
                }
                case "6" -> customSearch(scanner); // calls customSearch method

                case "0" -> running = false; // goes back if user types "0"
                default -> System.out.println("Invalid option"); // if user inputs an invalid option it will display this message
            }
        }
    }

    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        // filters transactions by date using for to go through every transaction and if to select which transaction qualifies the needs
        for (Transaction transaction : transactions) { // uses ! - before and ! - after to make sure everything within the boundaries shows
            if (!transaction.getDate().isBefore(start) && !transaction.getDate().isAfter(end)) {
                System.out.println(transaction);
            }
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        // filters transactions by vendor using a scanner to receive what the user inputted and checking if it matches with an existing vendor
        for (Transaction transaction : transactions) { // goes through each transaction
            if (transaction.getVendor().equalsIgnoreCase(vendor)) { // checks each transaction if it equals to user input
                System.out.println(transaction);
            }
        }
    }

    private static void customSearch(Scanner scanner) {
        System.out.println("Enter the start date (yyyy-MM-dd), or press Enter to skip: "); // custom search method that gives the user freedom to skip a line if they want to
        String startDate = scanner.nextLine().trim();

        System.out.println("Enter the end date (yyyy-MM-dd), or press Enter to skip: ");
        String endDate = scanner.nextLine().trim();

        System.out.println("Enter the description, or press Enter to skip: ");
        String description = scanner.nextLine().trim();

        System.out.println("Enter the vendor, or press Enter to skip: ");
        String vendor = scanner.nextLine().trim();

        System.out.println("Enter the amount, or press Enter to skip: ");
        String amount = scanner.nextLine().trim();

        try {
            for (Transaction transaction : transactions) { // for each loop that goes through every transaction

                boolean run = true; // run true instead of false so that it only changes something if it's false

                if (!startDate.isBlank() && transaction.getDate().isBefore(LocalDate.parse(startDate))) {
                    run = false;
                }
                if (!endDate.isBlank() && transaction.getDate().isAfter(LocalDate.parse(endDate))) {
                    run = false;
                }
                if (!description.isBlank() && !transaction.getDescription().equalsIgnoreCase(description)) {
                    run = false;
                }
                if (!vendor.isBlank() && !transaction.getVendor().equalsIgnoreCase(vendor)) {
                    run = false;
                }
                if (!amount.isBlank() && Math.abs(transaction.getAmount()) != Double.parseDouble(amount)) { // uses math.abs to show both deposits and payments
                    run = false;
                }

                if (run) { // this runs the custom search and prints out the transactions based off of the filters you applied
                    System.out.println(transaction);
                }
            }
        } catch (Exception e) {
            System.err.println("The input you entered is incorrect. Please try again."); // message in red alerting user that the input is incorrect
        }
    }

    private static LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s.trim(), DATE_FMT);
        } catch (Exception e) {
            return null;
        }
    }

    private static Double parseDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
