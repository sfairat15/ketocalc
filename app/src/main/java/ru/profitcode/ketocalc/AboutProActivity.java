package ru.profitcode.ketocalc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class AboutProActivity extends BaseBannerAdvActivity implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerFragment playerFragment;
    private YouTubePlayer mPlayer;
    private String YouTubeKey = "AIzaSyBzWve6WqjAl9u9qd_OiZvjsz9tc-O56zI";
    private Button mInstallPro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInstallPro = findViewById(R.id.install_pro_btn);
        mInstallPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=ru.profitcode.ketocalcpropaid&referrer=utm_source%3Dfreeversion%26utm_medium%3Dbutton"));

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    try {
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=ru.profitcode.ketocalcpropaid&referrer=utm_source%3Dfreeversion%26utm_medium%3Dbutton"));
                        startActivity(intent);
                    } catch (Exception ex) {
                        Toast.makeText(v.getContext(),
                                v.getResources().getString(R.string.install_pro_error, e.getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        playerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_player_fragment);

        playerFragment.initialize(YouTubeKey, this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_about_pro;
    }

    @Override
    protected String getAdUnitId() {
        return "ca-app-pub-9772487056729057/4337561452";
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        mPlayer = player;

        //Enables automatic control of orientation
        mPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);

        //Show full screen in landscape mode always
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);

        //System controls will appear automatically
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);

        if (!wasRestored) {

            //show thumbnail
            //mPlayer.cueVideo("GqQNQlfxqtY");

            //instead of URL https://www.youtube.com/watch?reload=9&v=GqQNQlfxqtY&feature=youtu.be
            //we should use only v parameter value
            //start playing at activity start
            mPlayer.loadVideo("GqQNQlfxqtY");
        }
        else
        {
            mPlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        mPlayer = null;
    }
}
