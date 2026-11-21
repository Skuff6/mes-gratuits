# Format des données de l'appli (version 2)

L'appli a deux grandes catégories : **Jeux vidéo** (data/jeux.json) et **Offres** (data/produits.json).
Les deux fichiers sont réécrits EN ENTIER à chaque recherche (UTF-8, JSON valide, sans commentaires).
Mets TOUTES les offres actives, triées : d'abord par `acces` (direct → port → rembourse → abonnement → condition → pascher → selection → tirage), puis rareté (leg → com), puis 100 % gratuit, puis nouveautés, puis fin la plus proche.

## Le champ `acces` (OBLIGATOIRE, demande de Hugo du 21 nov.) : est-ce qu'on est SÛR de l'avoir ?

L'appli range tout par ce champ, en grands groupes séparés. Choisis-le avec soin, pour chaque offre :
- `direct` : SÛR à 100 %. Tu cliques / tu commandes, tu l'as, sans tirage, sans candidature, sans rien avancer, sans abonnement. (Jeu à garder, échantillon livré à 0 €, code qui marche pour tout le monde…)
- `port` : le produit est offert, tu paies SEULEMENT les frais de port / de livraison (indique-les dans `cout` et `coutNum`).
- `rembourse` : tu avances l'argent, puis tu es remboursé (offres « 100 % remboursé », ODR).
- `abonnement` : il faut S'ABONNER : le 1er mois (ou la 1re box, ou l'essai) est gratuit et tu reçois quelque chose, puis ça devient payant si tu ne résilies pas. Remplis OBLIGATOIREMENT `prixApres` (« 12,99 €/mois »), `resilierLe` (date ISO limite pour résilier si on la connaît en partant d'une inscription aujourd'hui, sinon null), `resilierTxt` (« avant le 21 déc. » / « avant la fin du 1er mois ») et `resilier` (comment résilier, en une phrase). L'appli affiche un gros avertissement « N'oublie pas de te désabonner » et propose un rappel.
- `condition` : tu l'as sûrement, mais avec une autre condition : achat minimum, Prime / Game Pass déjà requis, points ou missions à faire, compte à lier, déplacement en magasin.
- `pascher` : pas gratuit, mais PAS CHER et vraiment RENTABLE selon toi (grosse quantité pour le prix, -70 % et plus, box à petit prix sans engagement, erreur de prix). Dis dans `avis` pourquoi ça vaut le coup.
- `selection` : sur candidature / sélection : tu postules (test produit, panel, bêta fermée sur inscription, playtest « demander l'accès » non garanti) et ce n'est PAS sûr d'être choisi.
- `tirage` : à gagner, tirage au sort, concours, giveaway au hasard : juste une chance.

`gratuit: true` uniquement si `acces` vaut `direct` ET que ça coûte 0 € tout compris.

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
      "acces": "direct",
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
- `rayon` : bouffe, box, alcool, beaute, homme, vetements, accessoires, maison, tech, culture, applis, sport, sante, hygiene, animaux, bebe, jouets, papeterie, voyage, services, colis, abo, livraison, promo, autre.
  - `tech` = high-tech et électronique ; `culture` = livres, ebooks, BD, films, musique, presse gratuits ; `applis` = applis et logiciels payants offerts ; `livraison` = livraison gratuite ou pas chère qui vaut le coup ; `promo` = bons plans exceptionnels pas gratuits mais qui valent vraiment le coup.
- `type` : ech (échantillon), test (test produit), remb (remboursé), essai (essai gratuit), box, reduc (réduction / code), livraison, cadeau, promo.
- `cout` : coût réel tout compris en texte (« 0 € », « 4,90 € port compris »). `coutNum` : le même en nombre (0, 4.9…) ou `null` si inconnu. Sert au filtre « Coût maximum ».
- `sansCarte` : `true` si aucune carte bancaire n'est demandée.
- `qty` : quantité exacte. `contenu` : liste du contenu exact du colis. `pieges` : liste des trucs cachés.
- `rarete` : `leg` = 💎 Pépite, `epi` = ⭐ Rare, `rar` = 👍 Bon plan, `com` = Classique.
- `emoji` : un emoji du produit.

## data/theme.json

`{"nom": "...", "accent": "#22d3ee", "accent2": "#a855f7"}` : couleurs par défaut (Hugo peut choisir les siennes dans les Réglages).
