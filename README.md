# Mes gratuits

L'appli Android perso de Hugo : jeux, DLC et produits gratuits trouvés chaque jour par Claude.

**Installer / mettre à jour l'appli :** sur le téléphone, ouvre
https://github.com/Skuff6/mes-gratuits/releases/download/appli/mes-gratuits.apk
puis touche « Installer ».

## Comment ça marche

- Chaque jour à 13 h, Claude fait la recherche et met à jour `data/jeux.json` et `data/produits.json`.
- L'appli lit ces fichiers et envoie une notification à l'heure choisie dans les Réglages.
- `data/theme.json` contient les couleurs proposées par Claude.
- À chaque changement du code (`app/`), GitHub fabrique une nouvelle version de l'appli.

Le format des données est décrit dans `data/FORMAT.md`.
