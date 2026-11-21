# Format des données de l'appli

Deux fichiers JSON, réécrits en entier à chaque recherche (UTF-8, JSON valide, sans commentaires).

## data/jeux.json (jeux + DLC)

```json
{
  "maj": "2026-11-21T13:05:00+01:00",
  "offres": [
    {
      "id": "alien-isolation-epic",
      "cat": "jeu",
      "titre": "Alien: Isolation",
      "plateforme": "epic",
      "via": "",
      "rarete": "leg",
      "gratuit": true,
      "prix": "39,99 €",
      "fin": "2026-11-26T17:00:00+01:00",
      "nouveau": true,
      "desc": "Phrase courte qui donne envie.",
      "conditions": "",
      "pieges": [],
      "etapes": ["Ouvre la page", "Clique sur Obtenir"],
      "fiabilite": "Officiel",
      "lien": "https://store.epicgames.com/...",
      "avis": "Avis court de Claude.",
      "emoji": ""
    }
  ]
}
```

- `cat` : `jeu` (à garder), `cle` (clé de jeu), `beta` (bêta/playtest), `essai` (à essayer, week-end gratuit), `avenir` (à venir), `dlc`, `code` (code en jeu), `pack` (packs/objets, Twitch Drops…). `dlc`, `code`, `pack` vont dans l'onglet DLC.
- `plateforme` : steam, epic, gog, prime, ubi, ea, bnet, xbox, itch, humble, gala, fanat, stove, riot, rock, autre.
- `rarete` : `leg` (Légendaire), `epi` (Épique), `rar` (Rare), `com` (Commun).
- `gratuit` : `true` seulement si c'est 100 % gratuit, à garder, sans abonnement ni condition. Sinon `false` et expliquer dans `conditions`.
- `fin` : date ISO avec fuseau, ou `null` si pas de fin connue. Les offres finies sont masquées automatiquement.
- `finTxt` : la date ou la durée en clair, comme sur la page (« Jusqu'au 26 nov. 17h », « Stock limité », « Tirage le 30 nov. »). Affichée quand `fin` est `null`.
- `nouveau` : `true` si l'offre n'était pas là la veille.
- `id` : stable d'un jour à l'autre pour la même offre (minuscules, tirets).

## data/produits.json

Même structure, avec en plus :
- `rayon` : bouffe, box, alcool, beaute, homme, vetements, hygiene, animaux, colis, abo, promo.
- `cout` : coût réel tout compris (« 0 € », « 4,90 € port »).
- `qty` : quantité exacte.
- `contenu` : liste du contenu exact du colis.
- `pieges` : liste des trucs cachés.
- `rarete` : `leg` = 💎 Pépite, `epi` = ⭐ Rare, `rar` = 👍 Bon plan, `com` = Classique.
- `cat` n'est pas utilisé.

## data/theme.json

`{"nom": "...", "accent": "#00f0ff", "accent2": "#ff2bd6"}` : couleurs par défaut de l'appli (Hugo peut les remplacer dans les Réglages).
