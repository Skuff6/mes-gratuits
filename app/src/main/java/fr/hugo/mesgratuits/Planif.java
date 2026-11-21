package fr.hugo.mesgratuits;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

/** Programme la vérification quotidienne (alarme). */
public class Planif {

    static final String PREFS = "mesgratuits";

    static void programmer(Context c) {
        SharedPreferences p = c.getSharedPreferences(PREFS, 0);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = intention(c, 0, 0);
        am.cancel(pi);
        if (!p.getBoolean("actif", true)) return;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, p.getInt("h", 13));
        cal.set(Calendar.MINUTE, p.getInt("m", 30));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (cal.getTimeInMillis() <= System.currentTimeMillis() + 60_000L) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        am.setWindow(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10 * 60_000L, pi);
    }

    /** Nouvel essai dans 30 minutes si les résultats du jour ne sont pas encore là. */
    static void reessayer(Context c, int essai) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        am.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 30 * 60_000L,
                5 * 60_000L, intention(c, 1, essai));
    }

    private static PendingIntent intention(Context c, int code, int essai) {
        Intent i = new Intent(c, AlarmReceiver.class);
        i.putExtra("essai", essai);
        return PendingIntent.getBroadcast(c, code, i,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
