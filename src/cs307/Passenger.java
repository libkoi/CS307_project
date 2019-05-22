package cs307;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Passenger {
    Scanner in = null;
    Connection conn = null;

    public Passenger(Scanner in, Connection conn) {
        this.in = in;
        this.conn = conn;
    }


    public int insertPassenger(String passengerName, String certID) {
        String sql = "insert into passenger(PASSENGER_NAME, PASSENGER_CERTIFICATE_NUMBER) value (?,?);";
        PreparedStatement pstmt;
        int i = 0;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, passengerName);
            pstmt.setString(2, certID);
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    //exist -> true
    public boolean checkExist(String passengerName, String certID) {
        String sql = "SELECT count(*) FROM passenger WHERE PASSENGER_NAME = ?" +
                " and PASSENGER_CERTIFICATE_NUMBER = ?;";
        PreparedStatement pstmt;
        boolean res = false;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, passengerName);
            pstmt.setString(2, certID);
            ResultSet rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                count = Integer.parseInt(rs.getString(1));
            }
            if (count == 1) res = true;
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    public void listPassenger(String passengerName) {
        String sql = "select PASSENGER_NAME, PASSENGER_CERTIFICATE_NUMBER from passenger " +
                "where PASSENGER_NAME = ?;";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, passengerName);
            ResultSet rs = pstmt.executeQuery();


            int col = rs.getMetaData().getColumnCount();
            rs.beforeFirst();

            int size = 0;
            try {
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
            } catch (SQLException ex) {
            }
            if (size == 0) System.out.println("No passenger matched.");
            else {
                int[] attrMaxLength = new int[col];

                while (rs.next()) {
                    for (int i = 1; i <= col; i++) {
                        int length = rs.getString(i).length();
                        if (length > attrMaxLength[i - 1]) attrMaxLength[i - 1] = length;
                    }
                }
                rs.beforeFirst();

                System.out.println("============================================================");
                while (rs.next()) {
                    for (int i = 1; i <= col; i++) {
                        String str = rs.getString(i);
                        System.out.print(str);
                        if (i == 1) {
                            for (int j = 0; j < (attrMaxLength[i - 1] - str.length()); j++) System.out.print("  ");
                            System.out.print("    ");
                        } else
                            for (int j = 0; j < (attrMaxLength[i - 1] - str.length()) + 4; j++) System.out.print(" ");
                    }
                    System.out.println();
                }
                System.out.println("============================================================");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int deletePassenger(String passengerName, String certID) {
        String sql = "delete from passenger where PASSENGER_NAME = ? and PASSENGER_CERTIFICATE_NUMBER = ?;";
        PreparedStatement pstmt;
        int i = 0;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, passengerName);
            pstmt.setString(2, certID);
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public void main() {
        while (true) {
            System.out.println("Add passenger: 1 | View passenger: 2");
            System.out.println("Delete passenger: 3| Return: 0");
            int i = in.nextInt();
            if (i == 1 || i == 2 || i == 3 || i == 0) {
                if (i == 0) return;
                if (i == 1) {
                    String passengerName;
                    String certID;
                    System.out.println("Enter passenger name: ");
                    passengerName = in.next();
                    System.out.println("Enter certificate number(i.e. ID number): ");
                    certID = in.next();
                    if (checkExist(passengerName, certID)) System.out.println("Passenger already exist.");
                    else insertPassenger(passengerName, certID);
                    continue;
                }
                if (i == 2) {
                    String passengerName;
                    System.out.println("Enter passenger name: ");
                    passengerName = in.next();
                    listPassenger(passengerName);
                    continue;
                }
                if (i == 3) {
                    String passengerName;
                    String certID;
                    System.out.println("Enter passenger name: ");
                    passengerName = in.next();
                    System.out.println("Enter certificate number(i.e. ID number): ");
                    certID = in.next();
                    if (!checkExist(passengerName, certID)) System.out.println("Passenger do not exist.");
                    else deletePassenger(passengerName, certID);
                    continue;
                }
            }
            System.out.println("Invalid input, please try again. ");
        }
    }
}
