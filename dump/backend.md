## FAQ

* **`java.sql.SQLException: The server time zone value 'XXX' is unrecognized or represents more than one time zone. `**

  * *Add `serverTimezone=UTC` after `url`.*

* **`WARN: Establishing SSL connection without server's identity verification is not recommended.`**

  * *Add `useSSL=false` after `url` to dismiss this warning.*
  * *Notice, this is not recommend -- but as database is running at localhost, it's fine.*

* **I need to insert a row with a column value type is `datetime`. How?**

  * *We have `PreparedStatement pstmt;`*

  * *We assume an object `student` which has a String value `birthday`, and the **fourth** place is remained for this value.*

  * ```java
      try {
          java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(student.getSbirthday());
          java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
          pstmt.setDate(4, sqlDate);
      } catch (ParseException e) {
          e.printStackTrace();
      }
    ```
  * *https://stackoverflow.com/questions/21872040/jdbc-inserting-date-values-into-mysql*


* **I want to hide input when typing a password. How?**
* *`java.io.Console` has a method called `readPassword()`. https://stackoverflow.com/questions/10819469/hide-input-on-command-line*
  * *Known bug for IDEA: https://stackoverflow.com/questions/4203646/system-console-returns-null*
* **`Exception in thread "main" java.sql.SQLException: Before start of result set`**
  * *Initial pointer is before the first line, so please use `resultSet.next()`.* 
* **`Public key retrieval is not allowed`**

  * *Update your JDBC driver.*
