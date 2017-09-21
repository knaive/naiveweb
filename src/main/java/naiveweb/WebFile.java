package naiveweb;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class WebFile {
    private static final Map<String, String> mime;
    private File file;
    private String url;
    private String name;

    static {
        mime = new HashMap<String, String>();
        mime.put("", "text/plain");
        mime.put("html", "text/html");
        mime.put("xml", "text/xml");
        mime.put("css", "text/css");
        mime.put("csv", "text/csv");
        mime.put("js", "text/javascript");
        mime.put("md", "text/markdown");
        mime.put("pdf", "application/pdf");
        mime.put("bmp", "image/bmp");
        mime.put("png", "text/png");
        mime.put("jpeg", "text/jpeg");
    }

    public WebFile(File file, String url) throws Exception {
        if (file==null || url==null) throw new Exception("Parameter can not be null");
        this.file = file;
        this.url = url.replaceAll("/+", "/");
        this.name = this.file.getName();
    }

    public long size() {
        return this.file.length();
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return this.name;
    }

    public boolean isDirectory() {
        return this.file.isDirectory();
    }

    public String getContents() {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader in = new BufferedReader(new FileReader(this.file))) {
            while((line = in.readLine())!=null) sb.append(line);
        } catch (FileNotFoundException e) {
            Logger.error("File not found: ", e.getMessage());
        } catch (IOException e) {
            Logger.error("IOException: ", e.getMessage());
        }
        return sb.toString();
    }

    public WebFile[] list() throws Exception {
        if(!isDirectory()) return null;

        String[] filenames = this.file.list();
        String path = this.file.getAbsolutePath();

        WebFile[] webfiles = new WebFile[filenames.length];

        String name, url;
        File file;
        for(int i=0; i<filenames.length; i++) {
            name = filenames[i];
            file = new File(path, name);
            url = PathUtil.join(getUrl(), name);
            webfiles[i] = new WebFile(file, url);
        }
        return webfiles;
    }

    public WebFile getParent() throws Exception {
        String parent = PathUtil.getParent(this.getUrl());
        return FileServer.getFile(parent);
    }

    public String getMIME() {
        int index = this.name.lastIndexOf(".");
        if (index == this.name.length() || index < 0) return "text/plain";
        String extension = this.name.substring(index+1);
        if (this.mime.containsKey(extension)) return this.mime.get(extension);
        else return "text/plain";
    }
}
