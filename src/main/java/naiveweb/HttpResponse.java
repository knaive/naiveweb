package naiveweb;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
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
    private String createBody() throws IOException {
        if (!fileServer.exists()) {
            throw new IOException("File not found");
        }
        if (fileServer.isRegFile()) {
            return fileServer.getRegularFileContents();
        }

        List<Path> files = fileServer.getDirectoryContents();
        StringBuilder sb = new StringBuilder(4096);
        sb.append(String.format("<div>File list: </div>"));
        for(int i=0; i<files.size(); i++) {
            Path file = files.get(i);
            int count = file.getNameCount();
            String link = file.toString();
            String displayName = "..";
            if (i != 0) {
                displayName = file.subpath(count-1, count).toString();
            }
            sb.append(String.format("<div><a href='%s'>%s</a></div>\n", link, displayName));
        }
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
