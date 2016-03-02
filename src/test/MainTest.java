import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by MacLap on 3/2/16.
 */
public class MainTest {

    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        PeopleWeb.createTables(conn);
        return conn;
    }

    public void endConnection(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE people");
        conn.close();
    }

    @Test
    public void testPerson() throws SQLException {
        Connection conn = startConnection();
        PeopleWeb.insertPerson(conn, "Alice", "Smith", "", "", "");
        Person person = PeopleWeb.selectPerson(conn, 1);
        endConnection(conn);

        assertTrue(person != null);
    }

    @Test
    public void testPeople() throws SQLException, FileNotFoundException {
        Connection conn = startConnection();
        PeopleWeb.populateDatabase(conn);
        ArrayList<Person> people = PeopleWeb.selectPeople(conn, 20);
        endConnection(conn);

        assertTrue(people != null);
    }
}
