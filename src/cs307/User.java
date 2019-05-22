package cs307;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    String uName;
    String uPwdE;
    Scanner in;
    Connection conn;

    public User(String uName, String uPwdE, Scanner in, Connection conn) {
        this.uName = uName;
        this.uPwdE = uPwdE;
        this.in = in;
        this.conn = conn;
    }


    public void changePwd(String newPwdE) {

    }

    public void loop(int k) {
        if (k == 0) System.exit(0);
        if (k == 1) {
            Query query = new Query(in, conn);
            query.main();
        }
        if (k == 2) {
            Passenger ps = new Passenger(in, conn);
            ps.main();
        }
        if (k == 3) {
            Book book = new Book(in, conn, uName);
            book.main();
        }
        if (k == 4) {
            Meal meal = new Meal(in, conn, uName);
            meal.main();
        }
        if (k == 5) {
            ChangePassword changePass = new ChangePassword(in, conn);
            changePass.main(uName);
        }
    }

    public void main() {
        System.out.println("You have login as " + uName + ".");


        while (true) {
            System.out.println("Query:1 | Passenger: 2 | Book:3 | Meal:4 | Change_password:5 | Exit: 0");
            int k = in.nextInt();
            if (k == 1 || k == 2 || k == 3 || k == 4 || k == 5 || k == 0) {
                if (k == 0) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
                loop(k);
                continue;
            }
            System.out.println("Invalid input, please try again. ");
        }


    }
}