# État des lieux - Champs Game dans RomManager

## Source de référence

Code source gamelist.xml Recalbox :

- https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.cpp
- https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.h

## État actuel dans RomManager

### Classe Game.java - Champs supportés

| Champ XML          | Type Java | Type champ                                 | Lecture XML | Écriture XML | Mod. ODS par GUI |     | TODO                             | Règle fusion | Utilisation                         |
| ------------------ | --------- | ------------------------------------------ | ----------- | ------------ | ---------------- | --- | -------------------------------- | ------------ | ----------------------------------- |
| path               | String    | File info                                  | ✅          | ❌           | ❌               |     | ok (Clé)                         | Base         | Chemin du fichier ROM               |
| hash               | String    | File info                                  | ✅          | ❌           | ❌               |     | Calculer en local et comparer    | Base         | Hash CRC32 du ROM                   |
| timestamp          | long      | ? Est-ce mis a jour au scrap et/ou modif ? | ✅          | ❌           | ❌               |     | Utiliser pour savoir plus récent | Plus récent  | Timestamp de dernière modification  |
| playcount          | int       | User Stats                                 | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Maximum      | Nombre de parties jouées            |
| lastplayed         | String    | User Stats                                 | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus récent  | Dernière fois joué                  |
| timeplayed         | int       | User Stats                                 | ❌          | ❌           | ❌               |     | Lre de recalbox et afficher      | -            | Temps total de jeu (en secondes)    |
| favorite           | boolean   | User                                       | ✅          | ✅           | ✅               |     | Sync, le plus récemment modifié  | Local        | Jeu favori (préférence utilisateur) |
| hidden             | boolean   | User                                       | ✅          | ✅           | ✅               |     | Sync, le plus récemment modifié  | Local        | Jeu caché (préférence utilisateur)  |
| adult              | boolean   | Scrappé / User                             | ✅          | ✅           | ✅               |     | Sync, le plus récemment modifié  | Local        | Jeu adulte (préférence utilisateur) |
| name               | String    | Scrappé / User                             | ✅          | ❌           | ✅               |     | Sync, le plus récemment modifié  | Base         | Nom du jeu                          |
| desc               | String    | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus complet | Description                         |
| rating             | float     | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus élevé   | Note/évaluation                     |
| image              | String    | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus complet | Chemin de l'image                   |
| thumbnail          | String    | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus complet | Chemin du thumbnail                 |
| video              | String    | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus complet | Chemin de la vidéo                  |
| releasedate        | String    | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus récent  | Date de sortie                      |
| developer          | String    | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus complet | Développeur                         |
| publisher          | String    | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus complet | Éditeur                             |
| genre              | String    | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus complet | Genre                               |
| genreid            | String    | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Base         | ID du genre                         |
| players            | String    | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus complet | Nombre de joueurs                   |
| region             | String    | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus complet | Région                              |
| ratio              | String    | Scrappé                                    | ✅          | ❌           | ❌               |     | Prendre de recalbox              | Plus complet | Ratio d'écran                       |
| emulator           | -         |                                            | ❌          | ❌           | ❌               |     | plus tard, peut etre             | -            | Émulateur                           |
| core               | -         |                                            | ❌          | ❌           | ❌               |     | plus tard, peut etre             | -            | Core de l'émulateur                 |
| lastPatch          | -         |                                            | ❌          | ❌           | ❌               |     | plus tard, peut etre             | -            | Dernier patch appliqué              |
| rotation           | -         |                                            | ❌          | ❌           | ❌               |     | plus tard, peut etre             | -            | Rotation de l'écran                 |
| lightgunluminosity | -         |                                            | ❌          | ❌           | ❌               |     | plus tard, peut etre             | -            | Luminosité du lightgun              |
| source             | -         |                                            | ❌          | ❌           | ❌               |     | plus tard, peut etre             | -            | Source des métadonnées (attribut)   |
| aliases            | -         |                                            | ❌          | ❌           | ❌               |     | plus tard, peut etre             | -            | Alias du jeu                        |
| licences           | -         |                                            | ❌          | ❌           | ❌               |     | plus tard, peut etre             | -            | Licences                            |

### Lecture depuis gamelist.xml

**Fichier :** `Gamelist.java::getGame()`

Tous les champs sont lus depuis le XML, sauf `timeplayed` et `source` qui ne sont pas dans la classe Game.

### Écriture vers gamelist.xml

**Fichier :** `Gamelist.java::setGame()`

Actuellement, seuls les champs suivants sont écrits/modifiés :

- `favorite`
- `hidden`
- `adult`

**Commentaire du code :** "TODO Gamelist - Do all other modified values !!"

### Règles de synchronisation actuelles

1. **Lecture seule depuis Recalbox** si EmulationStation n'est pas stoppé
2. **Écriture possible** si EmulationStation est stoppé
3. **Préférences locales** (favorite/hidden/adult) prennent le dessus
4. **Fusion intelligente** basée sur la complétude des métadonnées

### Problèmes identifiés

1. **Champs non modifiables :** La plupart des champs ne peuvent pas être modifiés par RomManager
2. **Champs manquants :** `timeplayed` et `source` ne sont pas gérés
3. **Logique de fusion :** La logique actuelle privilégie la complétude plutôt que la fraîcheur des données
4. **Timestamp :** Le champ timestamp n'est pas utilisé pour déterminer quelle version est la plus récente

### Suggestions d'amélioration

1. **Ajouter les champs manquants :** `timeplayed`, `source`
2. **Utiliser les timestamps :** Comparer les timestamps pour déterminer les données les plus récentes
3. **Étendre les champs modifiables :** Permettre la modification d'autres champs (name, desc, etc.)
4. **Améliorer la logique de fusion :** Basée sur les timestamps plutôt que sur la complétude