package fr.hugo.mesgratuits;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

/** Rappels « pense à résilier ton abonnement » : programmés, gardés après un redémarrage. */
public class Rappel extends BroadcastReceiver {

    static final String CANAL = "rappels";

    static void programmer(Context c, String id, String titre, String texte, long quand) {
        SharedPreferences p = c.getSharedPreferences(Planif.PREFS, 0);
        try {
            JSONArray a = new JSONArray(p.getString("rappels", "[]"));
            JSONArray b = new JSONArray();
            for (int i = 0; i < a.length(); i++) {
                JSONObject o = a.getJSONObject(i);
                if (!o.optString("id").equals(id) && o.optLong("quand") > System.currentTimeMillis()) b.put(o);
            }
            b.put(new JSONObject().put("id", id).put("titre", titre).put("texte", texte).put("quand", quand));
            p.edit().putString("rappels", b.toString()).apply();
        } catch (Exception ignored) {
        }
        alarme(c, id, titre, texte, quand);
    }

    static boolean existe(Context c, String id) {
        try {
            JSONArray a = new JSONArray(c.getSharedPreferences(Planif.PREFS, 0).getString("rappels", "[]"));
            for (int i = 0; i < a.length(); i++) {
                JSONObject o = a.getJSONObject(i);
                if (o.optString("id").equals(id) && o.optLong("quand") > System.currentTimeMillis()) return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    static void reprogrammerTout(Context c) {
        try {
            JSONArray a = new JSONArray(c.getSharedPreferences(Planif.PREFS, 0).getString("rappels", "[]"));
            for (int i = 0; i < a.length(); i++) {
                JSONObject o = a.getJSONObject(i);
                if (o.optLong("quand") > System.currentTimeMillis())
                    alarme(c, o.optString("id"), o.optString("titre"), o.optString("texte"), o.optLong("quand"));
            }
        } catch (Exception ignored) {
        }
    }

    private static void alarme(Context c, String id, String titre, String texte, long quand) {
        Intent i = new Intent(c, Rappel.class);
        i.putExtra("titre", titre);
        i.putExtra("texte", texte);
        PendingIntent pi = PendingIntent.getBroadcast(c, 1000 + Math.abs(id.hashCode() % 100000), i,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        am.setWindow(AlarmManager.RTC_WAKEUP, quand, 30 * 60_000L, pi);
    }

    @Override
    public void onReceive(Context c, Intent intent) {
        NotificationManager nm = c.getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel(CANAL, "Rappels de résiliation",
                NotificationManager.IMPORTANCE_HIGH));
        Intent ouvrir = new Intent(c, MainActivity.class);
        ouvrir.putExtra("onglet", "offres");
        ouvrir.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(c, 8, ouvrir,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        String texte = intent.getStringExtra("texte");
        Notification n = new Notification.Builder(c, CANAL)
                .setSmallIcon(R.drawable.ic_notif)
                .setColor(0xFFF43F5E)
                .setContentTitle("⚠️ " + intent.getStringExtra("titre"))
                .setContentText(texte)
                .setStyle(new Notification.BigTextStyle().bigText(texte))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        try {
            nm.notify((int) (System.currentTimeMillis() % 100000) + 100, n);
        } catch (SecurityException ignored) {
        }
    }
}
