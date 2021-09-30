# HttpParser

半天写完的Http服务器，由于Java学得不系统也不知道有啥设计模式，所以编码写的很脏;

A simple http parser using Java Socket;

## Socket部分 

用了特别老的ServerSocket/Socket，（因为懒，且不太会用BufferedByte）;

加了个进程池;

## Http解析部分

请求行/响应行/请求头/响应行用了字符流，请求体/响应体用了byte[]，所以完全不支持HTTP/2.0;

Content-Encoding也只支持identity;

## 展望

- HTML模板 使用Thymeleaf？
- Content-Encoding做做压缩？
- Transfer-Encoding做做chunked？
