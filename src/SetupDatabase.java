public class SetupDatabase {

    public static void main(String[] args) {
        Postgres.initialize();
        Postgres.setupDatabase();
        System.out.println("Successfully intiialized database.");
    }
}
