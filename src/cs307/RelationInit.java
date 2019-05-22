package cs307;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


public class RelationInit {
    public static void relationInit(String date) {

        Connection conn = null;
        Statement stmt = null;
        //FW stmt=null;

        try {
            conn = cs307.Connect.getConn();
            stmt = conn.createStatement();

            //train_calendar
            String insert_cal = "INSERT INTO train_calendar (CALENDAR) VALUES (\'" + date + "\');";
            stmt.execute(insert_cal);
            String selectCalID = "select TRAIN_CALENDAR_ID from train_calendar where CALENDAR=\'" + date + "\';";
            ResultSet calid = stmt.executeQuery(selectCalID);
            calid.next();
            int calID = calid.getInt("TRAIN_CALENDAR_ID");
            String selectTN = "select TRAIN_NUMBER_ID from train_number;";
            ResultSet trainNumberID = stmt.executeQuery(selectTN);
            ArrayList<Integer> trainNumberIDs = new ArrayList<>();
            while (trainNumberID.next())
                trainNumberIDs.add(trainNumberID.getInt("TRAIN_NUMBER_ID"));
            long[] times = new long[100];
            int[] timess = new int[100];
            for (int i = 0; i < trainNumberIDs.size(); i++) {
                int ID = trainNumberIDs.get(i);
                String insertSpecificTrainNumber = "insert into specific_train_number " +
                        "(TRAIN_NUMBER_ID, TRAIN_CALENDAR_ID) values" + "(" + ID + "," + calID + ");";
                stmt.execute(insertSpecificTrainNumber);
                String selectSpecificTrainNumberID = "select SPECIFIC_TRAIN_NUMBER_ID " +
                        "from specific_train_number where TRAIN_CALENDAR_ID=" +
                        calID + " and TRAIN_NUMBER_ID=" + ID + ";";
                ResultSet specificTrainNumberIDrs = stmt.executeQuery(selectSpecificTrainNumberID);
                specificTrainNumberIDrs.next();
                int specificTrainNumberID = specificTrainNumberIDrs.getInt("SPECIFIC_TRAIN_NUMBER_ID");
                String selectTimetable = "select STATION_ID from _station_train_number WHERE TRAIN_NUMBER_ID="
                        + ID + " order by STATION_ORDER;";
                ResultSet timetablers = stmt.executeQuery(selectTimetable);
                ArrayList<Integer> timetable = new ArrayList<>();
                while (timetablers.next())
                    timetable.add(timetablers.getInt("STATION_ID"));
                int n = timetable.size();
                if (n == 0 || n == 1) continue;
                System.out.println(ID + " " + n);
                int ticketNum = 1500 / (n * (n - 1) / 2);
                long t1 = System.currentTimeMillis();
                String insertTimetable = "insert into ticket (specific_train_number_id, station_from_id, " +
                        "station_to_id, station_ticket_left) VALUES";
                for (int j = 0; j < timetable.size(); j++) {
                    for (int k = j + 1; k < timetable.size(); k++) {
                        insertTimetable += " (" + specificTrainNumberID + "," + timetable.get(j) + "," +
                                timetable.get(k) + "," + ticketNum + "),";
                        //stmt.execute(sql);
                    }
                }
                insertTimetable = insertTimetable.substring(0, insertTimetable.length() - 1);
                insertTimetable += ";";
                //System.out.println(insertTimetable);
                stmt.execute(insertTimetable);
                long t2 = System.currentTimeMillis();
                times[n] += t2 - t1;
                timess[n]++;
            }
            long alltime = 0;
            for (int i = 0; i < 100; i++) {
                if (timess[i] == 0)
                    continue;
                System.out.printf("size: %d, all time: %d, n: %d, per time: %d\n",
                        i, times[i], timess[i], times[i] / timess[i]);
                alltime += times[i];
            }
            System.out.println("all time of all size: " + alltime);
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter date to init(yyyy-MM-dd): ");
        String str = in.next();
        relationInit(str);
    }
}