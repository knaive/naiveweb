package naiveweb;

import java.io.File;
import java.nio.file.*;

public class FileServer {
    private static final String defaultRoot = System.getProperty("user.dir");
    private static final String[] roots = {"static"};
    
    public static WebFile getFile(String uri) throws Exception {
        if (uri == null) return null;
        WebFile file = search(defaultRoot, uri);
        if (file != null) return file;

        for(String root : roots) {
            root = defaultRoot + "\\" + root;
            file = search(root, uri);
            if (file != null) return file;
        }
        return null;
    }

    private static WebFile search(String root, String uri) throws Exception {
        Path real;
        try {
            real = Paths.get(root, uri);
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