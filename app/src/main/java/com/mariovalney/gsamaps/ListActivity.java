package com.mariovalney.gsamaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by Mário Valney on 18/11/14.
 * mail: mariovalney@gmail.com
 */
public class ListActivity extends ActionBarActivity implements ListFragment.Callback {

    private boolean isTwoFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (findViewById(R.id.activity_ambassador_fragment) != null) {
            isTwoFragments = true;
            /*
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.activity_ambassador_fragment, new AmbassadorFragment())
                        .commit();
            } */
        } else {
            isTwoFragments = false;
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

    @Override
    public void onItemSelected(long id) {
        if (isTwoFragments) {

            Bundle args = new Bundle();
            args.putLong(AmbassadorActivity.INTENT_EMBAIXADOR_ID, id);

            AmbassadorFragment fragment = new AmbassadorFragment();
            fragment.setArguments(args);

            // EXTENDENDO O FRAGMENTO (IMPORTANTE!)
            int mMatchParent = ViewGroup.LayoutParams.MATCH_PARENT;

            FrameLayout frameAmbassador =
                    (FrameLayout)findViewById(R.id.activity_ambassador_fragment);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    mMatchParent, mMatchParent, 4);
            frameAmbassador.setLayoutParams(layoutParams);

            // Replace no fragmento
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_ambassador_fragment, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, AmbassadorActivity.class)
                .putExtra(AmbassadorActivity.INTENT_EMBAIXADOR_ID, id);
            startActivity(intent);
        }
    }
}
