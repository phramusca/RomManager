# État des lieux - Champs Game dans RomManager

## Source de référence

Code source gamelist.xml Recalbox :

- https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.cpp
- https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.h

## État actuel dans RomManager

### Classe Game.java - Champs supportés

| Champ XML          | Type Java | Type champ     | Lecture XML | Écriture XML | Mod. ODS par GUI | Modif Recalbox   | Règle fusion | Utilisation                         |
| ------------------ | --------- | -------------- | ----------- | ------------ | ---------------- | ---------------- | ------------ | ----------------------------------- |
| path               | String    | File info      | ✅          | ❌           | ❌               | Non              | Base         | Chemin du fichier ROM               |
| hash               | String    | File info      | ✅          | ❌           | ❌               | Non              | Base         | Hash CRC32 du ROM                   |
| playcount          | int       | User Stats     | ✅          | ❌           | ❌               | Non              | Maximum      | Nombre de parties jouées            |
| lastplayed         | String    | User Stats     | ✅          | ❌           | ❌               | Non              | Plus récent  | Dernière fois joué                  |
| timeplayed         | int       | User Stats     | ✅          | ❌           | ✅               | Non              | -            | Temps total de jeu (en secondes)    |
| favorite           | boolean   | User           | ✅          | ✅           | ✅               | Oui              | Local        | Jeu favori (préférence utilisateur) |
| hidden             | boolean   | User           | ✅          | ✅           | ✅               | Oui              | Local        | Jeu caché (préférence utilisateur)  |
| adult              | boolean   | Scrappé / User | ✅          | ✅           | ✅               | Oui              | Local        | Jeu adulte (préférence utilisateur) |
| name               | String    | Scrappé / User | ✅          | ❌           | ✅               | Oui              | Base         | Nom du jeu                          |
| desc               | String    | Scrappé        | ✅          | ❌           | ❌               | Oui              | Plus complet | Description                         |
| rating             | float     | Scrappé        | ✅          | ❌           | ❌               | Oui              | Plus élevé   | Note/évaluation                     |
| image              | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Plus complet | Chemin de l'image                   |
| thumbnail          | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Plus complet | Chemin du thumbnail                 |
| video              | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Plus complet | Chemin de la vidéo                  |
| releasedate        | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Plus récent  | Date de sortie                      |
| developer          | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Plus complet | Développeur                         |
| publisher          | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Plus complet | Éditeur                             |
| genre              | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Plus complet | Genre                               |
| genreid            | String    | Scrappé        | ✅          | ❌           | ❌               | Oui (ou genre ?) | Base         | ID du genre                         |
| players            | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Plus complet | Nombre de joueurs                   |
| region             | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Plus complet | Région                              |
| ratio              | String    | Scrappé        | ✅          | ❌           | ❌               | Oui (marche ?)   | Plus complet | Ratio d'écran                       |
| emulator           | -         |                | ❌          | ❌           | ❌               | Oui              | -            | Émulateur                           |
| core               | -         |                | ❌          | ❌           | ❌               | Oui              | -            | Core de l'émulateur                 |
| rotation           | -         |                | ❌          | ❌           | ❌               | Oui              | -            | Rotation de l'écran                 |
| lastPatch          | -         |                | ❌          | ❌           | ❌               | ???              | -            | Dernier patch appliqué              |
| lightgunluminosity | -         |                | ❌          | ❌           | ❌               | ???              | -            | Luminosité du lightgun              |
| aliases            | -         |                | ❌          | ❌           | ❌               | ???              | -            | Alias du jeu                        |
| licences           | -         |                | ❌          | ❌           | ❌               | ???              | -            | Licences                            |
| timestamp          | long      | Scrap info     | ✅          | ❌           | ❌               | Non              | Plus récent  | Timestamp du scrap seulement !      |
| source             | -         | Scrap info     | ❌          | ❌           | ❌               | Non              | -            | Toujours "Recalbox" (attribut).     |
