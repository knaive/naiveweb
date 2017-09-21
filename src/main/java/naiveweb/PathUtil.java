package naiveweb;

public class PathUtil {
    // path1 and path2 should be absolute paths
    public static String join(String path1, String path2) {
        if (path1 == null) return path2;
        if (path2 == null) return path1;

        String path = path1 + "/" + path2;
        return path.replace("/+", "/");
    }

    // path should be an absolute path
    public static String getParent(String path) {
        if (path == null || "/".equals(path)) return null;

        String pattern = "[^/]+/?$";
        String parent = path.replaceAll(pattern, "");
        return "/".equals(parent)? "/" : parent.replaceAll("/+$", "");
    }
}
