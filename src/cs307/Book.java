package cs307;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Book {
    Scanner in = null;
    Connection conn = null;
    String uName = null;

    public Book(Scanner in, Connection conn, String uName) {
        this.in = in;
        this.conn = conn;
        this.uName = uName;
    }

    // Return ticket_id if ticket_left > 0, else return -1
    public int checkRemain(String trainNumber, String departureStation,
                           String arrivalStation, Date date) {
        int res = 0;
        String sql = "select ticket_id, station_ticket_left\n" +
                "from train_number t_n\n" +
                "       join specific_train_number sp on sp.train_number_id = t_n.train_number_id\n" +
                "       join train_calendar t_c on t_c.train_calendar_id = sp.train_calendar_id\n" +
                "       join ticket ti\n" +
                "            on ti.specific_train_number_id = sp.specific_train_number_id\n" +
                "where t_n.train_number_name = ?\n" +
                "  and DATE(calendar) = ?\n" +
                "  and station_from_id = (select station_id from station where station_name = ?)\n" +
                "  and station_to_id =\n" +
                "      (select station_id from station where station_name = ?);";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, trainNumber);

            try {
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                pstmt.setDate(2, sqlDate);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            pstmt.setString(3, departureStation);
            pstmt.setString(4, arrivalStation);

            ResultSet rs = pstmt.executeQuery();

            int col = rs.getMetaData().getColumnCount();
            int size = 0;
            try {
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
            } catch (SQLException ex) {
            }

            if (size == 0) res = -1;
            else {
                int[] rsArr = new int[2];
                while (rs.next()) {
                    for (int i = 1; i <= col; i++) {
                        String str = rs.getString(i);
                        int rsInt = Integer.parseInt(str);
                        rsArr[i - 1] = rsInt;
                    }
                }
                if (rsArr[rsArr.length - 1] == 0) res = -1;
                else res = rsArr[0];
            }
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    public int ticketMinus(String ticketID) {
        String sql = "update ticket set station_ticket_left = (station_ticket_left - 1)" +
                " where ticket_id= ? and station_ticket_left > 0 ;";
        PreparedStatement pstmt;
        int i = 0;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, ticketID);
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }


    public int ticketPlus(String purchasedOrderID) {
        String sql = "update ticket\n" +
                "set station_ticket_left=station_ticket_left + 1\n" +
                "where ticket_id = (select ticket_id from purchased_order " +
                "where purchased_order_id = ?);";
        PreparedStatement pstmt;
        int i = 0;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, purchasedOrderID);
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }


    public int purchasePlus(String uName, String passenger,
                            String passengerCertID, String ticketID) {
        String sql = "insert into purchased_order(USER_ID, PASSENGER_ID, TICKET_ID)\n" +
                "     select (select USER_ID from login_user where USER_NAME = ?) as USER_ID,\n" +
                "            (select PASSENGER_ID\n" +
                "             from passenger\n" +
                "             where PASSENGER_NAME = ?\n" +
                "               and PASSENGER_CERTIFICATE_NUMBER = ?)             as PASSENGER_ID,\n" +
                "            ?                                                    as TICKET_ID;";
        PreparedStatement pstmt;
        int i = 0;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, uName);
            pstmt.setString(2, passenger);
            pstmt.setString(3, passengerCertID);
            pstmt.setString(4, ticketID);
            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;

    }

    public int purchaseMinus(String purchaseOrderID) {
        String sql = "delete from purchased_order where purchased_order_id = ?;\n";
        PreparedStatement pstmt;
        int i = 0;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, purchaseOrderID);

            i = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;

    }


    public void viewBooked() {
        String sql = "select po.PURCHASED_ORDER_ID, tn.TRAIN_NUMBER_NAME, s1.STATION_NAME, s2.STATION_NAME, " +
                "       tc.CALENDAR, p.PASSENGER_NAME\n" +
                "from purchased_order po\n" +
                "       join ticket t on po.TICKET_ID = t.TICKET_ID\n" +
                "       join login_user lu on po.USER_ID = lu.USER_ID\n" +
                "       join passenger p on po.PASSENGER_ID = p.PASSENGER_ID\n" +
                "       join specific_train_number stn on t.SPECIFIC_TRAIN_NUMBER_ID = stn.SPECIFIC_TRAIN_NUMBER_ID\n" +
                "       join train_calendar tc on stn.TRAIN_CALENDAR_ID = tc.TRAIN_CALENDAR_ID\n" +
                "       join train_number tn on stn.TRAIN_NUMBER_ID = tn.TRAIN_NUMBER_ID\n" +
                "       join station s1 on t.STATION_FROM_ID = s1.STATION_ID\n" +
                "       join station s2 on t.STATION_TO_ID = s2.STATION_ID\n" +
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
                        if (i == 3 || i == 4) {
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


    public void viewChangeableTrain(String purchaseOrderID) {
        // get Start,End station, Date --> Specific Query
        String sql = "select s1.STATION_NAME, s2.STATION_NAME, tc.CALENDAR\n" +
                "from purchased_order po\n" +
                "       join ticket t on po.TICKET_ID = t.TICKET_ID\n" +
                "       join login_user lu on po.USER_ID = lu.USER_ID\n" +
                "       join passenger p on po.PASSENGER_ID = p.PASSENGER_ID\n" +
                "       join specific_train_number stn on t.SPECIFIC_TRAIN_NUMBER_ID = stn.SPECIFIC_TRAIN_NUMBER_ID\n" +
                "       join train_calendar tc on stn.TRAIN_CALENDAR_ID = tc.TRAIN_CALENDAR_ID\n" +
                "       join train_number tn on stn.TRAIN_NUMBER_ID = tn.TRAIN_NUMBER_ID\n" +
                "       join station s1 on t.STATION_FROM_ID = s1.STATION_ID\n" +
                "       join station s2 on t.STATION_TO_ID = s2.STATION_ID\n" +
                "where po.PURCHASED_ORDER_ID = ? limit 1;";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, purchaseOrderID);
            ResultSet rs = pstmt.executeQuery();

            int col = rs.getMetaData().getColumnCount();
            String departure = null;
            String arrival = null;
            String dateString = null;

            int size = 0;
            try {
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
            } catch (SQLException ex) {
            }

            if (size == 0) System.out.println("Purchase order ID not found. ");
            else {
                while (rs.next()) {
                    departure = rs.getString(1);
                    arrival = rs.getString(2);
                    dateString = rs.getString(3);
                }
                rs.close();
                pstmt.close();


                Date dateAtCalender = null;

                try {
                    dateAtCalender = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Query qu = new Query(in, conn);
                qu.specific_query(departure, arrival, dateAtCalender);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }


    }

    public void changeTrain(String purchaseOrderID, String trainNumber) {
        String sql = "select s1.STATION_NAME, s2.STATION_NAME, tc.CALENDAR, " +
                "       p.PASSENGER_NAME, p.PASSENGER_CERTIFICATE_NUMBER\n" +
                "from purchased_order po\n" +
                "       join ticket t on po.TICKET_ID = t.TICKET_ID\n" +
                "       join login_user lu on po.USER_ID = lu.USER_ID\n" +
                "       join passenger p on po.PASSENGER_ID = p.PASSENGER_ID\n" +
                "       join specific_train_number stn on t.SPECIFIC_TRAIN_NUMBER_ID = stn.SPECIFIC_TRAIN_NUMBER_ID\n" +
                "       join train_calendar tc on stn.TRAIN_CALENDAR_ID = tc.TRAIN_CALENDAR_ID\n" +
                "       join train_number tn on stn.TRAIN_NUMBER_ID = tn.TRAIN_NUMBER_ID\n" +
                "       join station s1 on t.STATION_FROM_ID = s1.STATION_ID\n" +
                "       join station s2 on t.STATION_TO_ID = s2.STATION_ID\n" +
                "where po.PURCHASED_ORDER_ID = ? limit 1;";
        PreparedStatement pstmt;


        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, purchaseOrderID);
            ResultSet rs = pstmt.executeQuery();

            int col = rs.getMetaData().getColumnCount();


            int size = 0;
            try {
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
            } catch (SQLException ex) {
            }

            if (size == 0) System.out.println("Purchase order ID not found. ");
            else {
                String departure = null;
                String arrival = null;
                String dateString = null;
                String passengerName = null;
                String passengerCertID = null;
                Date dateAtCalender = null;


                while (rs.next()) {
                    departure = rs.getString(1);
                    arrival = rs.getString(2);
                    dateString = rs.getString(3);
                    passengerName = rs.getString(4);
                    passengerCertID = rs.getString(5);
                }

                try {
                    dateAtCalender = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                rs.close();
                pstmt.close();

                if (!trainNumberinQuery(departure, arrival, dateAtCalender, trainNumber))
                    System.out.println("Invalid input train number. ");
                else {
                    //TODO

                    int remainCount = checkRemain(trainNumber, departure, arrival, dateAtCalender);
                    if (remainCount != -1) {

                        ticketPlus(purchaseOrderID);
                        purchaseMinus(purchaseOrderID);

                        String ticketID = Integer.toString(remainCount);
                        ticketMinus(ticketID);
                        purchasePlus(uName, passengerName, passengerCertID, ticketID);
                        System.out.println("Ticket changed successfully. ");
                    } else System.out.println("No remain ticket available. ");

                }

            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }


    public boolean trainNumberinQuery(String departure, String destination, Date date, String trainNumber) {
        boolean res = false;
        String sql = "select count(*)\n" +
                "from (\n" +
                "      (select STATION_NAME,\n" +
                "              station_to_id,\n" +
                "              t_n.TRAIN_NUMBER_ID,\n" +
                "              TRAIN_NUMBER_NAME,\n" +
                "              DEPARTURE_TIME,\n" +
                "              s_t_n.day_num,\n" +
                "              station_ticket_left\n" +
                "       from station st\n" +
                "              join _station_train_number s_t_n on s_t_n.station_id = st.station_id\n" +
                "              join train_number t_n on t_n.train_number_id = s_t_n.train_number_id\n" +
                "              join specific_train_number sp on sp.train_number_id = t_n.train_number_id\n" +
                "              join train_calendar t_c on t_c.train_calendar_id = sp.train_calendar_id\n" +
                "              join ticket ti\n" +
                "                   on ti.specific_train_number_id = sp.specific_train_number_id and ti.STATION_FROM_ID = st.STATION_ID\n" +
                "       where station_name = ?\n" +
                "         and DATE(calendar) = ?) f1\n" +
                "       join\n" +
                "     (select STATION_NAME, st.STATION_ID, t_n.TRAIN_NUMBER_ID, ARRIVAL_TIME, s_t_n.day_num\n" +
                "      from station st\n" +
                "             join _station_train_number s_t_n on s_t_n.station_id = st.station_id\n" +
                "             join train_number t_n on t_n.train_number_id = s_t_n.train_number_id\n" +
                "             join specific_train_number sp on sp.train_number_id = t_n.train_number_id\n" +
                "             join train_calendar t_c on t_c.train_calendar_id = sp.train_calendar_id\n" +
                "      where station_name = ?\n" +
                "        and DATE(calendar) = ?\n" +
                "     ) f2\n" +
                "     on f2.train_number_id = f1.train_number_id and f2.station_id = f1.station_to_id)\n" +
                "where TIME(f1.departure_time) > TIME(?)\n" +
                "  and TRAIN_NUMBER_NAME = ?\n" +
                "order by f1.departure_time, f2.ARRIVAL_TIME desc;";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, departure);

            try {
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                pstmt.setDate(2, sqlDate);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            pstmt.setString(3, destination);

            try {
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                pstmt.setDate(4, sqlDate);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String currentTimeString = Tool.getCurrentTime();
            Date currentTime = null;
            try {
                currentTime = new SimpleDateFormat("HH:mm").parse(currentTimeString);
                java.sql.Time sqlTime = new java.sql.Time(currentTime.getTime());
                pstmt.setTime(5, sqlTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            pstmt.setString(6, trainNumber);

            //System.out.println(pstmt.toString());

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


    public void loop(int k) {
        if (k == 1) {
            String passengerName;
            String passengerCertID;
            String trainNumber;
            String departureStation;
            String arrivalStation;
            String dateString;
            System.out.println("Enter passenger name: ");
            passengerName = in.next();
            System.out.println("Enter passenger certificate number(i.e. ID number): ");
            passengerCertID = in.next();
            System.out.println("Enter train number: ");
            trainNumber = in.next();
            System.out.println("Enter departure station: ");
            departureStation = in.next();
            System.out.println("Enter arrival station: ");
            arrivalStation = in.next();
            System.out.println("Enter departure date(yyyy-MM-dd): ");
            dateString = in.next();
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int remainCount = checkRemain(trainNumber, departureStation, arrivalStation, date);
            if (remainCount != -1) {
                String ticketID = Integer.toString(remainCount);
                ticketMinus(ticketID);
                purchasePlus(uName, passengerName, passengerCertID, ticketID);
                System.out.println("Ticket book successfully. ");
            } else System.out.println("No remain ticket available. ");

        }
        if (k == 2) {
            System.out.println("The following ticket are booked by you, " + uName + ". ");
            viewBooked();
        }
        if (k == 3) {
            String purchaseOrderID;
            System.out.println("Enter purchase order. This could get via \"View_book_ticket\".");
            purchaseOrderID = in.next();
            ticketPlus(purchaseOrderID);
            purchaseMinus(purchaseOrderID);
            System.out.println("Ticket cancelled.");
        }
        if (k == 4) {
            String purchaseOrderID;
            System.out.println("Enter purchase order. This could get via \"View_book_ticket\".");
            purchaseOrderID = in.next();
            viewChangeableTrain(purchaseOrderID);
        }
        if (k == 5) {
            String purchaseOrderID;
            String trainNumber;
            System.out.println("Enter purchase order. This could get via \"View_book_ticket\".");
            purchaseOrderID = in.next();
            System.out.println("Enter intend train number. This could get via \"View_changeable_train\".");
            trainNumber = in.next();
            changeTrain(purchaseOrderID, trainNumber);
        }


    }

    public void main() {

        while (true) {
            System.out.println("Book_new_ticket: 1 | View_booked_ticket: 2 | Cancel: 3");
            System.out.println("View_changeable_train: 4 | Change_train: 5 | Return: 0");
            int k = in.nextInt();
            if (k == 1 || k == 2 || k == 3 | k == 4 || k == 5 || k == 0) {
                if (k == 0) return;
                loop(k);
                continue;
            }
            System.out.println("Invalid input, please try again. ");
        }

    }
}
