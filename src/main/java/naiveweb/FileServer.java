package naiveweb;

import java.io.File;
import java.nio.file.*;

public class FileServer {
    private static final String defaultRoot = System.getProperty("user.dir");

    public static WebFile getFile(String uri) throws Exception {
        Path real;
        if (uri == null) return null;
        try {
            real = Paths.get(defaultRoot, uri);
        } catch (InvalidPathException e) {
            Logger.error(e.getMessage());
            return null;
        }
        File file = real.toFile();
        if (!file.exists()) {
            Logger.error("***File not found: ", real.toString());
            return null;
        }
        return new WebFile(file, uri);
    }
}