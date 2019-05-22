package cs307;

import java.util.Scanner;
import java.sql.*;

public class Launch {

    public static void launch(Scanner in, Connection conn) {
        System.out.println("Login:1 | Register:2 | Exit: 0");
        while (true) {
            int k = in.nextInt();
            if (k == 1 || k == 2 | k == 0) {
                if (k == 0) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (k == 1) {
                    Login login = new Login(in, conn);
                    login.main();
                    continue;
                }
                if (k == 2) {
                    Register register = new Register(in, conn);
                    register.main();
                    continue;
                }
            }
            System.out.println("Invalid input, please try again. ");
        }

    }


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Connection conn = Connect.getConn();
        launch(in, conn);
    }
}
