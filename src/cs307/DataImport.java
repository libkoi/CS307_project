package cs307;

import java.io.*;
import java.sql.*;
import java.util.HashMap;


public class DataImport {
    public static void printPro(String stop, String start, long time1, long time2) {
        System.out.println("Table " + stop + " import finished. time cost: " + (time2 - time1)
                + " ms\nTable " + start + " starts importing");
    }

    static class FW {
        FileWriter fw;

        public FW() throws IOException {
            fw = new FileWriter("trainData.sql");
        }

        public void execute(String s) throws IOException {
            fw.write(s + "\n");
        }

        public void close() throws IOException {
            fw.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Connection conn = null;
        Statement stmt = null;
        //FW stmt=null;
        InputStreamReader read = new InputStreamReader(
                new FileInputStream("stations.csv"));// 考虑到编码格式
        BufferedReader in = new BufferedReader(read);

        //length of different files
        int stationLength = 672;
        int stationInfoLength = 3066;
        int trainNumberLength = 4458;
        int timetableLength = 34644;

        //Hash maps to store different infomation
        HashMap<String, Integer> stationNameToID = new HashMap<>();
        HashMap<String, Integer> trainNumberNameToID = new HashMap<>();
        HashMap<String, Integer> provinceToID = new HashMap<>();
        HashMap<String, Integer> operatingCompanyToID = new HashMap<>();
        HashMap<String, Integer> cityToID = new HashMap<>();
        HashMap<Integer, Boolean> trainN = new HashMap<>();
        HashMap<String, Boolean> station_train_unique = new HashMap<>();
        HashMap<String, Integer> stationNameToOperationID = new HashMap<>();

        try {
            conn = cs307.Connect.getConn();
            stmt = conn.createStatement();

            long time1 = System.currentTimeMillis();
            String insertStation = "INSERT INTO station (STATION_NAME, STATION_CODE, PINYIN, " +
                    "INITIAL_CODE, PINYIN_CODE, LONGTITUDE, LATITUDE) VALUES ";
            for (int i = 0; i < stationLength; i++) {
                String temp = in.readLine();
                String[] arr = temp.split(",");
                stationNameToID.put(arr[1], i + 1);
                insertStation += "(\'" + arr[1] + "\',\'" + arr[2] + "\',\'" + arr[3] + "\',\'"
                        + arr[4] + "\',\'" + arr[5] + "\',\'" + arr[6] + "\',\'" + arr[7] + "\'),";
            }
            insertStation = insertStation.substring(0, insertStation.length() - 1);
            insertStation += ";";
            stmt.execute(insertStation);
            long time2 = System.currentTimeMillis();
            printPro("stations", "stationInfo", time1, time2);
            read = new InputStreamReader(
                    new FileInputStream("stationInfo.csv"));
            in = new BufferedReader(read);

            int provinceID = 1;
            int operatingCompanyID = 1;
            int cityID = 1;
            for (int i = 0; i < stationInfoLength; i++) {
                String temp = in.readLine();
                String[] arr = temp.split(",");
                if (arr[5].equals("北京") || arr[5].equals("上海") || arr[5].equals("天津") || arr[5].equals("重庆"))
                    arr[6] = arr[5];
                if (!provinceToID.containsKey(arr[5])) {
                    provinceToID.put(arr[5], provinceID);
                    String sql = "INSERT INTO province (PROVINCE_ID,PROVINCE_NAME) VALUES " +
                            "(\'" + provinceID++ + "\',\'" + arr[5] + "\');";
                    stmt.execute(sql);
                }
                if (!cityToID.containsKey(arr[6])) {
                    cityToID.put(arr[6], cityID);
                    String sql = "INSERT INTO city (CITY_ID,CITY_NAME,PROVINCE_ID) VALUES " +
                            "(\'" + cityID++ + "\',\'" + arr[6] + "\',\'" + provinceToID.get(arr[5]) + "\');";
                    stmt.execute(sql);
                }
            }

            read = new InputStreamReader(
                    new FileInputStream("stationInfo.csv"));
            in = new BufferedReader(read);
            for (int i = 0; i < stationInfoLength; i++) {
                String temp = in.readLine();
                String[] arr = temp.split(",");
                if (!operatingCompanyToID.containsKey(arr[2])) {
                    int operatingCity = -1;
                    if (arr[2].equals("广铁集团"))
                        operatingCity = cityToID.get("广州");
                    else if (arr[2].equals("青藏铁路公司"))
                        operatingCity = cityToID.get("拉萨");
                    else
                        operatingCity = cityToID.get(arr[2].substring(0, arr[2].length() - 3));

                    operatingCompanyToID.put(arr[2], operatingCompanyID);
                    String sql = "INSERT INTO operating_company (COMPANY_ID,COMPANY_NAME,COMPANY_CITY_ID) VALUES " +
                            "(\'" + operatingCompanyID++ + "\',\'" + arr[2] + "\',\'" + operatingCity + "\');";
                    stmt.execute(sql);
                }
            }

            read = new InputStreamReader(
                    new FileInputStream("stationInfo.csv"));
            in = new BufferedReader(read);
            for (int i = 0; i < stationInfoLength; i++) {
                String temp = in.readLine();
                String[] arr = temp.split(",");
                if (arr[0].indexOf("站") != -1) {
                    arr[0] = arr[0].substring(0, arr[0].length() - 1);
                }
                //System.out.println(arr[0]+" "+<Station_name_without'站'>);
                int stationID = -1;
                if (stationNameToID.containsKey(arr[0]))
                    stationID = stationNameToID.get(arr[0]);
                //System.out.println(arr[0]+" stationid: "+stationID);
                int city = -1;
                if (cityToID.containsKey(arr[6]))
                    city = cityToID.get(arr[6]);
                if (arr[5].equals("北京") || arr[5].equals("上海") || arr[5].equals("天津") || arr[5].equals("重庆"))
                    city = cityToID.get(arr[5]);
                if (stationID != -1) {
                    String sql = "UPDATE station SET CITY_ID=" + city + ", OPERATING_COMPANY_ID="
                            + operatingCompanyToID.get(arr[2]) + " WHERE STATION_ID=" + stationID + ";";
                    stmt.execute(sql);
                    stationNameToOperationID.put(arr[0], operatingCompanyToID.get(arr[2]));
                }
            }

            long time3 = System.currentTimeMillis();
            printPro("stationInfo", "train_numbers", time2, time3);
            read = new InputStreamReader(
                    new FileInputStream("train_numbers.csv"));
            in = new BufferedReader(read);

            String insertTrainNumber = "INSERT INTO train_number (TRAIN_NUMBER_NAME,OPERATING_COMPANY_ID) VALUES ";
            for (int i = 0, j = 0; i < trainNumberLength; i++) {
                String temp = in.readLine();
                String[] arr = temp.split(",");
                if (!trainNumberNameToID.containsKey(arr[0])) {
                    insertTrainNumber += "(\'" + arr[0] + "\'," + stationNameToOperationID.get(arr[1]) + "),";
                    j++;
                }
                trainNumberNameToID.put(arr[0], j + 1);
            }
            insertTrainNumber = insertTrainNumber.substring(0, insertTrainNumber.length() - 1);
            insertTrainNumber += ";";
            stmt.execute(insertTrainNumber);

            long time4 = System.currentTimeMillis();
            printPro("train_numbers", "timetables", time3, time4);
            read = new InputStreamReader(
                    new FileInputStream("timetables.csv"));
            in = new BufferedReader(read);

            int trainNumberID = -1;
            String arr0 = "";
            String insertTimetable = "INSERT INTO _station_train_number (STATION_ID, TRAIN_NUMBER_ID, " +
                    "DUPLICATE_STATION_NUM, STATION_ORDER, ARRIVAL_TIME, DEPARTURE_TIME, DAY_NUM) VALUES ";
            HashMap<Integer, Integer> duplicate = new HashMap<>();
            int day = 0;
            int dup = 1;
            int t = 0;
            for (int i = 0; i < timetableLength; i++) {
                String temp = in.readLine();
                String[] arr = temp.split(",");
                if (!arr[0].equals("")) {
                    arr0 = arr[0];
                    duplicate.clear();
                    day = 0;
                    t = 0;
                }
                if (!trainNumberNameToID.containsKey(arr0))
                    continue;
                String[] time = arr[9].split(":");
                if (!arr[0].equals("")) {
                    //System.out.println(arr[0]+" "+trainNumberNameToID.get(arr[0]));
                    trainN.put(trainNumberID, true);
                    trainNumberID = trainNumberNameToID.get(arr[0]);
                    time = arr[10].split(":");
                }
                if (duplicate.containsKey(stationNameToID.get(arr[8]))) {
                    int v = duplicate.get(stationNameToID.get(arr[8]));
                    dup = v + 1;
                    duplicate.replace(stationNameToID.get(arr[8]), v + 1);
                } else {
                    dup = 1;
                    duplicate.put(stationNameToID.get(arr[8]), 1);
                }
                int tt = Integer.parseInt(time[0] + time[1]);
                if (tt < t) {
                    day++;
                    //System.out.printf("t:%d; tt:%d\n",t,tt);
                }
                t = tt;
                if (trainN.containsKey(trainNumberID))
                    continue;
                insertTimetable += "(\'" + stationNameToID.get(arr[8]) + "\',\'" + (trainNumberID - 1) +
                        "\',\'" + dup + "\',\'" + arr[7] + "\'," +
                        (arr[9].equals("----") ? "null" : ("\'" + arr[9] + "\'")) + "," +
                        (arr[10].equals(arr[9]) ? "null" : ("\'" + arr[10] + "\'")) + "," + day + "),";

            }
            insertTimetable = insertTimetable.substring(0, insertTimetable.length() - 1);
            insertTimetable += ";";
            stmt.execute(insertTimetable);
            long time5 = System.currentTimeMillis();
            printPro("timetables", "null", time4, time5);

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
}