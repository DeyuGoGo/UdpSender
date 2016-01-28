package deyu.go.udpsender;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

//    這邊設定你們板子的IP
    private static final String SERVER_IP = "192.168.29.132";
//    這邊設定你們板子的Port
    private static final int SERVER_PORT = 8080;

//    這個字串是Commnad按鈕們會送的字串
    private final String Command1 = "Hi";
    private final String Command2 = "White shu";
    private final String Command3 = "Andy Tasi";

    private UDPSender UDPSender;
    private MainActivity self;
    public static int WHAT_TOAST_OBJECT = 0x01;

    @OnClick(R.id.button)
    public void Commnad1(){
        send(Command1);
    }
    @OnClick(R.id.button2)
    public void Commnad2(){
        send(Command2);
    }
    @OnClick(R.id.button3)
    public void Commnad3(){
        send(Command3);
    }
    @OnClick(R.id.button4)
    public void setPortIP(){
        UDPSender.setIpAndPort(et_IP.getText().toString(),Integer.valueOf(et_Port.getText().toString()));
    }
    @Bind(R.id.editText)EditText et_IP;
    @Bind(R.id.editText2)EditText et_Port;


    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(self, msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        self = this;
        UDPSender = new UDPSender(SERVER_IP, SERVER_PORT,uiHandler);
        init();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        UDPSender.close();
    }

    private void init() {
        UDPSender.init();
    }

    private void send(String s){
        transmitIntentOnBackgroundThread(s);
    }

    private void transmitIntentOnBackgroundThread(final String s) {
        UDPSender.transmit(s);
    }

}
