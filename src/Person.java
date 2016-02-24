/**
 * Created by MacLap on 2/24/16.
 */
    public class Person implements Comparable{
        String id;
        String first_name;
        String last_name;
        String email;
        String country;
        String ip_address;



        public Person(String id, String first_name, String last_name, String email, String country, String ip_address) {
            this.id = id;
            this.first_name = first_name;
            this.last_name = last_name;
            this.email = email;
            this.country = country;
            this.ip_address = ip_address;
        }

        public String getId() {
            return id;
        }

        public String getFirst_name() {
            return first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public String getEmail() {
            return email;
        }

        public String getCountry() {
            return country;
        }

        public String getIp_address() {
            return ip_address;
        }

        @Override
        public int compareTo(Object o){
            Person e = (Person) o;
            return last_name.compareTo(e.last_name);
        }

        @Override
        public String toString() {
            return "Person{" +
                    "id = '" + id + '\'' +
                    ", '" + first_name + " " + last_name + '\'' +
                    ", email = '" + email + '\'' +
                    ", country = '" + country + '\'' +
                    ", ip_address = '" + ip_address + '\'' +
                    '}';
        }
    }


