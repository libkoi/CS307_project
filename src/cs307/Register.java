package cs307;


//import java.io.Console;
import java.sql.*;
import java.util.Scanner;

public class Register {
    Scanner in;
    Connection conn;

    public Register(Scanner in, Connection conn) {
        this.in = in;
        this.conn = conn;
    }

    public void main() {
        System.out.println("Please enter username:");
        String uName = null;
        this.conn = conn;

        while (true) {
            String uName1 = in.next();
            uName = uName1;
            if (checkExistance(uName)) {
                System.out.println("User already exist. Please re-enter.");
                continue;
            }
            break;
        }

        // Bug in IDEA -- works fine in cmd
        // https://stackoverflow.com/questions/4203646/system-console-returns-null
//        Console cons = System.console();
//        if (cons == null) {
//            System.out.print("No console available");
//            return;
//        }
//        char[] pwd = cons.readPassword("Please enter password: ");
//        String uPwd = pwd.toString();
        String uPwdE = null;
        while (true) {
            System.out.println("Please enter password: ");
            String uPwd1 = in.next();
            String uPwdE1 = Tool.encrypt(uPwd1);
            System.out.println("Please re-enter password: ");
            String uPwd2 = in.next();
            String uPwdE2 = Tool.encrypt(uPwd2);
            if (uPwdE2.equals(uPwdE1)) {
                uPwdE = uPwdE1;
                break;
            }
            System.out.println("Password do not match, please re-enter.");
        }
        register(uName, uPwdE);
        System.out.println("Registered. Enter 1 to login, 0 to return.");
        int k2 = 0;
        while (true) {
            int k21 = in.nextInt();
            if (k21 == 1 || k21 == 0) {
                k2 = k21;
                break;
            }
            System.out.println("Invalid input, please try again. ");
        }
        if (k2 == 1) {
            Login login = new Login(in, conn);
            login.main();
        } else return;
    }

    public int register(String uName, String uPwdE) {
        String sql = "INSERT INTO login_user(USER_NAME, USER_PASSWORD) VALUE (?,?)";
        PreparedStatement pstmt;
        int i = 0;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, uName);
            pstmt.setString(2, uPwdE);
            i = pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public boolean checkExistance(String uName) {
        String sql = "SELECT count(*) FROM login_user WHERE USER_NAME=?;";
        PreparedStatement pstmt;
        boolean res = false;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, uName);
            ResultSet rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                count = Integer.parseInt(rs.getString(1));
            }
            if (count == 1) res = true;
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


}
