package me.tunca.robotarm;
/**
 * Created by Tunca on 05/01/2019.
 */
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import java.util.Objects;
import me.aflak.bluetooth.Bluetooth;


public class MainActivity extends AppCompatActivity implements Bluetooth.CommunicationCallback
{
    public static Locale locale = Locale.getDefault();
    ImageButton buttonUp, buttonDown, buttonForward, buttonBack, buttonRight, buttonLeft, buttonClmpOn, buttonClmpOff;
    private Bluetooth btDevice;
    private TextView display;
    private boolean registered = false;
    public Runnable mAction;
    private Handler mHandler;
    int delayMilis = 16;
    int postDelayed = 16;
    String comingData;
    ProgressBar progressBar;
    TextView textView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = (TextView) findViewById(R.id.display);
        display.setMovementMethod(new ScrollingMovementMethod());

        btDevice = new Bluetooth(this);
        btDevice.enableBluetooth();

        btDevice.setCommunicationCallback(this);

        int pos = Objects.requireNonNull(getIntent().getExtras()).getInt("pos");

        Display(getResources().getString(R.string.connect));
        Display(getResources().getString(R.string.info_btn));
        btDevice.connectToDevice(btDevice.getPairedDevices().get(pos));

        // Battery
        LinearLayout battery = (LinearLayout) findViewById(R.id.battery);
        @SuppressLint("InflateParams") View childBattery = getLayoutInflater().inflate(R.layout.battery, null);
        assert battery != null;
        battery.addView(childBattery);
        progressBar = (ProgressBar) findViewById(R.id.prgBar);
        final TextView textViewPercent = (TextView) findViewById(R.id.prgBarText_percent);
        textView = (TextView) findViewById(R.id.prgBarText);
        if (!btDevice.isConnected())
        {
            progressBar.setProgress(0);
            textView.setText(getResources().getString(R.string.data_wait));
        }

        assert textViewPercent != null;
        textViewPercent.setText(progressBar.getProgress() + "%");
        if (progressBar.getProgress() >= 75 && progressBar.getProgress() <= 100)
        {
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.battery_bar_green));
        }
        else if (progressBar.getProgress() < 75 && progressBar.getProgress() >= 50)
        {
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.battery_bar_yellow));
        }
        else if (progressBar.getProgress() < 50 && progressBar.getProgress() >= 25)
        {
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.battery_bar_orange));
        }
        else if (progressBar.getProgress() < 25 && progressBar.getProgress() >= 0)
        {
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.battery_bar_red));
        }


        // Control page of servo motors, button assignments and image insertion
        LinearLayout servo = (LinearLayout) findViewById(R.id.buttons);
        @SuppressLint("InflateParams") View childServos = getLayoutInflater().inflate(R.layout.buttons, null);
        if (servo != null)
        {
            servo.addView(childServos);
        }
        else
        {
            Log.e("TAG-MA", "Error adding image cannot insert servo control image");
        }
        buttonUp = (ImageButton) childServos.findViewById(R.id.button_up);

        buttonUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                btDevice.send("1");
                Display(getResources().getString(R.string.move_up));
                Log.e("TAG-MA", "up : 1");
            }
        });

        buttonUp.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        buttonClickable(1, true);
                        sendDataAction("1", 1);
                        Log.e("Data :", "Veri : 1");
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, postDelayed);
                        break;

                    case MotionEvent.ACTION_UP:
                        if (mHandler == null)
                        {
                            return true;
                        }
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        buttonClickable(0, true);
                        break;
                }
                return false;
            }
        });

        buttonDown = (ImageButton) childServos.findViewById(R.id.button_down);

        buttonDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                btDevice.send("2");
                Display(getResources().getString(R.string.move_down));
                Log.e("TAG-MA", "down : 2");
            }
        });

        buttonDown.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        buttonClickable(2, true);
                        sendDataAction("2", 2);
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, postDelayed);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null)
                        {
                            return true;
                        }
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        buttonClickable(0, true);
                        break;

                }
                return false;
            }
        });


        buttonForward = (ImageButton) childServos.findViewById(R.id.button_forward);

        buttonForward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                btDevice.send("3");
                Display(getResources().getString(R.string.move_forward));
                Log.e("TAG-MA", "forward : 3");
            }
        });

        buttonForward.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:

                        buttonClickable(3, true);
                        sendDataAction("3", 3);
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, postDelayed);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null)
                        {
                            return true;
                        }
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        buttonClickable(0, true);
                        break;

                }
                return false;
            }
        });

        buttonBack = (ImageButton) childServos.findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                btDevice.send("4");
                Display(getResources().getString(R.string.move_backward));
                Log.e("TAG-MA", "back : 4");
            }
        });

        buttonBack.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        buttonClickable(4, true);
                        sendDataAction("4", 4);
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, postDelayed);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null)
                        {
                            return true;
                        }
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        buttonClickable(0, true);
                        break;
                }
                return false;
            }
        });

        buttonRight = (ImageButton) childServos.findViewById(R.id.button_right);

        buttonRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                btDevice.send("5");
                Display(getResources().getString(R.string.turn_right));
                Log.e("TAG-MA", "right : 5");
            }
        });

        buttonRight.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        buttonClickable(5, true);
                        sendDataAction("5", 5);
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, postDelayed);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null)
                        {
                            return true;
                        }
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        buttonClickable(0, true);
                        break;
                }
                return false;
            }
        });

        buttonLeft = (ImageButton) childServos.findViewById(R.id.button_left);

        buttonLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                btDevice.send("6");
                Display(getResources().getString(R.string.turn_left));
                Log.e("TAG-MA", "left : 6");
            }
        });

        buttonLeft.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:

                        buttonClickable(6, true);
                        sendDataAction("6", 6);
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, postDelayed);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null)
                        {
                            return true;
                        }
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        buttonClickable(0, true);
                        break;

                }
                return false;
            }
        });

        buttonClmpOn = (ImageButton) childServos.findViewById(R.id.button_clampon);
        buttonClmpOn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                btDevice.send("7");
                Display(getResources().getString(R.string.clamp_on));
                Log.e("TAG-MA", "clmpOn : 7");
                // operation =-1;

            }
        });

        buttonClmpOn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        buttonClickable(7, true);
                        sendDataAction("7", 7);

                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, postDelayed);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null)
                        {
                            return true;
                        }
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        buttonClickable(0, true);
                        break;

                }
                return false;
            }
        });

        buttonClmpOff = (ImageButton) childServos.findViewById(R.id.button_clampoff);
        buttonClmpOff.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                btDevice.send("8");
                Display(getResources().getString(R.string.clamp_off));
                Log.e("TAG-MA", "clmpOff : 8");

            }
        });

        buttonClmpOff.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:

                        buttonClickable(8, true);
                        sendDataAction("8", 8);
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, postDelayed);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null)
                        {
                            return true;
                        }
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        buttonClickable(0, true);
                        break;
                }
                return false;
            }
        });

        Button clearButton = (Button) findViewById(R.id.button_clear);
        assert clearButton != null;
        clearButton.setText(getResources().getString(R.string.clear_feedback));
        clearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                display.setText("");
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.clear_info_toast), Toast.LENGTH_SHORT).show();
            }
        });
        buttonClickable(0, false);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        registered = true;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (registered)
        {
            unregisterReceiver(mReceiver);
            registered = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.close:
                btDevice.removeCommunicationCallback();
                btDevice.disconnect();
                Intent intentSelect = new Intent(this, Select.class);
                startActivity(intentSelect);
                finish();
                return true;
            case R.id.about:
                Intent intentAbout = new Intent(this, About.class);
                startActivity(intentAbout);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void Display(final String s)
    {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                display.append("\n");
                display.append(s);
            }
        });
    }


    // 0    Black (Default)
    // 1    Green (for Device İnfo)
    // 2    Red (for Error)
    // 3    Mavi (for Callback)
    public void Display(final String s, final int color, final int start)
    {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                SpannableString ss = new SpannableString(s);
                if (color == 1)
                {
                    ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGreen)), start, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else if (color == 2)
                {
                    ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorRed)), start, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else if (color == 3)
                {
                    ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorBlue)), start, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else
                {
                    ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorBlack)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                display.append("\n");
                display.append(ss);
            }
        });
    }

    @Override
    public void onConnect(BluetoothDevice device)
    {
        String s = getResources().getString(R.string.connect_device) + device.getName() + " - " + device.getAddress();
        Display(s, 1, 19);
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                buttonClickable(0, true);
            }
        });
    }


    @Override
    public void onDisconnect(BluetoothDevice device, String message)
    {
        Display(getResources().getString(R.string.disconnect));
        Display(getResources().getString(R.string.reconnect));
        Display(getResources().getString(R.string.btn_inactive));
        btDevice.connectToDevice(device);
        textView.setText(getResources().getString(R.string.connect_wait));
        Log.e("TAG-Message", "onDisconnect Message : " + message);
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                buttonClickable(0, false);
            }
        });

    }

    @Override
    public void onMessage(String message)
    {
        comingData = message;
        // As soon as the message arrives, the data belongs to the battery.
        progressBar.setProgress(Integer.parseInt(comingData));
        textView.setText(getResources().getString(R.string.data_process));
        Log.e("TAG-Message", "onMassage Message : " + message);
    }

    @Override
    public void onError(String message)
    {
        Display(getResources().getString(R.string.error) + message);
    }

    @Override
    public void onConnectError(final BluetoothDevice device, String message)
    {
        String s = getResources().getString(R.string.error) + message;
        Display(s, 2, 6);
        Display(getResources().getString(R.string.retrying));
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btDevice.connectToDevice(device);
                    }
                }, 2000);
            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            assert action != null;
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Intent intent1 = new Intent(MainActivity.this, Select.class);

                switch (state)
                {
                    case BluetoothAdapter.STATE_OFF:
                        if (registered)
                        {
                            unregisterReceiver(mReceiver);
                            registered = false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if (registered)
                        {
                            unregisterReceiver(mReceiver);
                            registered = false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                }
            }
        }
    };

    public void sendDataAction(final String s, final int pos)
    {
        mAction = new Runnable()
        {
            @Override
            public void run()
            {
                btDevice.send(s);
                displayData(pos);
                mHandler.postDelayed(this, delayMilis);
            }
        };
    }

    public void displayData(int s)
    {
        switch (s)
        {
            case 1:
                Display(getResources().getString(R.string.move_up));
                break;
            case 2:
                Display(getResources().getString(R.string.move_down));
                break;
            case 3:
                Display(getResources().getString(R.string.move_forward));
                break;
            case 4:
                Display(getResources().getString(R.string.move_backward));
                break;
            case 5:
                Display(getResources().getString(R.string.turn_right));
                break;
            case 6:
                Display(getResources().getString(R.string.turn_left));
                break;
            case 7:
                Display(getResources().getString(R.string.clamp_on));
                break;
            case 8:
                Display(getResources().getString(R.string.clamp_off));
                break;
        }
    }

    public void buttonClickable(int pos, boolean bool)
    {

        if (0 < pos && pos > 8)
        {
            pos = 0;
        }
        else if (pos == 0)
        {
            buttonUp.setEnabled(bool);
            buttonDown.setEnabled(bool);
            buttonForward.setEnabled(bool);
            buttonBack.setEnabled(bool);
            buttonRight.setEnabled(bool);
            buttonLeft.setEnabled(bool);
            buttonClmpOn.setEnabled(bool);
            buttonClmpOff.setEnabled(bool);
        }
        else
        {
            switch (pos)
            {
                case 1:
                    buttonUp.setEnabled(bool);
                    buttonDown.setEnabled(false);
                    buttonForward.setEnabled(false);
                    buttonBack.setEnabled(false);
                    buttonRight.setEnabled(false);
                    buttonLeft.setEnabled(false);
                    buttonClmpOn.setEnabled(false);
                    buttonClmpOff.setEnabled(false);
                    break;
                case 2:
                    buttonUp.setEnabled(false);
                    buttonDown.setEnabled(bool);
                    buttonForward.setEnabled(false);
                    buttonBack.setEnabled(false);
                    buttonRight.setEnabled(false);
                    buttonLeft.setEnabled(false);
                    buttonClmpOn.setEnabled(false);
                    buttonClmpOff.setEnabled(false);
                    break;
                case 3:
                    buttonUp.setEnabled(false);
                    buttonDown.setEnabled(false);
                    buttonForward.setEnabled(bool);
                    buttonBack.setEnabled(false);
                    buttonRight.setEnabled(false);
                    buttonLeft.setEnabled(false);
                    buttonClmpOn.setEnabled(false);
                    buttonClmpOff.setEnabled(false);
                    break;
                case 4:
                    buttonUp.setEnabled(false);
                    buttonDown.setEnabled(false);
                    buttonForward.setEnabled(false);
                    buttonBack.setEnabled(bool);
                    buttonRight.setEnabled(false);
                    buttonLeft.setEnabled(false);
                    buttonClmpOn.setEnabled(false);
                    buttonClmpOff.setEnabled(false);
                    break;
                case 5:
                    buttonUp.setEnabled(false);
                    buttonDown.setEnabled(false);
                    buttonForward.setEnabled(false);
                    buttonBack.setEnabled(false);
                    buttonRight.setEnabled(bool);
                    buttonLeft.setEnabled(false);
                    buttonClmpOn.setEnabled(false);
                    buttonClmpOff.setEnabled(false);
                    break;
                case 6:
                    buttonUp.setEnabled(false);
                    buttonDown.setEnabled(false);
                    buttonForward.setEnabled(false);
                    buttonBack.setEnabled(false);
                    buttonRight.setEnabled(false);
                    buttonLeft.setEnabled(bool);
                    buttonClmpOn.setEnabled(false);
                    buttonClmpOff.setEnabled(false);
                    break;
                case 7:
                    buttonUp.setEnabled(false);
                    buttonDown.setEnabled(false);
                    buttonForward.setEnabled(false);
                    buttonBack.setEnabled(false);
                    buttonRight.setEnabled(false);
                    buttonLeft.setEnabled(false);
                    buttonClmpOn.setEnabled(bool);
                    buttonClmpOff.setEnabled(false);
                    break;
                case 8:
                    buttonUp.setEnabled(false);
                    buttonDown.setEnabled(false);
                    buttonForward.setEnabled(false);
                    buttonBack.setEnabled(false);
                    buttonRight.setEnabled(false);
                    buttonLeft.setEnabled(false);
                    buttonClmpOn.setEnabled(false);
                    buttonClmpOff.setEnabled(bool);
                    break;
            }
        }

    }


}
