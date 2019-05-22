package cs307;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Meal {
    Scanner in = null;
    Connection conn = null;
    String uName = null;

    public Meal(Scanner in, Connection conn, String uName) {
        this.in = in;
        this.conn = conn;
        this.uName = uName;
    }

    public void view_meal() {
        String sql = "select * from combo_menu order by COMBO_ID;";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            int col = rs.getMetaData().getColumnCount();
            int size = 0;
            try {
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
            } catch (SQLException ex) {
            }
            System.out.println(size + " records fetched.");
            if (size != 0) {
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
                        if (i == 2) {
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

    public int diningOrderPlus(String purchasedOrderID) {
        String sql = "insert into dining_order (user_id,purchased_order_id)\n" +
                "select\n" +
                "  (select USER_ID from login_user where USER_NAME = ?) as user_id,\n" +
                "  ? as purchased_order_id;";
        PreparedStatement pstmt;
        int i = 0;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, uName);
            pstmt.setString(2, purchasedOrderID);
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }


    public int diningOrderMinus(String diningOrder) {
        String sql = "delete from dining_order where dining_order_id = ?;";
        PreparedStatement pstmt;
        int i = 0;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, diningOrder);
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }


    public int diningRelationPlus(String purchasedOrderID, String comboID) {
        String sql = "insert into _dining_order_combo (dining_order_id, combo_id)\n" +
                "values ((select dining_order_id\n" +
                "         from dining_order\n" +
                "                join login_user lu on dining_order.USER_ID = lu.USER_ID\n" +
                "         where lu.USER_NAME = ?\n" +
                "           and purchased_order_id = ?\n" +
                "         limit 1), ?);";
        PreparedStatement pstmt;
        int i = 0;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, uName);
            pstmt.setString(2, purchasedOrderID);
            pstmt.setString(3, comboID);

            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }


    public int diningRelationMinus(String diningOrder) {
        String sql = "delete from _dining_order_combo where dining_order_id = ?;";
        PreparedStatement pstmt;
        int i = 0;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, diningOrder);
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }


    public void view_order() {
        String sql = "select do.DINING_ORDER_ID, cm.COMBO_NAME, do.PURCHASED_ORDER_ID, p.PASSENGER_NAME\n" +
                "from dining_order do\n" +
                "       join login_user lu on do.USER_ID = lu.USER_ID\n" +
                "       join _dining_order_combo doc on do.DINING_ORDER_ID = doc.DINING_ORDER_ID\n" +
                "       join combo_menu cm on doc.COMBO_ID = cm.COMBO_ID\n" +
                "       join purchased_order po on do.PURCHASED_ORDER_ID = po.PURCHASED_ORDER_ID\n" +
                "       join passenger p on po.PASSENGER_ID = p.PASSENGER_ID\n" +
                "where lu.USER_NAME = ?;";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, uName);

            ResultSet rs = pstmt.executeQuery();

            int col = rs.getMetaData().getColumnCount();
            int size = 0;
            try {
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
            } catch (SQLException ex) {
            }
            System.out.println(size + " records fetched.");

            if (size != 0) {
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
                        if (i == 2 || i == 4) {
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


    public void loop(int k) {
        if (k == 1) view_meal();
        if (k == 2) {
            String purchasedOrderID;
            String comboID;
            System.out.println("Enter purchase order. This could get via \"Book\" menu.");
            purchasedOrderID = in.next();
            System.out.println("Enter combo ID. This could get via \"View_meal\".");
            comboID = in.next();
            diningOrderPlus(purchasedOrderID);
            diningRelationPlus(purchasedOrderID, comboID);
            System.out.println("Ordered. ");
        }
        if (k == 3) {
            System.out.println("The following meal order are booked by you, " + uName + ". ");
            view_order();
        }
        if (k == 4) {
            String diningOrder;
            System.out.println("Enter dining order. This could get via \"View_order\".");
            diningOrder = in.next();
            diningRelationMinus(diningOrder);
            diningOrderMinus(diningOrder);
            System.out.println("Order cancelled. ");

        }
    }

    public void main() {
        while (true) {
            System.out.println("View_meal: 1 | Order: 2 | View_order: 3 | Cancel: 4 | Return: 0");
            int k = in.nextInt();
            if (k == 1 || k == 2 || k == 3 | k == 4 || k == 0) {
                if (k == 0) return;
                loop(k);
                continue;
            }
            System.out.println("Invalid input, please try again. ");
        }
    }


}
