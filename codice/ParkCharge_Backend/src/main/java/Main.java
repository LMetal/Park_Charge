public class Main {
    public static void main(String[] args) {
        RestAPI api = new RestAPI();
        api.start(new String[]{"4568"});
        Backend backend = new Backend();
        backend.start();
    }
}
