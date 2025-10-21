# TODO - Synchronisation RomManager

1) Test rapide manuel

   - Configurer les paramètres SSH dans `RomManager.properties` :
     ```properties
     romset.recalbox.ssh.host=recalbox.local
     romset.recalbox.ssh.user=root
     romset.recalbox.ssh.port=22
     romset.recalbox.ssh.key=
     ```
   - Lancer "Sync gamelist" depuis l'interface et confirmer l'arrêt d'EmulationStation.
   - Vérifier qu'ES s'arrête, que RomManager synchronise et qu'ES redémarre en arrière-plan. Contrôler les logs.

2) ✓ Test console unique

   - Ajout du FIXME dans `ProcessSyncGamelist` pour limiter la synchro à `atari2600` (temporaire).
   - Test effectué et validé.

3) Amélioration des logs

   - Grouper les logs par console et par type (missing, missingMedia, updated, deleted).
   - Écrire dans `cache/gamelists/sync-<timestamp>.log`.
   - Afficher un résumé groupé dans la popup finale.

4) Finaliser la logique de comparaison/fusion

   - Confirmer les champs à préserver vs préférer distant (actuellement, fusion basée sur timestamp pour name/favorite/hidden/adult).
   - Ajouter des tests unitaires pour `Gamelist.compareGame`.

5) Interface : panneau d'options

   - Ajouter l'interface de configuration SSH.
   - Option pour activer/désactiver l'arrêt d'ES pendant la synchro.
   - Options avancées pour les règles d'import (NoIntro/Redump/RomM plus tard).

6) Long terme

   - Envisager de patcher EmulationStation pour supporter la fusion plutôt que l'écrasement (optionnel).
   - Documenter les étapes de build/déploiement.

7) → Support SSH password + démarrage avant popup

   - Ajout du support de `romset.recalbox.ssh.password` et utilisation de `sshpass` si fourni.
   - Déplacement du redémarrage d'EmulationStation avant la popup pour ne pas bloquer.
   - Test effectué : fonctionne avec `sshpass` installé.

Notes pour la suite

- Pour continuer : lancer `mvn -DskipTests package` pour vérifier la compilation.
- Pour les tests SSH : soit utiliser une clé SSH (recommandé), soit installer `sshpass` pour l'authentification par mot de passe.

Changements (cette session)

- Ajout des paramètres SSH dans `RomManager.properties`.
- Documentation SSH ajoutée dans README.md.
- Traduction du TODO.md en français.