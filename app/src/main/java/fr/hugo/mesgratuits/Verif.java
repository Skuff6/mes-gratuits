package fr.hugo.mesgratuits;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Va chercher les résultats du jour sur GitHub et prévient s'il y a du nouveau. */
public class Verif {

    static final String BASE = "https://raw.githubusercontent.com/Skuff6/mes-gratuits/main/data/";

    static class Offre {
        String id, titre, onglet, rarete;
        boolean gratuit;
    }

    /** @return true si les résultats du jour ont bien été trouvés. */
    static boolean lancer(Context c, boolean test) {
        SharedPreferences p = c.getSharedPreferences(Planif.PREFS, 0);
        JSONObject filtres;
        try {
            filtres = new JSONObject(p.getString("filtres", "{}"));
        } catch (Exception e) {
            filtres = new JSONObject();
        }
        String aujourdhui = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(new Date());

        List<Offre> toutes = new ArrayList<>();
        boolean aJour = false;
        boolean ok = false;
        for (String f : new String[]{"jeux.json", "produits.json"}) {
            try {
                JSONObject d = new JSONObject(lire(BASE + f + "?t=" + System.currentTimeMillis()));
                ok = true;
                if (d.optString("maj", "").startsWith(aujourdhui)) aJour = true;
                JSONArray a = d.optJSONArray("offres");
                if (a == null) continue;
                for (int i = 0; i < a.length(); i++) {
                    JSONObject o = a.getJSONObject(i);
                    Offre x = new Offre();
                    x.id = f + ":" + o.optString("id");
                    x.titre = o.optString("titre");
                    x.rarete = o.optString("rarete", "com");
                    x.gratuit = o.optBoolean("gratuit", false);
                    String cat = o.optString("cat", "");
                    if (f.startsWith("produits")) x.onglet = "produits";
                    else if (cat.equals("dlc") || cat.equals("code") || cat.equals("pack")) x.onglet = "dlc";
                    else x.onglet = "jeux";
                    toutes.add(x);
                }
            } catch (Exception ignored) {
            }
        }
        if (!ok) return false;

        Set<String> connus = new HashSet<>(p.getStringSet("connus", new HashSet<>()));
        int min = rang(filtres.optString("rarete", "com"));
        List<Offre> nouvelles = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        for (Offre o : toutes) {
            ids.add(o.id);
            if (connus.contains(o.id)) continue;
            if (!filtres.optBoolean(o.onglet, true)) continue;
            if (rang(o.rarete) < min) continue;
            nouvelles.add(o);
        }
        p.edit().putStringSet("connus", ids).apply();

        if (test) {
            Notif.afficher(c, toutes, true);
        } else if (!nouvelles.isEmpty()) {
            Notif.afficher(c, nouvelles, false);
        }
        return aJour;
    }

    static int rang(String r) {
        switch (r) {
            case "leg": return 3;
            case "epi": return 2;
            case "rar": return 1;
            default: return 0;
        }
    }

    private static String lire(String adresse) throws Exception {
        HttpURLConnection h = (HttpURLConnection) new URL(adresse).openConnection();
        h.setConnectTimeout(15000);
        h.setReadTimeout(20000);
        h.setUseCaches(false);
        try (BufferedReader r = new BufferedReader(new InputStreamReader(h.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String l;
            while ((l = r.readLine()) != null) sb.append(l).append('\n');
            return sb.toString();
        } finally {
            h.disconnect();
        }
    }
}
