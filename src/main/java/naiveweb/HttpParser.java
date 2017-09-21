package naiveweb;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

enum HttpRequestLineType {
    STARTLINE,
    HEADER,
    BODY,
    EOF
}

public class HttpParser {
    private int contentLength;
    private String method;
    private String uri;
    private String httpVersion;
    private Hashtable<String, String> extraHeaders;
    private HttpRequestLineType type;
    private final String startLineDelimPattern = "\\s+";
    private final String headerDelim = ":";
    private StringBuilder body;
    private int receivedBodyLength;
    public String getUri() {
        return uri;
    }
    public void dumpRequestInfo() {
        StringBuilder info = new StringBuilder(4096);
        info.append("\n**************************************\n");
        info.append(String.format("method: %s\n", method));
        info.append(String.format("uri: %s\n", uri));
        info.append(String.format("http: %s\n", httpVersion));
        info.append(String.format("contentLength: %d\n", contentLength));

        Set<String> keys = extraHeaders.keySet();
        Iterator<String> iterator = keys.iterator();
        String key;

        while (iterator.hasNext()) {
            key = iterator.next();
            info.append(String.format("%s: %s\n", key, extraHeaders.get(key)));
        }
        info.append("**************************************\n");
        Logger.info(info.toString());
    }
    public HttpParser() {
        this.contentLength = 0;
        this.receivedBodyLength = 0;
        this.type = HttpRequestLineType.STARTLINE;
        this.extraHeaders = new Hashtable<String, String>();
        this.body = new StringBuilder(4096);
    }
    public boolean parseRequestMessage(String line) throws Exception {
        if (line == null) return true;
        switch (this.type) {
            case STARTLINE:
                if(parseStartLine(line)) this.type = HttpRequestLineType.HEADER;
                return false;
            case HEADER:
                if (parseHeader(line)) {
                    this.type = contentLength!=0 ? HttpRequestLineType.BODY : HttpRequestLineType.EOF;
                }
                return this.type == HttpRequestLineType.EOF;
            case BODY:
                return parseBody(line);
            default:
                return this.type == HttpRequestLineType.EOF;
        }
    }
    private boolean parseStartLine(String line) {
        String[] parts = line.split(startLineDelimPattern);
        this.method = parts[0];
        this.uri = parts[1];
        this.httpVersion = parts[2];
        return true;
    }
    private boolean parseHeader(String line) throws Exception {
        if ("".equals(line)) return true;

        int index = line.indexOf(headerDelim);
        String name, value;
        try {
            name = line.substring(0, index);
            value = line.substring(index+1, line.length());
            value = value.trim();
        } catch(Exception e) {
            throw new Exception("Invalid header: " + line);
        }

        if (name.toLowerCase().equals("content-length")) {
            contentLength = Integer.parseInt(value);
            return false;
        }

        extraHeaders.put(name, value);
        return false;
    }
    private boolean parseBody(String body) {
        if (contentLength == receivedBodyLength) return true;

        this.body.append(body);
        receivedBodyLength += body.getBytes().length;

        return receivedBodyLength == contentLength;
    }
}
