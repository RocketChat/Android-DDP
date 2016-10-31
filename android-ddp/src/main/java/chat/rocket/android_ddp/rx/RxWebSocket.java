package chat.rocket.android_ddp.rx;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;
import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;

public class RxWebSocket {
    private OkHttpClient mHttpClient;
    private WebSocket mWebSocket;

    public RxWebSocket(OkHttpClient client) {
        mHttpClient = client;
    }
    public ConnectableObservable<RxWebSocketCallback.Base> connect(String url){
        final Request request = new Request.Builder().url(url).build();
        WebSocketCall call = WebSocketCall.create(mHttpClient, request);

        return Observable.create(new Observable.OnSubscribe<RxWebSocketCallback.Base>() {
            @Override
            public void call(Subscriber<? super RxWebSocketCallback.Base> subscriber) {
                call.enqueue(new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        mWebSocket = webSocket;
                        subscriber.onNext(new RxWebSocketCallback.Open(mWebSocket, response));
                    }

                    @Override
                    public void onFailure(IOException e, Response response) {
                        subscriber.onError(new RxWebSocketCallback.Failure(mWebSocket, e, response));
                    }

                    @Override
                    public void onMessage(ResponseBody responseBody) throws IOException {
                        subscriber.onNext(new RxWebSocketCallback.Message(mWebSocket, responseBody));
                    }

                    @Override
                    public void onPong(Buffer payload) {
                        subscriber.onNext(new RxWebSocketCallback.Pong(mWebSocket, payload));
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        subscriber.onNext(new RxWebSocketCallback.Close(mWebSocket, code, reason));
                        subscriber.onCompleted();
                    }
                });
            }
        }).publish();
    }


    public void sendText(String message) throws IOException {
        mWebSocket.sendMessage(RequestBody.create(WebSocket.TEXT, message));
    }

    public void close(int code, String reason) throws IOException {
        mWebSocket.close(code, reason);
    }
}
