package naiveweb;

public class Logger {
    private static String assemble(String... message) {
        StringBuilder sb = new StringBuilder();
        for(String msg : message) {
            sb.append(msg);
        }
        return sb.toString();
    }
    public static void info(String... message) {
        String finalMessage = assemble(message);
        System.out.println(finalMessage);
    }
    public static void error(String... message) {
        String finalMessage = assemble(message);
        System.err.println(finalMessage);
    }
}