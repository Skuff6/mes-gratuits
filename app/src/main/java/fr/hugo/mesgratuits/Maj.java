package fr.hugo.mesgratuits;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/** Télécharge la nouvelle version et l'installe directement, sans passer par le navigateur. */
public class Maj extends BroadcastReceiver {

    static final String APK = "https://github.com/Skuff6/mes-gratuits/releases/download/appli/mes-gratuits.apk";

    static void installer(Context ctx) {
        final Context c = ctx.getApplicationContext();
        new Thread(() -> {
            try {
                PackageInstaller pi = c.getPackageManager().getPackageInstaller();
                PackageInstaller.SessionParams p = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
                int id = pi.createSession(p);
                try (PackageInstaller.Session s = pi.openSession(id)) {
                    HttpURLConnection con = (HttpURLConnection) new URL(APK).openConnection();
                    con.setInstanceFollowRedirects(true);
                    con.setConnectTimeout(20000);
                    con.setReadTimeout(60000);
                    try (InputStream in = con.getInputStream(); OutputStream out = s.openWrite("appli", 0, -1)) {
                        byte[] b = new byte[65536];
                        int n;
                        while ((n = in.read(b)) > 0) out.write(b, 0, n);
                        s.fsync(out);
                    }
                    Intent i = new Intent(c, Maj.class);
                    PendingIntent pend = PendingIntent.getBroadcast(c, id, i,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                    s.commit(pend.getIntentSender());
                }
            } catch (Exception e) {
                secours(c);
            }
        }).start();
    }

    /** Si l'installation directe échoue : on ouvre le lien de téléchargement. */
    static void secours(Context c) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                Intent v = new Intent(Intent.ACTION_VIEW, Uri.parse(APK));
                v.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(v);
            } catch (Exception ignored) {
            }
        });
    }

    @Override
    public void onReceive(Context c, Intent intent) {
        int st = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -999);
        if (st == PackageInstaller.STATUS_PENDING_USER_ACTION) {
            Intent confirmer = intent.getParcelableExtra(Intent.EXTRA_INTENT);
            if (confirmer != null) {
                confirmer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    c.startActivity(confirmer);
                    return;
                } catch (Exception ignored) {
                }
            }
            secours(c);
        } else if (st != PackageInstaller.STATUS_SUCCESS) {
            if (st == PackageInstaller.STATUS_FAILURE_ABORTED) {
                Toast.makeText(c, "Mise à jour annulée", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(c, "Installation directe impossible, j'ouvre le lien", Toast.LENGTH_SHORT).show();
                secours(c);
            }
        }
    }
}
