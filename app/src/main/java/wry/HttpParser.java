package wry;

import java.io.*;
import org.thymeleaf.*;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.net.*;
import java.util.*;

public class HttpParser implements Runnable {

    private Socket socket;
    private BufferedInputStream bytesInput;
    private BufferedOutputStream bytesOutput;
    private BufferedReader httpReader;
    private PrintWriter httpWriter;
    private TemplateEngine templateEngine;
    private FileTemplateResolver fileTemplateResolver;

    HttpParser(Socket socket) throws IOException {
        this.socket = socket;
        this.bytesInput = new BufferedInputStream(socket.getInputStream());
        this.bytesOutput = new BufferedOutputStream(socket.getOutputStream());
        this.httpReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
        this.httpWriter = new PrintWriter(socket.getOutputStream(), true);
        this.statusCode = new HashMap<>();

        this.statusCode.put(100, "Continue");
        this.statusCode.put(101, "Switching Protocol");
        this.statusCode.put(103, "Early Hints");
        this.statusCode.put(200, "OK");
        this.statusCode.put(201, "Created");
        this.statusCode.put(202, "Accepted");
        this.statusCode.put(203, "Non-Authoritative Information");
        this.statusCode.put(204, "No Content");
        this.statusCode.put(205, "Reset Content");
        this.statusCode.put(206, "Partial Content");
        this.statusCode.put(300, "Multiple Choice");
        this.statusCode.put(301, "Moved Permanently");
        this.statusCode.put(302, "Found");
        this.statusCode.put(303, "See Other");
        this.statusCode.put(304, "Not Modified");
        this.statusCode.put(305, "Use Proxy");
        this.statusCode.put(306, "unused");
        this.statusCode.put(307, "Temporary Redirect");
        this.statusCode.put(308, "Permanent Redirect");
        this.statusCode.put(400, "Bad Request");
        this.statusCode.put(401, "Unauthorized");
        this.statusCode.put(402, "Payment Required");
        this.statusCode.put(403, "Forbidden");
        this.statusCode.put(404, "Not Found");
        this.statusCode.put(405, "Method Not Allowed");
        this.statusCode.put(406, "Not Acceptable");
        this.statusCode.put(407, "Proxy Authentication Required");
        this.statusCode.put(408, "Request Timeout");
        this.statusCode.put(409, "Conflict");
        this.statusCode.put(410, "Gone");
        this.statusCode.put(411, "Length Required");
        this.statusCode.put(412, "Precondition Failed");
        this.statusCode.put(413, "Payload Too Large");
        this.statusCode.put(414, "URI Too Long");
        this.statusCode.put(415, "Unsupported Media Type");
        this.statusCode.put(416, "Requested Range Not Satisfiable");
        this.statusCode.put(417, "Expectation Failed");
        this.statusCode.put(418, "I'm a teapot");
        this.statusCode.put(421, "Misdirected Request");
        this.statusCode.put(425, "Too Early");
        this.statusCode.put(426, "Upgrade Required");
        this.statusCode.put(428, "Precondition Required");
        this.statusCode.put(429, "Too Many Requests");
        this.statusCode.put(431, "Request Header Fields Too Large");
        this.statusCode.put(451, "Unavailable For Legal Reasons");
        this.statusCode.put(500, "Internal Server Error");
        this.statusCode.put(501, "Not Implemented");
        this.statusCode.put(502, "Bad Gateway");
        this.statusCode.put(503, "Service Unavailable");
        this.statusCode.put(504, "Gateway Timeout");
        this.statusCode.put(505, "HTTP Version Not Supported");
        this.statusCode.put(506, "Variant Also Negotiates");
        this.statusCode.put(507, "Insufficient Storage");
        this.statusCode.put(510, "Not Extended");
        this.statusCode.put(511, "Network Authentication Required");
    }

    private final Map<Integer, String> statusCode;

    @Override
    public void run() {
        try {
            Request req = new Request(true);
            if (!req.isSent()) {
                Response res = new Response(req);
                switch (req.getMethod()) {
                    case "GET":
                        this.getHandler(req, res);
                        break;
                    case "HEAD":
                        // break;
                    case "PUT":
                        // break;
                    case "POST":
                        // break;
                    case "DELETE":
                        break;
                    case "TRACE":
                        this.traceHandler(req, res);
                        break;
                    case "OPTIONS":
                        this.optionsHandler(req, res);
                        break;
                    default:
                        this.methodNotAllowedHandler(req, res);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // region Massive clean up
            if (this.bytesInput != null) {
                try {
                    this.bytesInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.bytesInput = null;
            }
            if (this.httpReader != null) {
                try {
                    this.httpReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.httpReader = null;
            }
            if (this.httpWriter != null) {
                this.httpWriter.close();
                this.httpWriter = null;
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
                System.out.println("Connection closed");
            } // endregion
        }
    }

    private void getHandler(Request req, Response res) throws IOException {
        try {
            URL url = new URL("http://" + req.getHeaders().getOrDefault("Host", "localhost:8080") + req.getUri());
            Path path = Paths.get("." + url.getPath());
            res.setHeaders("Content-Type", Files.probeContentType(path));
            byte[] binary = Files.readAllBytes(path);
            res.setMessage(binary);
        } catch (IOException e) {
            e.printStackTrace();
            res.setCode(404);
            res.setMessage(new byte[0]);
        } finally {
            res.send();
        }
    }

    private void traceHandler(Request req, Response res) throws IOException {
        res.setHeaders("Content-Type", "text/plain");
        res.setCode(404);
        res.setMessage(req.getRawRequest().getBytes());
        res.send();
    }

    private void optionsHandler(Request req, Response res) throws IOException {
        res.setHeaders("Allow", "GET, HEAD, PUT, POST, TRACE, OPTIONS, DELETE");
        res.send();
    }

    private void methodNotAllowedHandler(Request req, Response res) throws IOException {
        res.setHeaders("Allow", "GET, HEAD, PUT, POST, TRACE, OPTIONS, DELETE");
        res.setCode(405);
        res.send();
    }

    public class Request {
        private Boolean sent;
        private Boolean keepRaw;
        private String method;                  // 请求方法 GET/POST/PUT/DELETE/OPTION...
        private String uri;                     // 请求的uri
        private String version;                 // http版本
        private byte[] message;                 // 请求体
        private Map<String, String> headers;    // 请求头
        private StringBuilder rawRequest;

        // region getter
        Boolean isSent() {
            return sent;
        }

        void send() {
            sent = true;
        }

        public String getVersion() {
            return version;
        }

        public String getMethod() {
            return method;
        }

        public String getUri() {
            return uri;
        }

        public byte[] getMessage() {
            return message;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public String getRawRequest() {
            return rawRequest.toString();
        }
        // endregion

        Request(Boolean keepRaw) throws IOException {
            this.sent = false;
            this.keepRaw = keepRaw;
            this.headers = new HashMap<>(16);
            this.rawRequest = new StringBuilder();
            this.decodeRequest();
        }

        private void decodeRequest() throws IOException {
            this.decodeRequestLine();
        }

        private void decodeRequestLine() throws IOException {
            // region Deal with keepRaw
            String rawStartLine = HttpParser.this.httpReader.readLine();
            if (this.keepRaw) {
                this.rawRequest.append(rawStartLine).append("\r\n");
            } // endregion
            // region Build ArrayList
            ArrayList<String> startLine = new ArrayList<String>(Arrays.asList(rawStartLine.split(" "))); // endregion
            // region Deal with HTTP/0.9
            if (startLine.size() == 2) {
                startLine.add(2, "HTTP/0.9");
            } //endregion
            // region Deal with Method, URI & HTTP version
            if (startLine.size() == 3 && startLine.get(2).matches("HTTP/((0\\.9)|(1\\.[01]))")) {
                // region Set HTTP version
                this.version = startLine.get(2); // endregion
                // region Turn on keepRaw
                if (startLine.get(0).equals("TRACE")) {
                    if (!this.keepRaw) {
                        this.rawRequest.append(rawStartLine).append("\r\n");
                    }
                    this.keepRaw = true;
                } // endregion
                // region Set Method & URI
                this.method = startLine.get(0);
                this.uri = startLine.get(1);
                this.decodeRequestHeader(); // endregion // fixme: Add consume
            } else {
                // region Deal with potential BadRequest
                Response badRes = new Response( this );
                badRes.setCode(400);
                badRes.send();
                this.decodeRequestHeader(); // endregion
            } // endregion
        }

        private void decodeRequestHeader() throws IOException {
            // region Set HTTP request headers
            String rawHeaderLine;
            String[] kv;
            while (!(rawHeaderLine = HttpParser.this.httpReader.readLine()).equals("")) {
                if (keepRaw) {
                    this.rawRequest.append(rawHeaderLine).append("\r\n");
                }
                kv = rawHeaderLine.split(":");
                this.headers.put(kv[0].trim(), kv[1].trim());
            }
            // endregion
            // region Deal with PUT/POST HTTP request body
            if (this.method.matches("(PUT|POST)")) {
                this.decodeRequestMessage();
            }
            // endregion
        }

        private void decodeRequestMessage() throws IOException {
            // region Get Content-Length
            int contentLen = Integer.parseInt(this.headers.getOrDefault("Content-Length", "0")); // endregion
            // region Deal with Content-Length & Keep-Alive
            if (contentLen > 0) {
                // region Read bytes as assigned
                byte[] message = new byte[contentLen];
                int ch = HttpParser.this.bytesInput.read(message); // endregion
                // region Deal with keepRaw
                if (this.keepRaw) {
                    this.rawRequest.append("\r\n").append(new String(message));
                } // endregion
                // region Set message
                this.message = message; // endregion
            } else if (!this.headers.getOrDefault("Connection", "Keep-Alive").equals("Keep-Alive")) {
                // region Read bytes when ready
                List<Byte> ListMessage = new ArrayList<>();
                byte[] message;
                int ch;
                while ((ch = HttpParser.this.bytesInput.read()) != -1) {
                    ListMessage.add((byte) ch);
                    if (this.keepRaw) {
                        this.rawRequest.append("\r\n").append((byte) ch);
                    }
                } // endregion
                message = new byte[ListMessage.toArray().length];
                for (int i = 0; i < ListMessage.toArray().length; i++) {
                    message[i] = ListMessage.get(i);
                }
                this.message = message; // endregion
            } else {
                // region Deal with potential Not Implemented
                Response badRes = new Response(
                        this
                );
                badRes.setCode(501);
                badRes.send(); // endregion
            } // endregion
        }
//        private void decodeRequestMessageChunkedTransferred() throws IOException {
//            StringBuilder message = new StringBuilder();
//            String chunkSize, rawMessageLine;
//            while (!(chunkSize = this.httpReader.readLine()).equals("0")) {
//                rawMessageLine = this.httpReader.readLine();
//                message.append(rawMessageLine);
//                if (this.keepRaw) {
//                    rawRequest.append("\r\n")
//                              .append(chunkSize)
//                              .append("\r\n")
//                              .append(rawMessageLine)
//                              .append("\r\n");
//                }
//            }
//            if (!(rawMessageLine = this.httpReader.readLine()).equals("")) {
////                if(this.headers.getOrDefault("", "").equals(""))
//            }
//            if (this.keepRaw) {
//                rawRequest.append("0").append("\r\n")
//                          .append(rawMessageLine).append("\r\n");
//            }
//            this.message = message.toString();
//        }
    }

    public class Response {
        private Integer code;
        private String status;
        private String version;
        private byte[] message;
        private Map<String, String> headers;
        private Request request;

        // region getter/setter
        void setCode(Integer code) {
            if ( code != null && statusCode.containsKey(code)) {
                this.code = code;
                this.status = statusCode.get(code);
            } else {
                this.code = 200;
                this.status = "OK";
            }
        }

        void setMessage(byte[] message) {
            this.message = message;
        }

        void setHeaders(String k, String v) {
            this.headers.put(k, v);
        }
        // endregion

        Response(Request request) {
            this.setCode(200);
            this.request = request;
            this.version = request.getVersion();
            this.headers = new HashMap<>();
        }

        void send() throws IOException {
            this.headers.put("Content-Length", String.valueOf(message.length));
            HttpParser.this.httpWriter.print(this.version + " " + this.code + " " + this.status + "\r\n");
            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                HttpParser.this.httpWriter.print(entry.getKey() + ": " + entry.getValue() + "\r\n");
            }
            HttpParser.this.httpWriter.println();
            HttpParser.this.bytesOutput.write(this.message);
            HttpParser.this.bytesOutput.flush();
            request.send();
        }
    }
}
