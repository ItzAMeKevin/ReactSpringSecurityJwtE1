# Définition de Done

Oct 21, 2026 · @Prodigio Valitiana

Une tâche est terminée seulement quand chaque étape de sa section est complétée, dans l'ordre.

## Front end

1. Tous les critères de succès de la tâche sont remplis à 100 %.
2. La page s'intègre au look and feel de l'application :
    - Boutons alignés uniformément (horizontalement ou verticalement)
    - Libellés, couleurs et messages identiques aux autres pages
    - Les informations les plus récentes sont affichées en haut
3. L'interface est bilingue : tous les textes sont traduits, aucun texte codé en dur.
4. Les messages de succès et d'erreur s'affichent pour chaque action (ex. « Compte créé avec succès »).
5. Aucune erreur dans la console du navigateur.
6. Aucun composant dupliqué : le code commun est centralisé dans des composants réutilisables.
7. Les tests sont réalisés avec une couverture minimale de 80 %.
8. Le travail est révisé par un humain et corrigé si nécessaire.
    1. Assurer l'uniformité du code
9. Une review par l'IAG est demandée après la vérification humaine.

## Back end

1. Tous les critères de succès de la tâche sont remplis à 100 %.
2. Les règles d'accès sont respectées selon le rôle (étudiant, employeur, gestionnaire) et le statut (ex. CV approuvé).
3. Les statuts et libellés retournés sont cohérents avec le front end (ex. « en attente d'acceptation »).
4. Les messages d'erreur et de retour vu au front-end sont bilingues.
5. Aucune classe dupliquée : le code commun est centralisé.
6. Les tests unitaires sont réalisés avec une couverture minimale de 80 %.
7. L'application démarre avec le docker compose configuré dans le pom.xml.
8. Le travail est révisé par un humain et corrigé si nécessaire.
    1. Assurer l'uniformité du code
9. Une review par l'IAG est demandée après la vérification humaine.

## Démo

1. Toutes les tâches présentées respectent la définition de done front end et back end.
2. Le plan de démo est à jour, avec les informations à saisir dans chaque champ.
3. Le plan commence par une mise en contexte pour le PO.
4. La démo est autonome : elle ne dépend d'aucun autre plan de démo.
5. Les données de démo sont prêtes (comptes, employeur et son entreprise, CV, offres).
6. Les rôles de chaque membre pendant la démo sont attribués.
7. La démo se déroule du début à la fin en suivant le plan à la lettre, sans blocage.
    1. Assurer l'uniformité de TOUT

### ONCE ALL IS DONE

1. La branche `main` est mergée dans la branche de travail et les conflits sont réglés.
2. La branche est propre et fusionnée dans `main`.
3. La branche est supprimée.

## Sprint 1

## Point d'action 1

- **Spécifique** : Revoir les disponibilités de l'équipe et réaffecter les créneaux perdus
- **Mesurable** : Tous les créneaux perdus réattribués
- **Atteignable** : Via un sondage de disponibilités
- **Réaliste** : Selon les contraintes de chacun
- **Temporel** : Avant le début du prochain sprint

➡️ **Formulation SMART** : « Revoir les disponibilités de l'équipe et réaffecter les créneaux perdus à des plages horaires plus adaptées, avant le début du prochain sprint. »

---

## Point d'action 2

- **Spécifique** : Vérifier que les tâches *in progress* sont bien assignées à quelqu'un
- **Mesurable** : 100 % des tâches *in progress* assignées à chaque rencontre
- **Atteignable** : Vérification systématique en début de rencontre
- **Réaliste** : Rapide, s'intègre dans la routine d'équipe
- **Temporel** : Au début de chaque rencontre

➡️ **Formulation SMART** : « Au début de chaque rencontre, vérifier que 100 % des tâches *in progress* sont bien assignées à quelqu'un. »

## Prochain Scrum Master pour le sprint 3

## Point d'action 1

- **Spécifique** : Faire un merge de main dans la branche staging avant de merger dans main
- **Mesurable** : Tous les pull requests doivent être à jour avec main avant d'être fusionnés
- **Atteignable** : Avec un git merge main
- **Réaliste** : Évite les merge conflicts et facilite la maintenance
- **Temporel** : Jusqu'à la fin du projet

## Point d'action 2

- **Spécifique** : Lire le code apres la revue, éliminer les classes dupliquées et centraliser le code commun
- **Mesurable** : 100% des classes dupliquées supprimées et code commun centralisé
- **Atteignable** : En suivant la défininition de done et en utilisant des outils de refactoring
- **Réaliste** : Avec le processus de revue de code et les tests unitaires, cela est faisable
- **Temporel** : Avant de commencer une tâche de développement, pour éviter la duplication de code


**Leon**

## Prochain Scrum Master pour le sprint 4

**Kevin**, mais le prof veut que **Léon** refasse la présentation de démo

## Point d'action 1

- **Spécifique** : Faire une pratique de la démo avant la review : scénario, environnement et rôles
- **Mesurable** : La démo peut être réalisée du début à la fin en suivant les étapes du plan, sans blocage
- **Atteignable** : Une rencontre tous les mardis, de 11 h 30 à 15 h au plus tard
- **Réaliste** : La démo sera fonctionnelle et sans surprise devant le PO
- **Temporel** : Chaque mardi, à partir du sprint 4 jusqu'à la fin du projet

➡️ **Formulation SMART** : « Chaque mardi, de 11 h 30 à 15 h au plus tard, à partir du sprint 4,
faire une pratique complète de la démo (scénario, environnement, rôles)
afin qu'elle puisse être réalisée du début à la fin sans blocage devant le PO lors de la review du mercredi. »

---

## Point d'action 2

- **Spécifique** : Renforcer la définition de done pour garantir la cohérence du code et l'uniformité de l'UX
- **Mesurable** : Un bloc d'au moins 2 h de review est planifié dès le début du sprint, le mercredi, et 100 % des tâches terminées sont révisées selon la définition de done
- **Atteignable** : Une review en groupe avec les 5 membres de l'équipe
- **Réaliste** : La définition de done est standardisée et partagée par toute l'équipe
- **Temporel** : Chaque mercredi, dès le prochain sprint, jusqu'à la fin du projet

➡️ **Formulation SMART** : « Dès le prochain sprint, renforcer la définition de done et planifier,
dès le début du sprint, un bloc d'au moins 2 h de review en groupe (5 membres) chaque mercredi
pour réviser 100 % des tâches terminées, afin d'assurer la cohérence du code et l'uniformité de l'UX. »