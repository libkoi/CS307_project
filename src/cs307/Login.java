package cs307;

//import java.io.Console;
import java.sql.*;
import java.util.Scanner;


public class Login {
    Scanner in;
    Connection conn;

    public Login(Scanner in, Connection conn) {
        this.in = in;
        this.conn = conn;
    }

    public void main() {
        System.out.println("Please enter username:");
        String uName = in.next();
        this.conn = conn;
        if (!checkExistance(uName)) {
            System.out.println("User do NOT exist, Please register first.");
            return;
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

        System.out.println("Please enter password: ");
        int tryCount = 0;
        String uPwdE = null;
        while (true) {
            String uPwd1 = in.next();
            String uPwdE1 = Tool.encrypt(uPwd1);
            uPwdE = uPwdE1;
            if (!checkPass(uName, uPwdE1)) {
                System.out.println("Wrong password. Please re-enter.");
                tryCount++;
                continue;
            }
            if (tryCount == 5) {
                System.out.println("Too many attempts.\n");
                return;
            }
            if (checkPass(uName, uPwdE1)) break;
        }

        User usr = new User(uName, uPwdE, in, conn);
        usr.main();

    }


    public boolean checkExistance(String uName) {
        String sql = "SELECT count(*) FROM login_user WHERE USER_NAME=?;";
        PreparedStatement pstmt;
        boolean res = false;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, uName); // AutoFill
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


    public boolean checkPass(String uName, String uPwdE) {
        String sql = "SELECT USER_PASSWORD FROM login_user WHERE USER_NAME=?;";
        PreparedStatement pstmt;
        boolean res = false;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, uName); // AutoFill
            ResultSet rs = pstmt.executeQuery();
            String getPass = null;
            while (rs.next()) {
                getPass = rs.getString(1);
            }
            if (getPass.equals(uPwdE)) res = true;
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


}
