# TODO pour IA

Voici une liste de choses à faire par l'IA

## Instructions pour l'IA

- Ne fait qu'un item à la fois. Je veux (et habituellement doit) vérifier et corriger ce que tu as fait.
- On se parle en français, mais les docs et le code (dont commentaires) sont en anglais

## Liste des taches

1) Faire un état des lieux

   - Actuellement RomManager ne permet de modifier que name/favorite/hidden/adult
   - Code source gamelist.xml : https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.cpp (et.h)
   - Faire un markdown pour lister tous les champs d'un game en xml, et si on le lit dans RomManager, si on l'écrit et les règles de synchro

Example (sans tous les champs):

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

1) Finaliser la logique de comparaison/fusion

   - Si EmulStation pas stoppée: seulement lecture de recalbox
   - Il faut implémenter/compléter l'ébauche de code pour la syncro: compareGame, ...
   - Ajouter des tests unitaires pour `Gamelist.compareGame`.

1) Interface : panneau d'options

   - Ajouter l'interface de configuration SSH.
   - Options avancées pour les règles d'import (NoIntro/Redump/RomM plus tard).
