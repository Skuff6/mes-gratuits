package fr.hugo.mesgratuits;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class Notif {

    static final String CANAL = "offres";

    static void creerCanal(Context c) {
        NotificationChannel ch = new NotificationChannel(CANAL, "Nouvelles offres gratuites",
                NotificationManager.IMPORTANCE_HIGH);
        ch.setDescription("Prévient quand il y a de nouveaux jeux, DLC ou produits gratuits");
        c.getSystemService(NotificationManager.class).createNotificationChannel(ch);
    }

    static void afficher(Context c, List<Verif.Offre> offres, boolean test) {
        creerCanal(c);
        int jeux = 0, prod = 0;
        Verif.Offre meilleure = null;
        for (Verif.Offre o : offres) {
            if (o.onglet.equals("jeux")) jeux++;
            else prod++;
            if (meilleure == null || score(o) > score(meilleure)) meilleure = o;
        }

        String titre;
        if (offres.isEmpty()) {
            titre = "Les Tréfonds d'Internet fonctionne ✅";
        } else if (meilleure.rarete.equals("leg") && "direct".equals(meilleure.acces)) {
            titre = "⭐ Légendaire : " + meilleure.titre;
        } else {
            titre = (test ? "Test : " : "") + offres.size() + " nouveaux gratuits aujourd'hui";
        }

        List<String> parts = new ArrayList<>();
        if (jeux > 0) parts.add("🎮 " + jeux + " jeu" + (jeux > 1 ? "x" : "") + " & DLC");
        if (prod > 0) parts.add("🎁 " + prod + " offre" + (prod > 1 ? "s" : ""));
        String resume = parts.isEmpty() ? "Les notifications marchent. Rendez-vous à l'heure choisie !" : String.join(" · ", parts);

        StringBuilder detail = new StringBuilder(resume);
        List<Verif.Offre> tri = new ArrayList<>(offres);
        tri.sort((a, b) -> score(b) - score(a));
        for (int i = 0; i < Math.min(5, tri.size()); i++) {
            Verif.Offre o = tri.get(i);
            detail.append("\n• ").append(o.titre).append("direct".equals(o.acces) ? (o.gratuit ? " — sûr et gratuit" : " — sûr") : "port".equals(o.acces) ? " — juste le port" : "");
        }

        Intent ouvrir = new Intent(c, MainActivity.class);
        ouvrir.putExtra("onglet", meilleure != null ? meilleure.onglet : "jeux");
        ouvrir.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(c, 7, ouvrir,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n = new Notification.Builder(c, CANAL)
                .setSmallIcon(R.drawable.ic_notif)
                .setColor(0xFF00F0FF)
                .setContentTitle(titre)
                .setContentText(resume)
                .setStyle(new Notification.BigTextStyle().bigText(detail.toString()))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        try {
            c.getSystemService(NotificationManager.class).notify(1, n);
        } catch (SecurityException ignored) {
        }
    }

    private static int score(Verif.Offre o) {
        int sur = o.acces == null ? 0 : o.acces.equals("direct") ? 3 : o.acces.equals("port") ? 2 : o.acces.equals("rembourse") ? 1 : 0;
        return sur * 10 + Verif.rang(o.rarete) * 2 + (o.gratuit ? 1 : 0);
    }
}
