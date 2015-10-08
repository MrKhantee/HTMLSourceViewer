package com.sferadev.htmlsourceviewer.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sferadev.htmlsourceviewer.R;
import com.sferadev.htmlsourceviewer.utils.NetworkUtils;
import com.sferadev.htmlsourceviewer.utils.PreferenceUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.textView);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = null;
        if (searchItem != null) searchView = (SearchView) searchItem.getActionView();
        if (searchView != null)
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_open) {
            showDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            loadCode();
        } else {
            finish();
        }
    }

    private void loadCode() {
        Document document = Jsoup.parse(NetworkUtils.getURLOutput(PreferenceUtils.getPreference(this,
                PreferenceUtils.PROPERTY_LAST_WEB, "https://github.com/")));
        textView.setText(document.outerHtml());
    }

    private void loadCode(String search) {
        Document document = Jsoup.parse(NetworkUtils.getURLOutput(PreferenceUtils.getPreference(this,
                PreferenceUtils.PROPERTY_LAST_WEB, "https://github.com/")));
        if (document.select(search).hasText()) {
            textView.setText(document.select(search).outerHtml());
        } else {
            Toast.makeText(this, getString(R.string.search_error), Toast.LENGTH_LONG).show();
        }
    }

    private void showDialog() {
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View myDialogView = factory.inflate(R.layout.input_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                .setTitle("URL")
                .setView(myDialogView)
                .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText urlView = (EditText) myDialogView.findViewById(R.id.dialog_input);
                        String url = urlView.getText().toString();
                        if (!url.startsWith("http")) url = "http://" + url;
                        PreferenceUtils.setPreference(MainActivity.this,
                                PreferenceUtils.PROPERTY_LAST_WEB, url);
                        loadCode();
                    }
                })
                .setNegativeButton(getString(android.R.string.no), null)
                .create();
        dialog.show();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            loadCode(query);
        } else {
            loadCode();
        }
    }

}
