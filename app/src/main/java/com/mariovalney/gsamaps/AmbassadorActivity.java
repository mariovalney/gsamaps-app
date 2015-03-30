package com.mariovalney.gsamaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Mário Valney on 18/11/14.
 * mail: mariovalney@gmail.com
 */
public class AmbassadorActivity extends ActionBarActivity {


    public static final String INTENT_EMBAIXADOR_ID = "ambassador_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambassador);

        if (savedInstanceState == null) {

            long id = getIntent().getLongExtra(INTENT_EMBAIXADOR_ID, 1);

            Bundle args = new Bundle();
            args.putLong(INTENT_EMBAIXADOR_ID, id);

            AmbassadorFragment fragment = new AmbassadorFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_ambassador_fragment, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.just_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, AmbassadorFragment.SHAREINFO);
            sendIntent.setType("text/plain");
            startActivity(
                    Intent.createChooser(sendIntent, getString(R.string.share_message_title)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
