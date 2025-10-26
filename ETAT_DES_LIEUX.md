# État des lieux - Champs Game dans RomManager

## Source de référence

Code source gamelist.xml Recalbox : https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.cpp

## Champs d'un game en XML

D'après la spécification Recalbox (MetadataDescriptor.cpp) et l'exemple fourni :

### Liste complète des champs selon Recalbox

1. **path** - Chemin du fichier ROM
2. **name** - Nom du jeu
3. **aliases** - Alias du jeu
4. **licences** - Licences
5. **rating** - Note/évaluation
6. **favorite** - Jeu favori (bool)
7. **hidden** - Jeu caché (bool)
8. **emulator** - Émulateur
9. **core** - Core de l'émulateur
10. **ratio** - Ratio d'écran
11. **desc** - Description
12. **image** - Chemin de l'image
13. **thumbnail** - Chemin du thumbnail
14. **video** - Chemin de la vidéo
15. **releasedate** - Date de sortie
16. **developer** - Développeur
17. **publisher** - Éditeur
18. **genre** - Genre
19. **genreid** - ID du genre
20. **adult** - Jeu adulte (bool)
21. **players** - Nombre de joueurs
22. **region** - Région
23. **playcount** - Nombre de parties jouées
24. **lastplayed** - Dernière fois joué
25. **hash** - Hash CRC32 du ROM
26. **lastPatch** - Dernier patch appliqué
27. **rotation** - Rotation de l'écran
28. **timeplayed** - Temps total de jeu
29. **lightgunluminosity** - Luminosité du lightgun

### Attributs du tag `<game>`

- **source** - Source des métadonnées
- **timestamp** - Timestamp de dernière modification

```xml
<game source="Recalbox" timestamp="1761076000">
   <timeplayed>169</timeplayed>
   <hash>BD08D915</hash>
   <lastplayed>20251026T165737</lastplayed>
   <playcount>1</playcount>
   <genreid>257</genreid>
   <genre>Plateforme</genre>
   <publisher>Tigervision</publisher>
   <developer>Tigervision</developer>
   <releasedate>19820101T000000</releasedate>
   <video>media/videos/Miner 2049er 319732c72fcae8a3fe94c43b8b66091e.mp4</video>
   <thumbnail>media/thumbnails/Miner 2049er f5b2676350b791b6257df46391743cf6.png</thumbnail>
   <image>media/images/Miner 2049er f5b2676350b791b6257df46391743cf6.png</image>
   <desc>"Bounty Bob" exploite une mine radioactive en 2049. Aidez-le à "revendiquer" toutes les différentes stations (écrans multiples). Évitez tout contact avec les organismes mutants mortels en vous enfuyant ou en sautant dessus. Collectez divers articles laissés par les mineurs précédents pour des points bonus.</desc>
   <ratio>auto</ratio>
   <favorite>true</favorite>
   <name>Miner 2049er</name>
   <path>1_atari2600/Miner 2049er (1982) (Tigervision).zip</path>
</game>
```

## État actuel dans RomManager

### Classe Game.java - Champs supportés

| Champ XML          | Type Java | Lecture XML | Modification par GUI | Commentaires                                      |
| ------------------ | --------- | ----------- | -------------------- | ------------------------------------------------- |
| path               | String    | ✅          | ❌                   | Chemin du fichier ROM                             |
| name               | String    | ✅          | ❌                   | Nom du jeu                                        |
| timestamp          | long      | ✅          | ❌                   | Timestamp (non utilisé)                           |
| hash               | String    | ✅          | ❌                   | Hash CRC32 du ROM (non utilisé)                   |
| desc               | String    | ✅          | ❌                   | Description                                       |
| rating             | float     | ✅          | ❌                   | Note/évaluation                                   |
| favorite           | boolean   | ✅          | ✅                   | Jeu favori                                        |
| hidden             | boolean   | ✅          | ✅                   | Jeu caché                                         |
| adult              | boolean   | ✅          | ✅                   | Jeu adulte                                        |
| playcount          | int       | ✅          | ❌                   | Nombre de parties jouées                          |
| lastplayed         | String    | ✅          | ❌                   | Dernière fois joué                                |
| image              | String    | ✅          | ❌                   | Chemin de l'image                                 |
| thumbnail          | String    | ✅          | ❌                   | Chemin du thumbnail (non utilisé, même que image) |
| video              | String    | ✅          | ❌                   | Chemin de la vidéo                                |
| releasedate        | String    | ✅          | ❌                   | Date de sortie                                    |
| developer          | String    | ✅          | ❌                   | Développeur                                       |
| publisher          | String    | ✅          | ❌                   | Éditeur                                           |
| genre              | String    | ✅          | ❌                   | Genre                                             |
| genreid            | String    | ✅          | ❌                   | ID du genre (non utilisé)                         |
| players            | String    | ✅          | ❌                   | Nombre de joueurs                                 |
| region             | String    | ✅          | ❌                   | Région (non utilisé)                              |
| ratio              | String    | ✅          | ❌                   | Ratio d'écran (non utilisé)                       |
| emulator           | -         | ❌          | ❌                   | Émulateur (manquant)                              |
| core               | -         | ❌          | ❌                   | Core de l'émulateur (manquant)                    |
| lastPatch          | -         | ❌          | ❌                   | Dernier patch appliqué (manquant)                 |
| rotation           | -         | ❌          | ❌                   | Rotation de l'écran (manquant)                    |
| timeplayed         | -         | ❌          | ❌                   | Temps total de jeu (manquant)                     |
| lightgunluminosity | -         | ❌          | ❌                   | Luminosité du lightgun (manquant)                 |
| source             | -         | ❌          | ❌                   | Source des métadonnées (attribut, manquant)       |
| aliases            | -         | ❌          | ❌                   | Alias du jeu (manquant)                           |
| licences           | -         | ❌          | ❌                   | Licences (manquant)                               |

### Champs manquants dans Game.java

D'après la spécification Recalbox complète, il manque de nombreux champs :

**Champs présents dans Recalbox mais absents de Game.java :**

- `aliases` - Alias du jeu
- `licences` - Licences
- `emulator` - Émulateur
- `core` - Core de l'émulateur
- `lastPatch` - Dernier patch appliqué
- `rotation` - Rotation de l'écran
- `timeplayed` - Temps total de jeu (différent de playcount)
- `lightgunluminosity` - Luminosité du lightgun
- `source` - Source des métadonnées (attribut du tag `<game>`)

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