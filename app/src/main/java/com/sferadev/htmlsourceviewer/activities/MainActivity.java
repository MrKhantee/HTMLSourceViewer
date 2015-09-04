package com.sferadev.htmlsourceviewer.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sferadev.htmlsourceviewer.R;
import com.sferadev.htmlsourceviewer.utils.NetworkUtils;
import com.sferadev.htmlsourceviewer.utils.PreferenceUtils;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.textView);

        loadCode();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
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

    private void loadCode() {
        textView.setText(NetworkUtils.getURLOutput(PreferenceUtils.getPreference(this,
                PreferenceUtils.PROPERTY_LAST_WEB, "https://github.com/")));
    }

    private void showDialog() {
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View myDialogView = factory.inflate(R.layout.input_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                .setTitle("RSS URL")
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

}
