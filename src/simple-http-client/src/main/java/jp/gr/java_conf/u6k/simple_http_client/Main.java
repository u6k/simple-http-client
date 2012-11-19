
package jp.gr.java_conf.u6k.simple_http_client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {

    /**
     * アプリのエントリーポイント。
     * 
     * <p>
     * アプリケーション引数の説明。
     * </p>
     * <dl>
     * <dt>-url=&lt;url&gt;</dt>
     * <dd>(必須) 接続先URL</dd>
     * <dt>-method=&lt;method&gt;</dt>
     * <dd>(必須) メソッド(GET, POSTなど)</dd>
     * <dt>-req=&lt;path%gt;</dt>
     * <dd>HTTPリクエストを記述したファイル。GETの場合は未指定</dd>
     * <dt>-log=&lt;path&gt;</dt>
     * <dd>ログ出力先ファイル</dd>
     * </dl>
     * 
     * @param args アプリケーション引数。
     */
    public static void main(String[] args) {
        try {
            // アプリケーション引数を解析
            String url = null;
            String method = null;
            String req = null;
            String log = null;
            boolean help = false;

            for (String arg : args) {
                if (arg.startsWith("-url=")) {
                    url = arg.substring("-url=".length()).trim();
                } else if (arg.startsWith("-method=")) {
                    method = arg.substring("-method=".length()).trim();
                } else if (arg.startsWith("-req=")) {
                    req = arg.substring("-req=".length()).trim();
                } else if (arg.startsWith("-log=")) {
                    log = arg.substring("-log=".length()).trim();
                } else if ("-help".equals(args)) {
                    help = true;
                }
            }

            if (help) {
                printHelp();
                return;
            }

            if (url == null) {
                System.out.println("エラー: -url引数が指定されていません。");
                System.out.println("");
                printHelp();
                return;
            }

            if (method == null) {
                System.out.println("エラー: -method引数が指定されていません。");
                System.out.println("");
                printHelp();
                return;
            }

            // HTTPリクエストのファイルを読み込み
            byte[] reqData = null;

            if (req != null) {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                FileInputStream fin = new FileInputStream(req);
                try {
                    int len;
                    byte[] buf = new byte[1024];

                    while ((len = fin.read(buf)) != -1) {
                        bout.write(buf, 0, len);
                    }

                    reqData = bout.toByteArray();
                } finally {
                    fin.close();
                }
            }

            // HTTPリクエストを送信、レスポンスを受信
            FileOutputStream fout = null;
            if (log != null) {
                fout = new FileOutputStream(log);
            }

            HttpURLConnection urlCon = (HttpURLConnection) new URL(url).openConnection();
            urlCon.setRequestMethod(method);

            if (reqData != null) {
                urlCon.setDoOutput(true);

                OutputStream out = urlCon.getOutputStream();
                out.close();
            }

            int respCode = urlCon.getResponseCode();
            String charset = null;

            for (String key : urlCon.getHeaderFields().keySet()) {
                String value = urlCon.getHeaderField(key);

                if (key != null && key.toLowerCase().equals("content-type")) {
                    charset = getCharset(value);
                }

                if (key != null) {
                    printResponse(key + ": " + value, fout);
                } else {
                    printResponse(value, fout);
                }
            }

            printResponse("", fout);

            if (respCode == 200) {
                BufferedReader r;
                if (charset != null) {
                    r = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), charset));
                } else {
                    r = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
                }

                String line;
                while ((line = r.readLine()) != null) {
                    printResponse(line, fout);
                }
            }

            urlCon.disconnect();

            if (fout != null) {
                fout.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printHelp() {
        System.out.println("simple-http-client v20121119");
        System.out.println("指定したURLにHTTPリクエストを送信し、HTTPレスポンスを受信して表示する。");
        System.out.println("HTTPリクエストは標準入力から入力する。");
        System.out.println("");
        System.out.println("usage: java -jar simple-http-client.jar <options>");
        System.out.println("options:");
        System.out.println("    -url=<url>       (必須) 接続先URL");
        System.out.println("    -method=<method> (必須) メソッド(GET, POSTなど)");
        System.out.println("    -req=<path>      HTTPリクエストを記述したファイル");
        System.out.println("                     GETの場合は未指定");
        System.out.println("    -log=<path>      ログ出力先ファイル");
        System.out.println("    -help            ヘルプを表示");
    }

    private static void printResponse(String msg, OutputStream out) throws IOException {
        System.out.println(msg);

        if (out != null) {
            out.write((msg + "\r\n").getBytes());
        }
    }

    private static String getCharset(String contentTypeHeader) {
        for (String param : contentTypeHeader.split(";")) {
            param = param.trim().toLowerCase();

            if (param.startsWith("charset=")) {
                String charset = param.substring("charset=".length());
                return charset;
            }
        }

        return null;
    }

}
