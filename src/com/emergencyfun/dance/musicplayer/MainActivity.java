/*   
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emergencyfun.dance.musicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Main activity: shows media player buttons. This activity shows the media
 * player buttons and lets the user click them. No media handling is done here
 * -- everything is done by passing Intents to our {@link MusicService}.
 * */
public class MainActivity extends Activity implements OnClickListener {
    /**
     * The URL we suggest as default when adding by URL. This is just so that
     * the user doesn't have to find an URL to test this sample.
     */

    String[] presidents = { "Dwight D. Eisenhower", "John F. Kennedy",
            "Lyndon B. Johnson", "Richard Nixon", "Gerald Ford",
            "Jimmy Carter", "Ronald Reagan", "George H. W. Bush",
            "Bill Clinton", "George W. Bush", "Barack Obama" };

    final String SUGGESTED_URL = "http://www.vorbis.com/music/Epoq-Lepidoptera.ogg";

    Button mPlayButton;
    Button mPauseButton;
    Button mSkipButton;
    Button mRewindButton;
    Button mStopButton;
    Button mEjectButton;
    
    ListView mMyList;
    
    /**
     * Called when the activity is first created. Here, we simply set the event
     * listeners and start the background service ({@link MusicService}) that
     * will handle the actual media playback.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mPlayButton = (Button) findViewById(R.id.playbutton);
        mPauseButton = (Button) findViewById(R.id.pausebutton);
        mSkipButton = (Button) findViewById(R.id.skipbutton);
        mRewindButton = (Button) findViewById(R.id.rewindbutton);
        mStopButton = (Button) findViewById(R.id.stopbutton);
        mEjectButton = (Button) findViewById(R.id.ejectbutton);

        mPlayButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);
        mSkipButton.setOnClickListener(this);
        mRewindButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        mEjectButton.setOnClickListener(this);

        mMyList = (ListView) findViewById(R.id.my_list);
        
        mMyList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, presidents));
        mMyList.setClickable(true);
        mMyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Toast.makeText(getApplicationContext(), 
                        "You have selected " + presidents[position] + "to be your mom", 
                        Toast.LENGTH_SHORT).show();
                
            }
          });
    }

    public void onClick(View target) {
        // Send the correct intent to the MusicService, according to the button
        // that was clicked
        if (target == mPlayButton)
            startService(new Intent(MusicService.ACTION_PLAY));
        else if (target == mPauseButton)
            startService(new Intent(MusicService.ACTION_PAUSE));
        else if (target == mSkipButton)
            startService(new Intent(MusicService.ACTION_SKIP));
        else if (target == mRewindButton)
            startService(new Intent(MusicService.ACTION_REWIND));
        else if (target == mStopButton)
            startService(new Intent(MusicService.ACTION_STOP));
        else if (target == mEjectButton) {
            showUrlDialog();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        
        /*menu.add(Menu.NONE, 0, Menu.NONE, R.string.settings_menu);
        menu.add(Menu.NONE, 1, Menu.NONE, R.string.quit_menu);*/
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
        case R.id.settings_menu:
            Toast.makeText(getApplicationContext(), 
                "Settings menu!", 
                Toast.LENGTH_SHORT).show();
            break;
        case R.id.quit_menu:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit?")
                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                       }
                   })
                   .setNegativeButton("No", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                       }
                   });
            builder.show();
            break;
        case R.id.add_menu:
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            
            final CharSequence[] items = {"Red", "Green", "Blue"};
            
            builder2.setTitle("Select Some Music")
                   .setItems(items, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int item) {
                           Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                       }
                   })
                   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                       }
                   });
            builder2.create();
            builder2.show();
            break;
        }
        return true;
    }

    /**
     * Shows an alert dialog where the user can input a URL. After showing the
     * dialog, if the user confirms, sends the appropriate intent to the
     * {@link MusicService} to cause that URL to be played.
     */
    void showUrlDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Manual Input");
        alertBuilder.setMessage("Enter a URL (must be http://)");
        final EditText input = new EditText(this);
        alertBuilder.setView(input);

        input.setText(SUGGESTED_URL);

        alertBuilder.setPositiveButton("Play!",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int whichButton) {
                        // Send an intent with the URL of the song to play. This
                        // is expected by
                        // MusicService.
                        Intent i = new Intent(MusicService.ACTION_URL);
                        Uri uri = Uri.parse(input.getText().toString());
                        i.setData(uri);
                        startService(i);
                    }
                });
        alertBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int whichButton) {
                    }
                });

        alertBuilder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
        case KeyEvent.KEYCODE_HEADSETHOOK:
            startService(new Intent(MusicService.ACTION_TOGGLE_PLAYBACK));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
