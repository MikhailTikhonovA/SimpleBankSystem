package banking;

import java.io.File;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    private static String file;
    private static File db;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DataBase dataBaseF = new DataBase();
        if (args[0].equals("-fileName")) {
            file = args[1];
            db = new File(args[1]);
            dataBaseF.isExist(file);
        }
        StartMenu startMenu = new StartMenu();

        while (true) {
            startMenu.printMenu();
            int firstChoice = scanner.nextInt();
            if (firstChoice == 1) {
                dataBaseF.createAcc();
            } else if (firstChoice == 2) {
                if(startMenu.printAuthorizationMenu(dataBaseF)){
                    break;
                }
            } else if (firstChoice == 0) {
                break;
            }
        }
        System.out.println("Bye!");
    }

}

class StartMenu {

    public void printMenu() {
        System.out.println("1. Create account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }


    public boolean printAuthorizationMenu(DataBase dataBase) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your card number:");
        String numberCard = scanner.next();
        System.out.println("Enter your PIN:");
        String numberPin = scanner.next();
        if(dataBase.checkCardAndPin(numberCard, numberPin)){
            return true;
        }
        return false;
    }
}





