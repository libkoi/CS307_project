package cs307;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Query {
    Scanner in = null;
    Connection conn = null;

    public Query(Scanner in, Connection conn) {
        this.in = in;
        this.conn = conn;
    }

    public void fuzzy_query(String departure, String destination, Date date) {
        String sql = "select f1.train_number_name,\n" +
                "       f1.STATION_NAME,\n" +
                "       f1.departure_time,\n" +
                "       f2.STATION_NAME,\n" +
                "       f2.arrival_time,\n" +
                "       case when (time(f2.ARRIVAL_TIME) < time(f1.DEPARTURE_TIME)) then '1' else '0' end as day_cross,\n" +
                "       f1.station_ticket_left\n" +
                "from (\n" +
                "      (select STATION_NAME, station_to_id, t_n.TRAIN_NUMBER_ID, TRAIN_NUMBER_NAME, DEPARTURE_TIME, \n" +
                "           station_ticket_left\n" +
                "       from city ci\n" +
                "              join station st on st.city_id = ci.city_id\n" +
                "              join _station_train_number s_t_n on s_t_n.station_id = st.station_id\n" +
                "              join train_number t_n on t_n.train_number_id = s_t_n.train_number_id\n" +
                "              join specific_train_number sp on sp.train_number_id = t_n.train_number_id\n" +
                "              join train_calendar t_c on t_c.train_calendar_id = sp.train_calendar_id\n" +
                "              join ticket ti\n" +
                "                   on ti.specific_train_number_id = sp.specific_train_number_id and\n" +
                "                       ti.STATION_FROM_ID = st.STATION_ID\n" +
                "       where city_name = ?\n" +
                "         and DATE(calendar) = ?) f1\n" +
                "       join\n" +
                "     (select STATION_NAME, st.STATION_ID, t_n.TRAIN_NUMBER_ID, ARRIVAL_TIME\n" +
                "      from city ci\n" +
                "             join station st on st.city_id = ci.city_id\n" +
                "             join _station_train_number s_t_n on s_t_n.station_id = st.station_id\n" +
                "             join train_number t_n on t_n.train_number_id = s_t_n.train_number_id\n" +
                "             join specific_train_number sp on sp.train_number_id = t_n.train_number_id\n" +
                "             join train_calendar t_c on t_c.train_calendar_id = sp.train_calendar_id\n" +
                "      where city_name = ?\n" +
                "        and DATE(calendar) = ?\n" +
                "     ) f2\n" +
                "     on f2.train_number_id = f1.train_number_id and f2.station_id = f1.station_to_id)\n" +
                "where TIME(f1.departure_time) > TIME(?)\n" +
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

            // System.out.println(pstmt.toString());

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


    public void specific_query(String departure, String destination, Date date) {
        String sql = "select f1.train_number_name,\n" +
                "       f1.STATION_NAME,\n" +
                "       f1.departure_time,\n" +
                "       f2.STATION_NAME,\n" +
                "       f2.arrival_time,\n" +
                "         f2.day_num-f1.day_num as day_cross,\n" +
                "       f1.station_ticket_left\n" +
                "from (\n" +
                "      (select STATION_NAME, station_to_id, t_n.TRAIN_NUMBER_ID, TRAIN_NUMBER_NAME, " +
                "           DEPARTURE_TIME,s_t_n.day_num, station_ticket_left\n" +
                "       from  station st\n" +
                "              join _station_train_number s_t_n on s_t_n.station_id = st.station_id\n" +
                "              join train_number t_n on t_n.train_number_id = s_t_n.train_number_id\n" +
                "              join specific_train_number sp on sp.train_number_id = t_n.train_number_id\n" +
                "              join train_calendar t_c on t_c.train_calendar_id = sp.train_calendar_id\n" +
                "              join ticket ti\n" +
                "                   on ti.specific_train_number_id = sp.specific_train_number_id " +
                "                       and ti.STATION_FROM_ID = st.STATION_ID\n" +
                "       where station_name = ?\n" +
                "         and DATE(calendar) = ?) f1\n" +
                "       join\n" +
                "     (select STATION_NAME, st.STATION_ID, t_n.TRAIN_NUMBER_ID, ARRIVAL_TIME,s_t_n.day_num\n" +
                "      from  station st\n" +
                "             join _station_train_number s_t_n on s_t_n.station_id = st.station_id\n" +
                "             join train_number t_n on t_n.train_number_id = s_t_n.train_number_id\n" +
                "             join specific_train_number sp on sp.train_number_id = t_n.train_number_id\n" +
                "             join train_calendar t_c on t_c.train_calendar_id = sp.train_calendar_id\n" +
                "      where station_name = ?\n" +
                "        and DATE(calendar) = ?\n" +
                "     ) f2\n" +
                "     on f2.train_number_id = f1.train_number_id and f2.station_id = f1.station_to_id)\n" +
                "where TIME(f1.departure_time) > TIME(?)\n" +
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

            //System.out.println(pstmt.toString());

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


    public void train_number(String train_number) {
        String sql = "select station_name, arrival_time, departure_time\n" +
                "from station st\n" +
                "       join _station_train_number s_t_n on s_t_n.station_id = st.station_id\n" +
                "       join train_number t_n on t_n.train_number_id = s_t_n.train_number_id\n" +
                "where train_number_name = ?\n" +
                "order by station_order;";

        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, train_number);

            // System.out.println(pstmt.toString());

            ResultSet rs = pstmt.executeQuery();

            int col = rs.getMetaData().getColumnCount();

            rs.beforeFirst();
            int[] attrMaxLength = new int[col];

            int size = 0;
            try {
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
            } catch (SQLException ex) {
            }

            if (size == 0) {
                System.out.println("Not found. ");
            } else {


                while (rs.next()) {
                    for (int i = 1; i <= col; i++) {
                        String str = rs.getString(i);
                        int length = 0;
                        if (str == null && i != 1) length = 6;
                        else length = rs.getString(i).length();
                        if (length > attrMaxLength[i - 1]) attrMaxLength[i - 1] = length;
                    }
                }
                rs.beforeFirst();

                System.out.println("============================================================");
                while (rs.next()) {
                    for (int i = 1; i <= col; i++) {
                        String str = rs.getString(i);
                        if (str == null) System.out.print(" <null> ");
                        else System.out.print(str);
                        if (i == 1) {
                            for (int j = 0; j < (attrMaxLength[0] - str.length()); j++)
                                System.out.print("  ");
                            System.out.print("    ");
                        } else
                            for (int j = 0; j < (attrMaxLength[i - 1] - ((str == null) ? 8 : str.length())) + 4; j++)
                                System.out.print(" ");
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


    public void main() {
        String departure;
        String destination;
        String date;
        while (true) {
            System.out.println("Fuzzy_Query: 1 | Specific_Query: 2 | Line_Query: 3 | Return: 0");
            int i = in.nextInt();
            if (i == 1 || i == 2 || i == 3 || i == 0) {
                if (i == 0) return;
                if (i == 1) {
                    System.out.println("Enter departure city: ");
                    departure = in.next();
                    System.out.println("Enter destination city: ");
                    destination = in.next();
                    System.out.println("Enter departure date(yyyy-MM-dd): ");
                    date = in.next();
                    Date dateAtCalender = null;

                    try {
                        dateAtCalender = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    fuzzy_query(departure, destination, dateAtCalender);
                    continue;
                }
                if (i == 2) {
                    System.out.println("Enter departure station: ");
                    departure = in.next();
                    System.out.println("Enter destination station: ");
                    destination = in.next();
                    System.out.println("Enter departure date(yyyy-MM-dd): ");
                    date = in.next();
                    Date dateAtCalender = null;

                    try {
                        dateAtCalender = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    specific_query(departure, destination, dateAtCalender);
                    continue;
                }
                if (i == 3) {
                    System.out.println("Enter train number(i.e. G2): ");
                    String trainNumber = in.next();

                    train_number(trainNumber);
                    continue;
                }
            }

            System.out.println("Invalid input, please try again. ");

        }

    }
}
