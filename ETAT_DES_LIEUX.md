# État des lieux - Champs Game dans RomManager

## Source de référence

Code source gamelist.xml Recalbox :

- https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.cpp
- https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.h

## État actuel dans RomManager

### Classe Game.java - Champs supportés

| Champ XML          | Type Java | Lecture XML | Écriture XML | Modification par GUI | Règle fusion | Utilisation                                 |
| ------------------ | --------- | ----------- | ------------ | -------------------- | ------------ | ------------------------------------------- |
| path               | String    | ✅          | ❌           | ❌                   | Base         | Chemin du fichier ROM                       |
| name               | String    | ✅          | ❌           | ❌                   | Base         | Nom du jeu                                  |
| timestamp          | long      | ✅          | ❌           | ❌                   | Plus récent  | Timestamp de dernière modification          |
| hash               | String    | ✅          | ❌           | ❌                   | Base         | Hash CRC32 du ROM                           |
| desc               | String    | ✅          | ❌           | ❌                   | Plus complet | Description                                 |
| rating             | float     | ✅          | ❌           | ❌                   | Plus élevé   | Note/évaluation                             |
| favorite           | boolean   | ✅          | ✅           | ✅                   | Local        | Jeu favori (préférence utilisateur)         |
| hidden             | boolean   | ✅          | ✅           | ✅                   | Local        | Jeu caché (préférence utilisateur)          |
| adult              | boolean   | ✅          | ✅           | ✅                   | Local        | Jeu adulte (préférence utilisateur)         |
| playcount          | int       | ✅          | ❌           | ❌                   | Maximum      | Nombre de parties jouées                    |
| lastplayed         | String    | ✅          | ❌           | ❌                   | Plus récent  | Dernière fois joué                          |
| image              | String    | ✅          | ❌           | ❌                   | Plus complet | Chemin de l'image                           |
| thumbnail          | String    | ✅          | ❌           | ❌                   | Plus complet | Chemin du thumbnail                         |
| video              | String    | ✅          | ❌           | ❌                   | Plus complet | Chemin de la vidéo                          |
| releasedate        | String    | ✅          | ❌           | ❌                   | Plus récent  | Date de sortie                              |
| developer          | String    | ✅          | ❌           | ❌                   | Plus complet | Développeur                                 |
| publisher          | String    | ✅          | ❌           | ❌                   | Plus complet | Éditeur                                     |
| genre              | String    | ✅          | ❌           | ❌                   | Plus complet | Genre                                       |
| genreid            | String    | ✅          | ❌           | ❌                   | Base         | ID du genre                                 |
| players            | String    | ✅          | ❌           | ❌                   | Plus complet | Nombre de joueurs                           |
| region             | String    | ✅          | ❌           | ❌                   | Plus complet | Région                                      |
| ratio              | String    | ✅          | ❌           | ❌                   | Plus complet | Ratio d'écran                               |
| emulator           | -         | ❌          | ❌           | ❌                   | -            | Émulateur (manquant)                        |
| core               | -         | ❌          | ❌           | ❌                   | -            | Core de l'émulateur (manquant)              |
| lastPatch          | -         | ❌          | ❌           | ❌                   | -            | Dernier patch appliqué (manquant)           |
| rotation           | -         | ❌          | ❌           | ❌                   | -            | Rotation de l'écran (manquant)              |
| timeplayed         | -         | ❌          | ❌           | ❌                   | -            | Temps total de jeu (manquant)               |
| lightgunluminosity | -         | ❌          | ❌           | ❌                   | -            | Luminosité du lightgun (manquant)           |
| source             | -         | ❌          | ❌           | ❌                   | -            | Source des métadonnées (attribut, manquant) |
| aliases            | -         | ❌          | ❌           | ❌                   | -            | Alias du jeu (manquant)                     |
| licences           | -         | ❌          | ❌           | ❌                   | -            | Licences (manquant)                         |

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