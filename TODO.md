# Synchronisation Recalbox

1) Stop/Start emulationstation

   - A finir:
     - readme en anglais
     - prévoir de ne pas faire la synchro vers recalbox si pas possible de fermer emulationstation (car sinon recalbox écrase les changements) et avertir l'utilisateur qu'il n'y aura que la lecture depuis recalbox mais pas sa mise à jour

1) Amélioration des logs

   - Grouper les logs par console et par type (missing, missingMedia, updated, deleted).
   - Écrire dans `cache/gamelists/sync-<timestamp>.log`.
   - Afficher un résumé groupé dans la popup finale.

1) Finaliser la logique de comparaison/fusion

   - Confirmer les champs à préserver vs préférer distant (actuellement, fusion basée sur timestamp pour name/favorite/hidden/adult).
   - Ajouter des tests unitaires pour `Gamelist.compareGame`.

1) Interface : panneau d'options

   - Ajouter l'interface de configuration SSH.
   - Options avancées pour les règles d'import (NoIntro/Redump/RomM plus tard).
