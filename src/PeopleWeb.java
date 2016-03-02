

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PeopleWeb {

        public static void createTables (Connection conn) throws SQLException {
            Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE IF EXISTS people");
            stmt.execute("CREATE TABLE people (id IDENTITY, first_name VARCHAR, last_name VARCHAR, email VARCHAR, country VARCHAR, ip_address VARCHAR)");
        }

    public static void insertPerson (Connection conn, String firstName, String lastName, String email, String country, String ipAddress) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO people VALUES (NULL, ?, ?, ?, ?, ?)");

        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, email);
        stmt.setString(4, country);
        stmt.setString(5, ipAddress);
        stmt.execute();
    }

    public static Person selectPerson(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String firstName = results.getString("first_name");
            String lastName = results.getString("last_name");
            String email = results.getString("email");
            String country = results.getString("country");
            String ipAddress = results.getString("ip_address");
            String idStr = String.valueOf(id);
            Person person = new Person(idStr, firstName, lastName, email, country, ipAddress);

            return person;
        }

        return null;
    }

    public static void populateDatabase(Connection conn) throws FileNotFoundException, SQLException {
        File f = new File("people.csv");
        Scanner fileScanner = new Scanner(f);


        int i = 0;
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            if (i != 0) {
                String[] column = line.split(",");
                insertPerson(conn, column[1], column[2], column[3], column[4], column[5]);
            }
            i ++;
        }
    }

    public static ArrayList<Person> selectPeople(Connection conn, int offset) throws SQLException {
        ArrayList<Person> people = new ArrayList<>();

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people LIMIT 20 OFFSET ?");
        stmt.setInt(1, offset);
        stmt.execute();

        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            String idStr = String.valueOf(results.getInt("id"));
            String firstName = results.getString("first_name");
            String lastName = results.getString("last_name");
            String email = results.getString("email");
            String country = results.getString("country");
            String ipAddress = results.getString("ip_address");
            Person person = new Person(idStr, firstName, lastName, email, country, ipAddress);

            people.add(person);
        }
        return people;
    }

    public static void main(String[] args) throws FileNotFoundException, SQLException {
        //ArrayList<Person> people = readFile();

        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);
        populateDatabase(conn);



        Spark.init();
        Spark.get(
                "/",
                ((request, response) -> {



                    HashMap m = new HashMap();
                    //ArrayList<Person> peopleDisplayed = new ArrayList<>();
                    int minDisplay = 1;
                    int maxDisplay = 20;
                    boolean displayNext;
                    boolean displayPrev;
                    int offsetUp = 20;
                    int offsetDown = -20;

                    String offsetString = request.queryParams("offset");
                    int start = 0;
                    if (offsetString != null){
                        int offset = Integer.valueOf(offsetString);
                        start = start + offset;
                        offsetUp = offset + offsetUp;
                        offsetDown = offset + offsetDown;
                        ArrayList<Person> people = selectPeople(conn, offset);
                    }

                    ArrayList<Person> people = selectPeople(conn, start);

                    maxDisplay = start + maxDisplay;
                    minDisplay = start + minDisplay;

//                    for (Person person : people){
//                        if(Integer.valueOf(person.id) <= maxDisplay && Integer.valueOf(person.id) >= minDisplay ) {
//                            peopleDisplayed.add(person);
//                        }
//                    }

                    if (minDisplay > 1) displayPrev = true;
                    else displayPrev = false;
                    if (maxDisplay < 1000) displayNext = true;
                    else displayNext = false;

                    m.put("offsetUp", offsetUp);
                    m.put("offsetDown", offsetDown);
                    m.put("displayPrev", displayPrev);
                    m.put("displayNext", displayNext);
                    m.put("peopleDisplayed", people);

                    return new ModelAndView(m, "home.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/person",
                ((request, response) -> {
                HashMap m = new HashMap();
                    int personId = Integer.valueOf(request.queryParams("id"));
                    Person dude = selectPerson(conn, personId);

                    m.put("dude", dude);

                    return new ModelAndView(m, "person.html");
                }),
                new MustacheTemplateEngine()
        );
    }

    public static ArrayList<Person> readFile() throws FileNotFoundException {
        ArrayList<Person> people = new ArrayList<>();
        File f = new File("people.csv");
        Scanner fileScanner = new Scanner(f);


        int i = 0;
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            if (i != 0) {
                String[] column = line.split(",");
                Person p = new Person(column[0], column[1], column[2], column[3], column[4], column[5]);
                people.add(p);
            }
            i ++;
        }
        return people;
    }
}
