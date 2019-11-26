package jp.co.hiropro.seminar_hall.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.util.List;
import java.util.Map;

import io.github.sac.Ack;
import io.github.sac.BasicListener;
import io.github.sac.ReconnectStrategy;
import io.github.sac.Socket;

public class SocketService extends Service {
    private static final SocketService INSTANCE = new SocketService();
    public Socket socket;

    private SocketService() {
        socket = new Socket("");
        socket.setListener(new BasicListener() {

            public void onConnected(Socket socket, Map<String, List<String>> headers) {
                System.out.println("Connected to endpoint");
            }

            public void onDisconnected(Socket socket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                System.out.println("Disconnected from end-point");
            }

            public void onConnectError(Socket socket, WebSocketException exception) {
                System.out.println("Got connect error " + exception);
            }

            public void onSetAuthToken(String token, Socket socket) {
                System.out.println("Token is " + token);
            }

            public void onAuthentication(Socket socket, Boolean status) {
                if (status) {
                    System.out.println("socket is authenticated");
                } else {
                    System.out.println("Authentication is required (optional)");
                }
            }

        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Static 'instance' method
    public static SocketService getInstance() {
        return INSTANCE;
    }

    public void connect() {
        socket.setReconnection(new ReconnectStrategy().setDelay(2000).setMaxAttempts(30));
        socket.connectAsync();
    }

    public void disconnect() {
        socket.disconnect();
    }

    public void sendMessage(String message) {
        socket.emit("event_name", message, new Ack() {
            @Override
            public void call(String name, Object error, Object data) {
                //If error and data is String
                System.out.println("Got message for :Send error is :" + error + " data is :" + data);
            }
        });
    }
}
