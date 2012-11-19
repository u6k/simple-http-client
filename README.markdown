# シンプルHTTPクライアント

    simple-http-client v20121119
    指定したURLにHTTPリクエストを送信し、HTTPレスポンスを受信して表示する。
    HTTPリクエストは標準入力から入力する。
    
    usage: java -jar simple-http-client.jar <options>
    options:
        -url=<url>       (必須) 接続先URL
        -method=<method> (必須) メソッド(GET, POSTなど)
        -req=<path>      HTTPリクエストを記述したファイル
                         GETの場合は未指定
        -log=<path>      ログ出力先ファイル
        -help            ヘルプを表示
