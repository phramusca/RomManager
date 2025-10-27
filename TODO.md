# État des lieux - Champs Game dans RomManager

## Source de référence

Code source gamelist.xml Recalbox :

- https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.cpp
- https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.h

## Classe Game.java / gamelist.xml

Voici la cible à implémenter, si ce n'est déjà fait

| Champ XML          | Type Java | Type champ     | Lecture XML | Écriture XML | Mod. ODS par GUI | Modif Recalbox  | Règle fusion | Utilisation                         |
| ------------------ | --------- | -------------- | ----------- | ------------ | ---------------- | --------------- | ------------ | ----------------------------------- |
| path               | String    | File info      | ✅          | ❌           | ❌               | ❌              | Recalbox     | Chemin du fichier ROM               |
| hash               | String    | File info      | ✅          | ❌           | ❌               | ❌              | Recalbox     | Hash CRC32 du ROM                   |
| playcount          | int       | User Stats     | ✅          | ❌           | ❌               | ❌              | Recalbox     | Nombre de parties jouées            |
| lastplayed         | String    | User Stats     | ✅          | ❌           | ❌               | ❌              | Recalbox     | Dernière fois joué                  |
| timeplayed         | int       | User Stats     | ✅          | ❌           | ✅               | ❌              | Maximum      | Temps total de jeu (en secondes)    |
| favorite           | boolean   | User           | ✅          | ✅           | ✅               | ✅              | Plus récent  | Jeu favori (préférence utilisateur) |
| hidden             | boolean   | User           | ✅          | ✅           | ✅               | ✅              | Plus récent  | Jeu caché (préférence utilisateur)  |
| adult              | boolean   | Scrappé / User | ✅          | ✅           | ✅               | ✅              | Plus récent  | Jeu adulte (préférence utilisateur) |
| name               | String    | Scrappé / User | ✅          | ❌           | ✅               | ✅              | Plus récent  | Nom du jeu                          |
| desc               | String    | Scrappé        | ✅          | ❌           | ❌               | ✅              | Recalbox     | Description                         |
| rating             | float     | Scrappé        | ✅          | ❌           | ❌               | ✅              | Recalbox     | Note/évaluation                     |
| image              | String    | Scrappé        | ✅          | ❌           | ❌               | ❌              | Recalbox     | Chemin de l'image                   |
| thumbnail          | String    | Scrappé        | ✅          | ❌           | ❌               | ❌              | Recalbox     | Chemin du thumbnail                 |
| video              | String    | Scrappé        | ✅          | ❌           | ❌               | ❌              | Recalbox     | Chemin de la vidéo                  |
| releasedate        | String    | Scrappé        | ✅          | ❌           | ❌               | ❌              | Recalbox     | Date de sortie                      |
| developer          | String    | Scrappé        | ✅          | ❌           | ❌               | ❌              | Recalbox     | Développeur                         |
| publisher          | String    | Scrappé        | ✅          | ❌           | ❌               | ❌              | Recalbox     | Éditeur                             |
| genre              | String    | Scrappé        | ✅          | ❌           | ❌               | ❌              | Recalbox     | Genre                               |
| genreid            | String    | Scrappé        | ✅          | ❌           | ❌               | ✅ (ou genre ?) | Recalbox     | ID du genre                         |
| players            | String    | Scrappé        | ✅          | ❌           | ❌               | ❌              | Recalbox     | Nombre de joueurs                   |
| region             | String    | Scrappé        | ✅          | ❌           | ❌               | ❌              | Recalbox     | Région                              |
| ratio              | String    | Scrappé        | ✅          | ❌           | ❌               | ✅ (marche ?)   | Recalbox     | Ratio d'écran                       |
| emulator           | -         |                | ❌          | ❌           | ❌               | ✅              | -            | Émulateur                           |
| core               | -         |                | ❌          | ❌           | ❌               | ✅              | -            | Core de l'émulateur                 |
| rotation           | -         |                | ❌          | ❌           | ❌               | ✅              | -            | Rotation de l'écran                 |
| lastPatch          | -         |                | ❌          | ❌           | ❌               | ???             | -            | Dernier patch appliqué              |
| lightgunluminosity | -         |                | ❌          | ❌           | ❌               | ???             | -            | Luminosité du lightgun              |
| aliases            | -         |                | ❌          | ❌           | ❌               | ???             | -            | Alias du jeu                        |
| licences           | -         |                | ❌          | ❌           | ❌               | ???             | -            | Licences                            |
| timestamp          | long      | Scrap info     | ✅          | ❌           | ❌               | ❌              | Recalbox     | Timestamp du scrap seulement !      |
| source             | -         | Scrap info     | ❌          | ❌           | ❌               | ❌              | -            | Toujours "Recalbox" (attribut).     |
