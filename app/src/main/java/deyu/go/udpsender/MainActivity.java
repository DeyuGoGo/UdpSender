package deyu.go.udpsender;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;

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
//    在一開始會初始成{0,0,0,0,0,0,0,0,0,0,0,0,0} in init();
    private final int[] arrayCommand = new int[13];

    private UDPSender UDPSender;
    private MainActivity self;
    public static final int WHAT_TOAST_OBJECT_TOSTRING = 0x01;
    public static final int WHAT_TOAST_INT_ARRAY = 0x02;

    @OnClick(R.id.btn_go)
    public void car_go(){
        arrayCommand[0] = 1;
        arrayCommand[1] = 0;
        arrayCommand[2] = 0;
        send(arrayCommand);
    }
    @OnClick(R.id.btn_stop)
    public void stop(){
        Arrays.fill(arrayCommand,0);
        send(arrayCommand);
    }
    @OnClick(R.id.btn_back)
    public void car_back(){
        arrayCommand[0] = 0;
        arrayCommand[1] = 0;
        arrayCommand[2] = 1;
        send(arrayCommand);
    }
    @OnClick(R.id.btn_plus)
    public void car_plus(){
        arrayCommand[5] += 1;
        send(arrayCommand);
    }
    @OnClick(R.id.btn_des)
    public void car_des(){
        arrayCommand[5] -= 1;
        send(arrayCommand);
    }
    @OnClick(R.id.btn_left)
    public void car_left(){
        arrayCommand[8] -= 1;
        send(arrayCommand);
    }
    @OnClick(R.id.btn_right)
    public void car_right(){
        arrayCommand[8] += 1;
        send(arrayCommand);
    }
    @OnClick(R.id.btn_test)
    public void test(){
        send(Command2);
    }
    @OnClick(R.id.btn_set)
    public void setPortIP(){
        UDPSender.setIpAndPort(et_IP.getText().toString(),Integer.valueOf(et_Port.getText().toString()));
    }
    @Bind(R.id.editText)EditText et_IP;
    @Bind(R.id.editText2)EditText et_Port;


    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_TOAST_INT_ARRAY :
                    Toast.makeText(self,Arrays.toString((int[])msg.obj), Toast.LENGTH_SHORT).show();
                    break;
                case WHAT_TOAST_OBJECT_TOSTRING:
                    Toast.makeText(self,msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
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
        Arrays.fill(arrayCommand,0);
    }

    private void send(String s){
        transmitIntentOnBackgroundThread(s);
    }

    private void send(int[] s){
        transmitIntentOnBackgroundThread(s);
    }

    private void transmitIntentOnBackgroundThread(final String s) {
        UDPSender.transmit(s);
    }
    private void transmitIntentOnBackgroundThread(final int[] s) {
        UDPSender.transmit(s);
    }

}
