import db.DatabaseUnmarshaller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainTest {
    public static void main(String argc[]) {
        String url = "jdbc:mysql://localhost:3306/Test_DATABASE?useSSL=false&characterEncoding=UTF-8";
        String user = "Annie";
        String password = "Password1";
        try {
            Class.forName("com.mysql.jdbc.Driver");

            try {
                Connection connection = DriverManager.getConnection(url, user, password);
                DatabaseUnmarshaller um = new DatabaseUnmarshaller();
                Test test = new Test();
                test.field = 99;
                TestTwo tt = new TestTwo();
                tt.nameStr = "coolName";
                tt.value = 1984;
                test.testTwoList.add(tt);

                //  test.testTwo.notInDBStr="wwwwwww";
                System.out.println(test.field);
                um.unmarshall(test, "testName", connection);
                System.out.println("jj");
                System.out.println(test.testTwoList.get(0).nameStr);
                System.out.println(test.testTwoList.get(0).value);
                // System.out.println(test.testTwo.notInDBStr);
                connection.close();
            } catch (SQLException ex) {
                System.out.println("SQL EXEPTION");
                ex.printStackTrace();

            } catch (IllegalAccessException ex) {
                System.out.println("IllegalAccessException EXEPTION");
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException EXEPTION");

        } catch (InstantiationException ex) {
            System.out.println("ClassNotFoundException EXEPTION");

        }

    }
}
