# État des lieux - Champs Game dans RomManager

## Source de référence

Code source gamelist.xml Recalbox :

- https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.cpp
- https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.h

## État actuel dans RomManager

### Classe Game.java - Champs supportés

| Champ XML          | Type Java | Type champ     | Lecture XML | Écriture XML | Mod. ODS par GUI | Modif Recalbox   | TODO                            | Règle fusion | Utilisation                         |
| ------------------ | --------- | -------------- | ----------- | ------------ | ---------------- | ---------------- | ------------------------------- | ------------ | ----------------------------------- |
| path               | String    | File info      | ✅          | ❌           | ❌               | Non              | ok (Clé)                        | Base         | Chemin du fichier ROM               |
| hash               | String    | File info      | ✅          | ❌           | ❌               | Non              | Calculer en local et comparer   | Base         | Hash CRC32 du ROM                   |
| playcount          | int       | User Stats     | ✅          | ❌           | ❌               | Non              | Prendre de recalbox             | Maximum      | Nombre de parties jouées            |
| lastplayed         | String    | User Stats     | ✅          | ❌           | ❌               | Non              | Prendre de recalbox             | Plus récent  | Dernière fois joué                  |
| timeplayed         | int       | User Stats     | ❌          | ❌           | ❌               | Non              | Lire de recalbox et afficher    | -            | Temps total de jeu (en secondes)    |
| favorite           | boolean   | User           | ✅          | ✅           | ✅               | Oui              | Sync, le plus récemment modifié | Local        | Jeu favori (préférence utilisateur) |
| hidden             | boolean   | User           | ✅          | ✅           | ✅               | Oui              | Sync, le plus récemment modifié | Local        | Jeu caché (préférence utilisateur)  |
| adult              | boolean   | Scrappé / User | ✅          | ✅           | ✅               | Oui              | Sync, le plus récemment modifié | Local        | Jeu adulte (préférence utilisateur) |
| name               | String    | Scrappé / User | ✅          | ❌           | ✅               | Oui              | Sync, le plus récemment modifié | Base         | Nom du jeu                          |
| desc               | String    | Scrappé        | ✅          | ❌           | ❌               | Oui              | Prendre de recalbox             | Plus complet | Description                         |
| rating             | float     | Scrappé        | ✅          | ❌           | ❌               | Oui              | Prendre de recalbox             | Plus élevé   | Note/évaluation                     |
| image              | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Prendre de recalbox             | Plus complet | Chemin de l'image                   |
| thumbnail          | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Prendre de recalbox             | Plus complet | Chemin du thumbnail                 |
| video              | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Prendre de recalbox             | Plus complet | Chemin de la vidéo                  |
| releasedate        | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Prendre de recalbox             | Plus récent  | Date de sortie                      |
| developer          | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Prendre de recalbox             | Plus complet | Développeur                         |
| publisher          | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Prendre de recalbox             | Plus complet | Éditeur                             |
| genre              | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Prendre de recalbox             | Plus complet | Genre                               |
| genreid            | String    | Scrappé        | ✅          | ❌           | ❌               | Oui (ou genre ?) | Prendre de recalbox             | Base         | ID du genre                         |
| players            | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Prendre de recalbox             | Plus complet | Nombre de joueurs                   |
| region             | String    | Scrappé        | ✅          | ❌           | ❌               | Non              | Prendre de recalbox             | Plus complet | Région                              |
| ratio              | String    | Scrappé        | ✅          | ❌           | ❌               | Oui (marche ?)   | Prendre de recalbox             | Plus complet | Ratio d'écran                       |
| emulator           | -         |                | ❌          | ❌           | ❌               | Oui              | plus tard, peut etre            | -            | Émulateur                           |
| core               | -         |                | ❌          | ❌           | ❌               | Oui              | plus tard, peut etre            | -            | Core de l'émulateur                 |
| rotation           | -         |                | ❌          | ❌           | ❌               | Oui              | plus tard, peut etre            | -            | Rotation de l'écran                 |
| lastPatch          | -         |                | ❌          | ❌           | ❌               | ???              | plus tard, peut etre            | -            | Dernier patch appliqué              |
| lightgunluminosity | -         |                | ❌          | ❌           | ❌               | ???              | plus tard, peut etre            | -            | Luminosité du lightgun              |
| aliases            | -         |                | ❌          | ❌           | ❌               | ???              | plus tard, peut etre            | -            | Alias du jeu                        |
| licences           | -         |                | ❌          | ❌           | ❌               | ???              | plus tard, peut etre            | -            | Licences                            |
| timestamp          | long      | Scrap info     | ✅          | ❌           | ❌               | Non              | ok                              | Plus récent  | Timestamp du scrap seulement !      |
| source             | -         | Scrap info     | ❌          | ❌           | ❌               | nON              | -                               | -            | Toujours "Recalbox" (attribut).     |

Voici un exemple avec tous les champs possiblement trouvés

```xml
<game source="Recalbox" timestamp="1760820863">
   <rotation>Left</rotation>
   <hash>2ABD0059</hash>
   <region>us,eu</region>
   <adult>true</adult>
   <genreid>512</genreid>
   <genre>Aventure</genre>
   <publisher>SEGA</publisher>
   <developer>Novotrade</developer>
   <releasedate>19921201T000000</releasedate>
   <video>media/videos/Ecco The Dolphin 52dbcf7bfbd5461815aabfa07d0dc4f8.mp4</video>
   <thumbnail>media/thumbnails/Ecco The Dolphin 5cd758108dd246c28ea024fc3602ef8f.png</thumbnail>
   <image>media/images/Ecco The Dolphin 5cd758108dd246c28ea024fc3602ef8f.png</image>
   <desc>Ecco the Dolphin sur Megadrive est un jeu d'action qui a l'originalité de mettre le joueur aux commandes d'un dauphin. L'histoire débute lorsque Ecco assiste impuissant à la disparition de tout l'écosystème, emporté par une mystérieuse tempête. 25 niveaux durant, explorez les abîmes et résolvez de nombreuses énigmes afin de ramener le calme au sein de la crique.</desc>
   <core>genesisplusgx</core>
   <emulator>libretro</emulator>
   <hidden>true</hidden>
   <favorite>true</favorite>
   <rating>0.75</rating>
   <name>ECCO The Dolphin</name>
   <path>2_JDG_3_ULTRA_DUR_megadrive/ECCO The Dolphin (UE) [T+Fre].zip</path>
</game>
```

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