# TODO pour IA

Voici une liste de choses à faire par l'IA

## Instructions pour l'IA

- Ne fait qu'un item à la fois. Je veux (et habituellement doit) vérifier et corriger ce que tu as fait.
- On se parle en français, mais les docs et le code (dont commentaires) sont en anglais

## Liste des taches

1) Stop/Start emulationstation

   - A finir:
     - readme en anglais
     - prévoir de ne pas faire la synchro vers recalbox si pas possible de fermer emulationstation (car sinon recalbox écrase les changements) et avertir l'utilisateur qu'il n'y aura que la lecture depuis recalbox mais pas sa mise à jour

2) Amélioration des logs

   - Grouper les logs par console et par type (missing, missingMedia, updated, deleted).
   - Écrire dans `cache/gamelists/sync-<timestamp>.log`.
   - Afficher un résumé groupé dans la popup finale.

3) Finaliser la logique de comparaison/fusion

   - Actuellement RomManager ne permet de modifier que name/favorite/hidden/adult
   - Si EmulStation pas stoppée: seulement lecture de recalbox
   - Il faut implémenter/compléter l'ébauche de code pour la syncro: compareGame, ...
   - Ajouter des tests unitaires pour `Gamelist.compareGame`.

4) Interface : panneau d'options

   - Ajouter l'interface de configuration SSH.
   - Options avancées pour les règles d'import (NoIntro/Redump/RomM plus tard).
