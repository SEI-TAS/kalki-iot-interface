public class ResetDatabase {

    public static void main(String[] args) {
        Postgres.initialize();
        Postgres.resetDatabase();
        System.out.println("Successfully cleared tables in database.");
    }
}
