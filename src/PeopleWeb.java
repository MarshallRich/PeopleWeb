import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PeopleWeb {
    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<Person> people = readFile();



        Spark.init();
        Spark.get(
                "/",
                ((request, response) -> {



                    HashMap m = new HashMap();
                    ArrayList<Person> peopleDisplayed = new ArrayList<>();
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
                    }

                    maxDisplay = start + maxDisplay;
                    minDisplay = start + minDisplay;

                    for (Person person : people){
                        if(Integer.valueOf(person.id) <= maxDisplay && Integer.valueOf(person.id) >= minDisplay ) {
                            peopleDisplayed.add(person);
                        }
                    }

                    if (minDisplay > 1) displayPrev = true;
                    else displayPrev = false;
                    if (maxDisplay < 1000) displayNext = true;
                    else displayNext = false;

                    m.put("offsetUp", offsetUp);
                    m.put("offsetDown", offsetDown);
                    m.put("displayPrev", displayPrev);
                    m.put("displayNext", displayNext);
                    m.put("peopleDisplayed", peopleDisplayed);

                    return new ModelAndView(m, "home.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/person",
                ((request, response) -> {
                HashMap m = new HashMap();
                    int personId = Integer.valueOf(request.queryParams("id"));
                    Person dude = people.get(personId-1);

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
