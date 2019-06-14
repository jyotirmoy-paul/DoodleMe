package paul.cipherresfeber.doodleme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        findViewById(R.id.btnPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, MainActivity.class));
                LandingActivity.this.finish();
            }
        });

        findViewById(R.id.btnAboutProject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, AboutProjectActivity.class));
            }
        });

        findViewById(R.id.btnGithub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewOnGithub = new Intent(Intent.ACTION_VIEW);
                viewOnGithub.setData(Uri.parse("https://github.com/jyotirmoy-paul/DoodleMe"));
                startActivity(viewOnGithub);
            }
        });


    }
}
