package cs307;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
//import java.io.Console;


public class ChangePassword {
    Scanner in = null;
    Connection conn = null;

    public ChangePassword(Scanner in, Connection conn) {
        this.in = in;
        this.conn = conn;
    }


    public void main(String uName) {
        String uoldPwd;
        String unewPwd;
        // Bug in IDEA -- works fine in cmd
        // https://stackoverflow.com/questions/4203646/system-console-returns-null
//        Console cons = System.console();
//        if (cons == null) {
//            System.out.print("No console available");
//            return;
//        }
//
//        String uOldPwdE;
//        while (true) {
//            char[] oldPwd = cons.readPassword("Enter old Password: ");
//            String uOldPwd = oldPwd.toString();
//            String uOldPwdE1 = Tool.encrypt(uOldPwd);
//            if (checkPass(uName, uOldPwdE1)) {
//                uOldPwdE = uOldPwdE1;
//                break;
//            }
//            System.out.println("Wrong password. Please re-enter.");
//        }
//        String uNewPwdE1; // ** new Password should be here
//        while (true) {
//            char[] newPwd1 = cons.readPassword("Enter new Password: ");
//            String uNewPwd1 = newPwd1.toString();
//            String uNewPwd1E = Tool.encrypt(uNewPwd1);
//            if (!uOldPwdE.equals(uNewPwd1E)) {
//                uNewPwdE1 = uNewPwd1E;
//                break;
//            }
//            System.out.println("New password could NOT equal old password. Please re-enter.");
//        }
//        while (true) {
//            char[] newPwd2 = cons.readPassword("Re-Enter new Password: ");
//            String uNewPwd2 = newPwd2.toString();
//            String uNewPwd2E = Tool.encrypt(uNewPwd2);
//            if (uNewPwd2E.equals(uNewPwdE1)) break;
//            System.out.println("Password do not match, please re-enter.");
//        }

        String uOldPwdE;
        while (true) {
            System.out.println("Enter old Password: ");
            String uOldPwd = in.next();
            String uOldPwdE1 = Tool.encrypt(uOldPwd);
            if (checkPass(uName, uOldPwdE1)) {
                uOldPwdE = uOldPwdE1;
                break;
            }
            System.out.println("Wrong password. Please re-enter.");
        }
        String uNewPwdE1; // ** new Password should be here
        while (true) {
            System.out.println("Enter new Password: ");
            String uNewPwd1 = in.next();
            String uNewPwd1E = Tool.encrypt(uNewPwd1);
            if (!uOldPwdE.equals(uNewPwd1E)) {
                uNewPwdE1 = uNewPwd1E;
                break;
            }
            System.out.println("New password could NOT equal old password. Please re-enter.");
        }
        while (true) {
            System.out.println("Re-Enter new Password: ");
            String uNewPwd2 = in.next();
            String uNewPwd2E = Tool.encrypt(uNewPwd2);
            if (uNewPwd2E.equals(uNewPwdE1)) break;
            System.out.println("Password do not match, please re-enter.");
        }


        changePass(uName, uNewPwdE1);
        System.out.println("Password changed.");
        return;
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
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    public int changePass(String uName, String uNewPwdE) {
        String sql = "UPDATE login_user SET USER_PASSWORD = ? WHERE USER_NAME = ?;";
        PreparedStatement pstmt;
        int i = 0;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, uNewPwdE);
            pstmt.setString(2, uName);
            i = pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

}
