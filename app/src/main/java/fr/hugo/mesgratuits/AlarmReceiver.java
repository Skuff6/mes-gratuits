package fr.hugo.mesgratuits;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Context c = context.getApplicationContext();
        final int essai = intent.getIntExtra("essai", 0);
        final PendingResult res = goAsync();
        new Thread(() -> {
            try {
                boolean aJour = Verif.lancer(c, false);
                if (!aJour && essai < 4) Planif.reessayer(c, essai + 1);
            } finally {
                if (essai == 0) Planif.programmer(c);
                res.finish();
            }
        }).start();
    }
}
