import connection.ServerFacade;
import ui.MainUI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MainUI ui = new MainUI(scanner, new ServerFacade());
        ui.run();
    }
}