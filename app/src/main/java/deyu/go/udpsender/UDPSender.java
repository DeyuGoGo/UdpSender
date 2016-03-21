package deyu.go.udpsender;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;


public class UDPSender {
    private String ipAddress;
    private int port;
    private MulticastSocket socket;
    private Handler uiHandler;
    private SenderHandler nonUiHandler;
    private HandlerThread mHandlerThread = new HandlerThread("NonUiThread");


    public UDPSender(String ipAddress, int port, Handler handler) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.uiHandler = handler;
    }

    public void init() {
        mHandlerThread.start();
        nonUiHandler = new SenderHandler(mHandlerThread.getLooper());
        nonUiHandler.sendMessage(nonUiHandler.obtainMessage(nonUiHandler.WHAT_CREATE_SOCKET));
    }
    public void setIpAndPort(String ipAddress , int port){
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void close() {
        socket.close();
    }

    public void transmit(String s) {
        uiHandler.sendMessage(uiHandler.obtainMessage(MainActivity.WHAT_TOAST_OBJECT, "" + s));
        nonUiHandler.sendMessage(nonUiHandler.obtainMessage(nonUiHandler.WHAT_SEND_DATA, s));
    }
    public void transmit(int[] data) {
        uiHandler.sendMessage(uiHandler.obtainMessage(MainActivity.WHAT_TOAST_OBJECT, Arrays.toString(data)));
        nonUiHandler.sendMessage(nonUiHandler.obtainMessage(nonUiHandler.WHAT_SEND_INT_ARRAY, data));
    }
    public void transmit(char[] data) {
        String s = new String(data);
        uiHandler.sendMessage(uiHandler.obtainMessage(MainActivity.WHAT_TOAST_OBJECT,s));
        nonUiHandler.sendMessage(nonUiHandler.obtainMessage(nonUiHandler.WHAT_SEND_DATA, s));
    }

    protected MulticastSocket createSocket() throws IOException {
        return new MulticastSocket();
    }

    private void transmit(MulticastSocket socket, byte[] data) {
        Log.d("transmit","transmit");
        try {
            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getByName(ipAddress),
                    port
            );
            socket.send(packet);
        } catch (UnknownHostException e) {
            uiHandler.sendMessage(uiHandler.obtainMessage(MainActivity.WHAT_TOAST_OBJECT, "" + e));
        } catch (IOException e) {
            uiHandler.sendMessage(uiHandler.obtainMessage(MainActivity.WHAT_TOAST_OBJECT, "" + e));
        }
    }

    private class SenderHandler extends Handler {
        public final int WHAT_CREATE_SOCKET = 0x01;
        public final int WHAT_SEND_DATA = 0x02;
        public final int WHAT_SEND_INT_ARRAY = 0x03;

        public SenderHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_CREATE_SOCKET:
                    try {
                        socket = createSocket();
                    } catch (IOException e) {
                        uiHandler.sendMessage(uiHandler.obtainMessage(MainActivity.WHAT_TOAST_OBJECT, "" + e));
                    }
                    break;
                case WHAT_SEND_DATA:
                    transmit(socket, ((String) msg.obj).getBytes());
                    break;
                case WHAT_SEND_INT_ARRAY:
                    int[] data = (int[])msg.obj;
                    ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
                    IntBuffer intBuffer = byteBuffer.asIntBuffer();
                    intBuffer.put(data);
                    byte[] array = byteBuffer.array();
                    transmit(socket , array);
                    break;
            }

        }
    }
}
