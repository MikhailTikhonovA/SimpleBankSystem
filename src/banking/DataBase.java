package banking;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class DataBase {
    private static Connection connect;
    private static boolean hasData = false;
    private String cardNumber;
    private StringBuilder pin;


    public void isExist(String name) throws SQLException, ClassNotFoundException {
        if (connect == null) {
            getConnection(name);
        } else {
            connect = DriverManager.getConnection("jdbc:sqlite:/Users/meshkovaalina/Documents/GitHub/Simple Banking System/Simple Banking System/task/" + name);
            Statement creatingTable = connect.createStatement();
            creatingTable.execute("CREATE TABLE card (id INTEGER," +
                    "number TEXT," +
                    "pin TEXT," +
                    "balance INTEGER DEFAULT 0);");
        }
    }

    public void getConnection(String name) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connect = DriverManager.getConnection("jdbc:sqlite:/Users/meshkovaalina/Documents/GitHub/Simple Banking System/Simple Banking System/task/" + name);
        initialize();
    }

    public void initialize() throws SQLException {
        if (!hasData) {
            hasData = true;
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("select name from sqlite_master where type = 'table' and name = 'card';");
            if (!resultSet.next()) {
                Statement creatingTable = connect.createStatement();
                creatingTable.execute("CREATE TABLE card (id INTEGER," +
                        "number TEXT," +
                        "pin TEXT," +
                        "balance INTEGER DEFAULT 0);");
            }
            resultSet.close();
        }
    }

    public void createAcc() throws SQLException {
        PreparedStatement creatingAcc = connect.prepareStatement("insert into card (id,number,pin)values(?,?,?);");
        int id = create_id();
        String num = createCard();
        String pin = createPin();
        creatingAcc.setInt(1, id);
        creatingAcc.setString(2, num);
        creatingAcc.setString(3, pin);
        creatingAcc.execute();
        System.out.println("Your card have been created");
        System.out.println("Your card number:");
        System.out.println(num);
        System.out.println("Your card PIN:");
        System.out.println(pin);

    }


    public boolean checkCardAndPin(String number, String pin) throws SQLException {
        String cardNum = "";
        String pinNum = "";
        boolean breakF = false;
        Statement cheking = connect.createStatement();
        ResultSet rs = cheking.executeQuery("select * from card where number = '" + number + "' and pin = '" + pin + "';");
        while (rs.next()) {
            cardNum = rs.getString("number");
            pinNum = rs.getString("pin");
        }
        if (cardNum.equals(number) && pinNum.equals(pin)) {
            System.out.println("You have successfully logged in!");
            while (true) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Balance");
                System.out.println("2. Add income");
                System.out.println("3. Do transfer");
                System.out.println("4. Close account");
                System.out.println("5. Log out");
                System.out.println("0. Exit");
                int sc = scanner.nextInt();
                if (sc == 1) {
                    getBalance(this.cardNumber);
                } else if (sc == 2) {
                    addBalance(this.cardNumber);
                } else if (sc == 3) {
                    transfer(this.cardNumber);
                } else if (sc == 4) {
                    deleteAcc(this.cardNumber);
                } else if (sc == 5) {
                    System.out.println();
                    System.out.println("You have successfully logged out!");
                    break;
                } else if (sc == 0) {
                    breakF = true;
                    break;
                }
            }
        } else {
            System.out.println();
            System.out.println("Wrong card number or PIN!");
        }
        return breakF;
    }

    public static void getBalance(String number) throws SQLException {
        Statement checking = connect.createStatement();
        ResultSet rs = checking.executeQuery("select balance from card where number = '" + number + "';");
        while (rs.next()) {
            int balance = rs.getInt("balance");
            System.out.println(balance);
        }
    }

    public static void addBalance(String number) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        Statement checking = connect.createStatement();
        System.out.println("Enter income:");
        String income = scanner.next();
        checking.execute("UPDATE card SET balance = balance+" + income + " where number = '" + number + "';");
        System.out.println("Income was added!");
    }

    public static void transfer(String number) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter card number:");
        String cardNumberFind = scanner.nextLine();
        if (!checkingLuhnAlg(cardNumberFind)) {
            System.out.println("Probably you made mistake in the card number. Please try again!");
        } else if(number.equals(cardNumberFind)){
            System.out.println("You can't transfer money to the same account!");
        } else{
            Statement checking = connect.createStatement();
            ResultSet resultSet = checking.executeQuery("select balance from card where number = " + cardNumberFind + " ;");
            if (resultSet.next()) {
                int currentBalance = resultSet.getInt(1);
                System.out.println("Enter how much money you want to transfer:");
                String transferSumm = scanner.next();
                if (Integer.parseInt(transferSumm) > currentBalance) {
                    System.out.println("Not enough money!");
                } else
                    checking.execute("UPDATE card SET balance = balance- " + transferSumm + " where number = '" + number + "';");
                checking.execute("UPDATE card SET balance = balance+ " + transferSumm + " where number = '" + cardNumberFind + "';");
                System.out.println("Success!");
            } else
                System.out.println("Such a card does not exist.");
        }
    }

    public static void deleteAcc(String cardNumber) throws SQLException {
        Statement checking = connect.createStatement();
        checking.execute("DELETE from card where number = " + cardNumber + " ;");
        System.out.println("The account has been closed!");
    }

    public static boolean checkingLuhnAlg(String cardNumber) {
        int summ = 0;
        String[] digits = cardNumber.split("");
        for (int i = 0; i < digits.length; i++) {
            if (i % 2 == 0) {
                if ((Integer.parseInt(digits[i]) * 2) > 9) {
                    summ -= 9;
                } else
                    summ += Integer.parseInt(digits[i]) * 2;
            } else {
                summ += Integer.parseInt(digits[i]);
            }
        }
        return summ % 2 == 0;
    }


    public String createCard() {
        int summ = 8;
        int last;
        int counter = 1;
        int buff = 0;
        StringBuilder cardNumber = new StringBuilder("400000");
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            int next = random.nextInt(9 + 1);
            if (counter % 2 != 0) {
                buff = next * 2;
                if (buff > 9) {
                    buff -= 9;
                }
                summ += buff;
            } else {
                summ += next;
            }
            cardNumber.append(next);
            counter++;
        }
        if (summ % 10 == 0) {
            last = 0;
        } else {
            last = 10 - (summ % 10);
        }
        cardNumber.append(last);
        this.cardNumber = cardNumber.toString();
        return cardNumber.toString();
    }


    public String createPin() {
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            Random random = new Random();
            int next = random.nextInt(9 + 1);
            pin.append(next);
        }
        this.pin = pin;
        return pin.toString();
    }

    public int create_id() {
        Random random = new Random();
        return random.nextInt(9 + 1);
    }

}
