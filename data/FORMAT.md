# Format des données de l'appli (version 2)

L'appli a deux grandes catégories : **Jeux vidéo** (data/jeux.json) et **Offres** (data/produits.json).
Les deux fichiers sont réécrits EN ENTIER à chaque recherche (UTF-8, JSON valide, sans commentaires).
Mets TOUTES les offres actives, triées : rareté (leg → com), puis 100 % gratuit d'abord, puis nouveautés, puis fin la plus proche.

## data/jeux.json — tout ce qui touche aux jeux vidéo

```json
{
  "maj": "2026-11-22T13:08:00+01:00",
  "offres": [
    {
      "id": "mark-of-the-deep-epic",
      "cat": "jeu",
      "titre": "Mark of the Deep",
      "plateforme": "epic",
      "via": "",
      "genre": "Metroidvania pirate",
      "tags": ["Solo"],
      "rarete": "epi",
      "gratuit": true,
      "prix": "24,99 €",
      "fin": "2026-11-26T17:00:00+01:00",
      "finTxt": "Jusqu'au 26 nov. 17h",
      "nouveau": false,
      "desc": "Une phrase qui donne envie.",
      "conditions": "",
      "pieges": [],
      "etapes": ["Ouvre la page", "Clique sur Obtenir"],
      "fiabilite": "Officiel",
      "lien": "https://store.epicgames.com/...",
      "avis": "Avis court de Claude."
    }
  ]
}
```

- `cat` : `jeu` (à garder), `cle` (clé de jeu), `dlc`, `code` (code en jeu), `pack` (packs/objets, Twitch Drops, Prime loot…), `beta` (bêta/playtest/démo), `essai` (week-end gratuit, essai), `avenir` (à venir).
- `plateforme` : steam, epic, gog, prime, ubi, ea, bnet, xbox, itch, humble, gala, fanat, stove, riot, rock, android, ios, ps, switch, autre. Les jeux mobiles (android/ios) vont automatiquement dans le filtre « Mobile ».
- `genre` : genre court (« FPS », « Extraction shooter », « Roguelike »…). `tags` : mots-clés courts (« Multijoueur », « Coop », « FPS », « Stock limité »…). Ils servent au filtre « FPS & multijoueur » et à la recherche.
- `rarete` : `leg` Légendaire, `epi` Épique, `rar` Rare, `com` Commun.
- `gratuit` : `true` SEULEMENT si 100 % gratuit à garder, sans abonnement, sans achat, sans condition. Sinon `false` et expliquer dans `conditions`.
- `fin` : date ISO avec fuseau si connue, sinon `null`. Les offres finies sont masquées automatiquement.
- `finTxt` : la date ou la durée en clair (« Jusqu'au 26 nov. 17h », « Stock limité »). Toujours le remplir.
- `nouveau` : `true` si l'offre n'était pas là la veille.
- `id` : stable d'un jour à l'autre pour la même offre (minuscules, tirets).

## data/produits.json — toutes les autres offres d'Internet

Même structure de base (id, titre, rarete, gratuit, fin, finTxt, nouveau, desc, conditions, pieges, etapes, lien, avis, fiabilite), avec en plus :
- `rayon` : bouffe, box, alcool, beaute, homme, vetements, maison, tech, culture, applis, sport, hygiene, animaux, bebe, colis, abo, livraison, promo, autre.
  - `tech` = high-tech et électronique ; `culture` = livres, ebooks, BD, films, musique, presse gratuits ; `applis` = applis et logiciels payants offerts ; `livraison` = livraison gratuite ou pas chère qui vaut le coup ; `promo` = bons plans exceptionnels pas gratuits mais qui valent vraiment le coup.
- `type` : ech (échantillon), test (test produit), remb (remboursé), essai (essai gratuit), box, reduc (réduction / code), livraison, cadeau, promo.
- `cout` : coût réel tout compris en texte (« 0 € », « 4,90 € port compris »). `coutNum` : le même en nombre (0, 4.9…) ou `null` si inconnu. Sert au filtre « Coût maximum ».
- `sansCarte` : `true` si aucune carte bancaire n'est demandée.
- `qty` : quantité exacte. `contenu` : liste du contenu exact du colis. `pieges` : liste des trucs cachés.
- `rarete` : `leg` = 💎 Pépite, `epi` = ⭐ Rare, `rar` = 👍 Bon plan, `com` = Classique.
- `emoji` : un emoji du produit.

## data/theme.json

`{"nom": "...", "accent": "#22d3ee", "accent2": "#a855f7"}` : couleurs par défaut (Hugo peut choisir les siennes dans les Réglages).
