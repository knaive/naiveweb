package naiveweb;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileServer {
    private static final String defaultRoot = System.getProperty("user.dir");
    private String rootDir = "./";
    private Path root;
    private Path path;
    private File file;
    public FileServer(String uri) {
        this(uri, defaultRoot);
    }
    public FileServer(String uri, String rootDir) {
        this.rootDir = rootDir;
        this.root = Paths.get(this.rootDir);

        this.path = null;
        this.file = null;

        if (uri == null) return;

        try {
            this.path = Paths.get(this.rootDir, uri);
        } catch (InvalidPathException e) {
            Logger.error("***File not found");
            return;
        }
        File tmp = this.path.toFile();
        if (tmp.exists()) {
            this.file = tmp;
        } else {
            Logger.error("***File not found: ", this.path.toString());
        }
    }

    private Path toVirtualPath(Path path) {
        if (!path.startsWith(this.rootDir)) return path;
        if (this.root.compareTo(path) == 0) return Paths.get("/");

        int count = this.root.getNameCount();
        int totalCount = path.getNameCount();
        return path.subpath(count, totalCount);
    }

    public boolean exists() {
        return this.file != null;
    }
    public boolean isDirectory() {
        return exists() && this.file.isDirectory();
    }
    public boolean isRegFile() {
        return exists() && this.file.isFile();
    }
    public List<Path> getDirectoryContents() throws IOException {
        if (isDirectory()) {
            List<Path> files = new ArrayList<Path>();
            // add parent dir as the first element
            Path parentPath = this.path.getParent();
            files.add(toVirtualPath(parentPath));
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path path: stream) {
                    Path virtualPath = toVirtualPath(path);
                    System.out.println(virtualPath.toString());
                    files.add(virtualPath);
                }
            }
            return files;
        }
        return null;
    }
    public String getRegularFileContents() throws IOException {
        if (isRegFile()) {
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, "UTF8");
        }
        return null;
    }
}