package naiveweb;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileServer {
    private static final String defaultRoot = System.getProperty("user.dir");
    private String rootDir;
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

    private String toVirtualPath(Path path) {
        if (!path.startsWith(this.rootDir)) return null;
        if(this.root.compareTo(path) == 0) return "/";

        String suffix = path.toFile().isDirectory() ? "/" : "";

//        int count = this.root.getNameCount();
//        int totalCount = path.getNameCount();
//        return path.subpath(count, totalCount);
//        return path.getName(totalCount - 1);
        Path virtualPath = root.relativize(path);
        String unixStylePath = virtualPath.toString().replaceAll("\\\\", "/");
        return "/" + unixStylePath + suffix;
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
    public List<String> getDirectoryContents() throws IOException {
        if (isDirectory()) {
            List<String> files = new ArrayList<String>();
            // add parent dir as the first element
            Path parentPath = this.path.getParent();
            String pathName = toVirtualPath(parentPath);
            files.add(pathName);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.path)) {
                String name;
                for (Path path: stream) {
                    name = toVirtualPath(path);
                    System.out.println(name);
                    files.add(name);
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