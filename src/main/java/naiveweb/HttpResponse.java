package naiveweb;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class HttpResponse {
    private Writer writer;
    private String uri;
    private String contentType;

    public HttpResponse(Writer writer, String uri) {
        this.writer = writer;
        this.contentType = "text/plain";
        this.uri = uri;
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
        writer.write(String.format("Content-Type: %s\r\n", this.contentType));
        writer.write("Content-Length: " + body.getBytes().length + "\r\n");
        writer.write("\r\n");
        writer.write(body);
        writer.flush();
    }

    private String createBody(WebFile file) throws Exception {
        if (!file.isDirectory()) {
            this.contentType = file.getMIME();
            return file.getContents();
        }

        this.contentType = "text/html";

        WebFile[] files = file.list();

        // if this folder is empty

        StringBuilder body = new StringBuilder();
        body.append("<ul>");
        WebFile parent = file.getParent();
        if (parent != null) {
            body.append(String.format("<li><a href='%s'>..Back to parent directory</a></li>\n", parent.getUri()));
        }
        if (files.length == 0) {
            body.append("<li>No files in this directory.</li>\n");
        }
        for(WebFile f : files) {
            String link = f.getUri();
            String name = f.getName();
            if (f.isDirectory()) name = name + "/";
            body.append(String.format("<li><a href='%s'>%s</a></li>\n", link, name));
        }
        body.append("</ul>");
        return body.toString();
    }

    private void respond404() throws IOException {
        writer.write("HTTP/1.1 301 Found\r\n");
        writer.write("Content-Type: text/html\r\n");
        writer.write("Location: http://localhost:8080/404.html\r\n");
        writer.write("\r\n");
        writer.flush();
    }

    public void respond() throws Exception {
        WebFile file = FileServer.getFile(uri);
        if (file == null) {
            respond404();
            return;
        }
        String body = createBody(file);
        doRespond(200, body);
    }
}
