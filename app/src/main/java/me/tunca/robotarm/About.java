package me.tunca.robotarm;
/**
 * Created by Tunca on 05/01/2019.
 */
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class About extends Activity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        LinearLayout aboutLayout = (LinearLayout) findViewById(R.id.ll_about);
        View childAbout = getLayoutInflater().inflate(R.layout.about, null);
        aboutLayout.addView(childAbout);

        TextView textTeamInfo = (TextView) findViewById(R.id.teamInfo);
        textTeamInfo.setTextColor(getResources().getColor(R.color.colorBlack));
        textTeamInfo.setText(getResources().getString(R.string.about_string));

        int newHeight=288;
        int newWidth=288;
        ImageView imageView = (ImageView) findViewById(R.id.robotImage);
        imageView.setImageResource(R.mipmap.ic_launcher_robot);
        imageView.getLayoutParams().height=newHeight;
        imageView.getLayoutParams().width=newWidth;
    }
}