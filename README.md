# Définition de Done

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

## Point d'action 1 (PLUS NECESSAIRE)

- **Spécifique** : Revoir les disponibilités de l'équipe et réaffecter les créneaux perdus
- **Mesurable** : Tous les créneaux perdus réattribués
- **Atteignable** : Via un sondage de disponibilités
- **Réaliste** : Selon les contraintes de chacun
- **Temporel** : Avant le début du prochain sprint

➡️ **Formulation SMART** : « Revoir les disponibilités de l'équipe et réaffecter les créneaux perdus à des plages horaires plus adaptées, avant le début du prochain sprint. »

---

## Point d'action 2 (PLUS NECESSAIRE)

- **Spécifique** : Vérifier que les tâches *in progress* sont bien assignées à quelqu'un
- **Mesurable** : 100 % des tâches *in progress* assignées à chaque rencontre
- **Atteignable** : Vérification systématique en début de rencontre
- **Réaliste** : Rapide, s'intègre dans la routine d'équipe
- **Temporel** : Au début de chaque rencontre

➡️ **Formulation SMART** : « Au début de chaque rencontre, vérifier que 100 % des tâches *in progress* sont bien assignées à quelqu'un. »

## Prochain Scrum Master pour le sprint 3

## Point d'action 1 (PLUS NECESSAIRE)

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

---

## Sprint 5 : Iulian Moraru

## Point d'action 1

- **Spécifique** : Mettre le chapeau du Product Owner avant de déclarer une tâche terminée : faire le parcours complet de la fonctionnalité dans la peau de l'utilisateur final (étudiant, employeur, gestionnaire) pour valider la clarté de la navigation, des libellés et des messages
- **Mesurable** : 100 % des tâches front end sont testées en parcours utilisateur par un membre autre que le développeur, avec une liste de vérification UX (étapes claires, messages compréhensibles, retour visuel après chaque action, cohérence avec les autres pages), et chaque irritant relevé est corrigé avant le merge
- **Atteignable** : Environ 15 minutes par tâche, intégrées à la review de groupe du mercredi
- **Réaliste** : Aucune nouvelle technologie à apprendre, et l'expérience présentée au PO lors de la démo est directement améliorée
- **Temporel** : Pour chaque tâche, dès le sprint 5 jusqu'à la fin du projet

➡️ **Formulation SMART** : « Dès le sprint 5, avant de déclarer une tâche terminée, tous les membres des développeur
met le chapeau du PO et fait le parcours complet de la fonctionnalité comme utilisateur final, à l'aide d'une liste de vérification UX,
et 100 % des irritants relevés sont corrigés avant le merge, afin d'offrir une meilleure expérience utilisateur. »

---

## Point d'action 2

- **Spécifique** : Créer un subtask Jira distinct pour chaque correctif demandé au sprint précédent, au lieu de tout regrouper dans un seul subtask « Correctif » de la story « Correction sprint précédent »
- **Mesurable** : 100 % des correctifs relevés à la review ont leur propre subtask Jira, avec un titre clair, une personne assignée et un statut à jour
- **Atteignable** : Les subtasks sont créés pendant la planification du sprint, à partir des commentaires reçus à la review
- **Réaliste** : Quelques minutes suffisent, et cela permet de mieux voir qui fait quoi et ce qu'il reste à corriger
- **Temporel** : À chaque planification de sprint, dès le sprint 5 jusqu'à la fin du projet

➡️ **Formulation SMART** : « Dès le sprint 5, à chaque planification de sprint, créer un subtask Jira distinct pour chaque correctif
du sprint précédent dans la story "Correction sprint précédent", avec une personne assignée et un statut à jour,
afin de suivre 100 % des correctifs plutôt que de les regrouper dans un seul subtask "Correctif". »

---

---

## Clean Code Rules

Follow these principles on every change. After implementing, always run `/simplify` before committing.

### Core Acronyms
- **DRY** – No duplicated knowledge; one authoritative representation.
- **KISS** – Simplest solution that works.
- **YAGNI** – Don't build speculative features.
- **SOLID** — S: Single Responsibility · O: Open/Closed · L: Liskov · I: Interface Segregation · D: Dependency Inversion

### Structural
- Separation of Concerns – Different responsibilities in different places.
- Law of Demeter – Only talk to direct dependencies.
- Composition over Inheritance – Prefer composing objects.
- Tell, Don't Ask – Ask objects to act, not to expose state.
- Single Level of Abstraction – Each function operates at one level.
- Prefer Polymorphism over if/else – Let the type decide behavior.
- Use Dependency Injection – Inject, don't hard-code.

### Coding Practices
- Meaningful naming – Reveal intent; no abbreviations or magic numbers.
- Small functions – Do one thing; fit on one screen.
- Boy Scout Rule – Leave code cleaner than you found it.
- Consistency – Do similar things the same way.
- Fail fast – Validate early, return errors immediately.
- Guard clauses – Early returns over deep nesting.
- Avoid negative conditionals – Prefer `if (valid)` over `if (!invalid)`.
- Encapsulate boundary conditions – Handle edge cases in one place.
- Prefer value objects over primitives – Use domain types.
- Code smell awareness – Fix long functions, god classes, shotgun surgery, etc.
- Root cause analysis – Find the root cause, don't patch symptoms.

### Testing
- TDD – Write a failing test before the code.
- One scenario per test – Each test covers exactly one behavior.
- Descriptive test names – The name should describe the scenario and expected outcome.

### Structure & Formatting
- Declare variables close to usage.
- Keep lines short (~80–120 chars).
- Use white space to group related things.
- Place functions in downward direction (called before defined).
- Separate concepts vertically.

---

## Post-Implementation Workflow

After every feature, fix, or refactor:

1. Run tests.
2. Run `/simplify` (or `/simplify focus on [area]` for targeted cleanup).
3. Review the diff.
4. Suggest commit message + files to commit.

---

## Pre-Review Checklist

Run these checks before marking a story as ready for review.

### Tests
- [ ] All tests pass. Pre-existing failures are documented and unchanged.
- [ ] Tests use the actual current DTOs — not stale ones left over from a merge.

### Dead Code
- [ ] No unused DTOs, classes, or methods left behind.
- [ ] No stale imports.
- [ ] No commented-out code blocks.

### Frontend ↔ Backend Contract
- [ ] URL in `fetcher(...)` matches the `@GetMapping`/`@PutMapping` exactly.
- [ ] Request body shape matches the `@RequestBody` DTO fields.
- [ ] Response field names match what the frontend reads (e.g. `offer.adresse` not `offer.location`).
- [ ] MapStruct `@Mapping` annotations are complete — no silent null fields in the DTO.

### i18n
- [ ] Every user-visible string uses a translation key (no hardcoded French/English strings).
- [ ] All new keys exist in both `fr.json` and `en.json`.

### Error Handling
- [ ] HTTP errors use `getApiErrorKey` (project pattern) — not raw `response.statusText`.
- [ ] 409 / 404 / 403 cases are handled on the frontend with user-visible feedback.
- [ ] Backend exceptions map to the right HTTP status (`@ResponseStatus` or `GlobalExceptionHandler`).

### Design System
- [ ] Status badges use `statusBadge + statusBadgeColor(status)` from `classes.js` — no inline color maps.
- [ ] Buttons use the shared classes (`btnPrimary`, `btnAccept`, `btnRefuse`, etc.).
- [ ] Address formatting uses `formatAddress` from `utils/formatAddress.js`.
- [ ] Date formatting uses `formatSlotDate` / `formatDateFrench` from `utils/dateFormatting.js`.

### Backend
- [ ] Write operations have `@Transactional`, reads have `@Transactional(readOnly = true)`.
- [ ] Queries that `JOIN FETCH` a collection use `DISTINCT` to avoid duplicate rows.
- [ ] Method references preferred over lambdas: `Foo::new` not `() -> new Foo()`.

---

## Sprint 6 : Luis Carlos Quezada valle

## Point d'action 1
À partir de ce sprint et jusqu'a la mort,
l'équipe s'engage à compléter 100% des user stories planifiées
et à finaliser la présentation, avec un point d'avancement 
quotidien pour évaluer la progression
et adapter les efforts si nécessaire.
On the brighest day...
**Every ai used was for good!---