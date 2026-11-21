package fr.hugo.mesgratuits;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Planif.programmer(context.getApplicationContext());
        Rappel.reprogrammerTout(context.getApplicationContext());
    }
}
