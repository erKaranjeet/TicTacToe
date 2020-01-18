package bane.kjsdev.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private RewardedVideoAd mRewardedVideoAd;

    int turn = 0, steps = 0, isGameOn = 1, count = 0; //Jerry Turn if 0 else Tom Turn and Steps is counter for number of turns
    int[] game = {2, 2, 2, 2, 2, 2, 2, 2, 2}; //2->Not Played, 0->Played by Jerry, 1->Played by Tom
    String[] player = {"Player 2", "Player 1"}; //Default Player Name
    int[][] winningConditions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}}; //All Winning Conditions Grid Number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Admob ads
        MobileAds.initialize(this, getResources().getString(R.string.google_ad_app_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        loadRewardedVideoAd();

        GridLayout grid = (GridLayout) findViewById(R.id.grid);
        int length = (double) grid.getHeight() < grid.getWidth() ? grid.getHeight() : grid.getWidth();
        length = length / 3;

        for (int i = 0; i < grid.getChildCount(); i++) {
            ImageView img = (ImageView) grid.getChildAt(i);
            img.setImageResource(R.mipmap.b1);
            img.setAlpha(0f);
            img.animate().alpha(1f).setDuration(2000);
            img.getLayoutParams().height = length;
            img.getLayoutParams().width = length;
        }
    }

    public void setPlayerJerry(View view) { //Player Name Setting
        if (!((TextView) findViewById(R.id.jerryName)).getText().toString().isEmpty())
            Toast.makeText(this, "Player Name has been Set !!!", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Player Name is set to " + player[0], Toast.LENGTH_SHORT).show();
    }

    public void setPlayerTom(View view) {
        if (!((TextView) findViewById(R.id.tomName)).getText().toString().isEmpty())
            Toast.makeText(this, "Player Name has been Set !!!", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Player Name is set to " + player[1], Toast.LENGTH_SHORT).show();
    }

    public void gameOver(String msg) { //GameOver Msg
        LinearLayout gameover = (LinearLayout) findViewById(R.id.gameOver);
        TextView text = (TextView) findViewById(R.id.winnerMsg);
        text.setText(msg);
        gameover.setVisibility(View.VISIBLE);
        count++;
        if (mRewardedVideoAd.isLoaded() && count == 2) {
            count = 0;
            mRewardedVideoAd.show();
        } else {
            loadRewardedVideoAd();
        }
    }

    public void appear(View view) {//Appearance of Images on Grid Click and Game is Continue
        ImageView grid = (ImageView) view;
        int index = Integer.parseInt(view.getTag().toString());
        if (game[index] == 2 && isGameOn == 1) {
            if (turn == 0) {
                grid.setImageResource(R.mipmap.cross);
                turn = 1;
                game[index] = 0;
            } else {
                grid.setImageResource(R.mipmap.circle);
                turn = 0;
                game[index] = 1;
            }
            steps++;
            grid.setAlpha(0f);
            grid.animate().alpha(1f).setDuration(1000);
        } else if (isGameOn == 1) {
            Toast.makeText(this, "Tap on un-Played Grid !!!", Toast.LENGTH_SHORT).show();
        }

        for (int[] comp : winningConditions) { //Checking for winner
            if (game[comp[0]] == game[comp[1]] && game[comp[1]] == game[comp[2]] && game[comp[0]] != 2) {
                steps = 0;
                isGameOn = 0;
                gameOver("Congratulations " + player[game[comp[0]]] + " !!!\nYou are Winner !!!");
            }

        }
        if (isGameOn == 1 && steps == 9) gameOver("Match Drawn !!!");
    }

    public void playAgain(View view) { //Play Again Pressed
        LinearLayout gameover = (LinearLayout) findViewById(R.id.gameOver);
        gameover.setVisibility(View.INVISIBLE);
        for (int i = 0; i < 9; i++) game[i] = 2;
        isGameOn = 1;
        GridLayout grid = (GridLayout) findViewById(R.id.grid);
        for (int i = 0; i < grid.getChildCount(); i++) {
            ImageView img = (ImageView) grid.getChildAt(i);
            img.setImageResource(R.mipmap.b1);
            img.setAlpha(0f);
            img.animate().alpha(1f).setDuration(1000);
        }
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3325618649122260/1856708665",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem reward) {
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
    }

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoCompleted() {
        loadRewardedVideoAd();
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}