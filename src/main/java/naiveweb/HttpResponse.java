package naiveweb;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class HttpResponse {
    private FileServer fileServer;
    private Writer writer;
    public HttpResponse(Writer writer, String uri) {
        this.writer = writer;
        this.fileServer = new FileServer(uri);
    }
    private void doRespond(int status, String body) throws IOException {
        String msg;
        switch (status) {
            case 200:
                msg = "OK";
                break;
            case 404:
                msg = "NotFound";
                break;
            default:
                msg = "Invalid Request";
        }
        writer.write(String.format("HTTP/1.1 %d %s\r\n", status, msg));
        writer.write("Content-Type: text/html\r\n");
        writer.write("Content-Length: " + body.getBytes().length + "\r\n");
        writer.write("\r\n");
        writer.write(body);
        writer.flush();
    }
    private String getBasePath(String path) {
        String base = null;
        String[] parts = path.split("/");
        if(parts.length > 0) {
            base = parts[parts.length-1];
            if(!path.endsWith(base)) base = base + "/";
        }
        return base;
    }
    private String createBody() throws IOException {
        if (!fileServer.exists()) {
            throw new IOException("File not found");
        }
        if (fileServer.isRegFile()) {
            return fileServer.getRegularFileContents();
        }

        List<String> filenames = fileServer.getDirectoryContents();
        StringBuilder sb = new StringBuilder(4096);
        sb.append("<ul>");
        String link, name;
        for(int i=0; i<filenames.size(); i++) {
            link = filenames.get(i);
            if (link != null) {
                name = "..";
                if (i > 0) {
                    name = getBasePath(link);
                    if(!link.endsWith(name)) name = name + "/";
                }
                sb.append(String.format("<li><a href='%s'>%s</a></li>\n", link, name));
            }
        }
        sb.append("</ul>");
        return sb.toString();
    }
    public void respond() throws IOException {
        boolean valid = fileServer.exists();
        if (!valid) {
            doRespond(404, "<h1>Not Found</h1>");
            return;
        }
        String body = createBody();
        doRespond(200, body);
    }
}
