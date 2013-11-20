package com.joshafeinberg.minesweeper;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class HighScoreBackup extends BackupAgentHelper {

    static final String PREFS = MainActivity.sharedprefs;
    static final String MY_PREFS_BACKUP_KEY = "prefs";

    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper =
                new SharedPreferencesBackupHelper(this, PREFS);
        addHelper(MY_PREFS_BACKUP_KEY, helper);
    }

}