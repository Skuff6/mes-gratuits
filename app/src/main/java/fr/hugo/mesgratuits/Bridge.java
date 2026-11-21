package fr.hugo.mesgratuits;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.webkit.JavascriptInterface;

/** Pont entre l'interface (index.html) et Android. */
public class Bridge {

    private final Context c;

    Bridge(Context c) {
        this.c = c.getApplicationContext();
    }

    /** Enregistre les réglages de notification et reprogramme l'alarme. */
    @JavascriptInterface
    public void reglerNotif(boolean actif, int heure, int minute, String filtresJson) {
        SharedPreferences p = c.getSharedPreferences(Planif.PREFS, 0);
        p.edit()
                .putBoolean("actif", actif)
                .putInt("h", heure)
                .putInt("m", minute)
                .putString("filtres", filtresJson)
                .apply();
        Planif.programmer(c);
    }

    @JavascriptInterface
    public void ouvrirLien(String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);
        } catch (Exception ignored) {
        }
    }

    /** Envoie une notification de test tout de suite. */
    @JavascriptInterface
    public void testerNotif() {
        new Thread(() -> Verif.lancer(c, true)).start();
    }

    @JavascriptInterface
    public boolean estAndroid() {
        return true;
    }
}
