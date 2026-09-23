## En tant que manager, je veux m'inscrire

### Scénario principal — Inscription réussie

1. Je suis sur la page d'enregistrement et j'accède au formulaire d'inscription.
2. Je sélectionne le rôle « Professeur » dans la liste déroulante.
3. Je saisis mon prénom et mon nom.
4. Je saisis mon adresse courriel.
5. Je saisis mon mot de passe et je le confirme.
6. Je saisis mon matricule employé.
7. Je clique sur le bouton « S'inscrire ».
8. L'inscription est confirmée et je suis redirigé.

**Résultat attendu :** le compte manager est créé, l'utilisateur est connecté.

### Critères d'acceptation

- Le formulaire s'affiche correctement pour un manager.
- La sélection du rôle « Professeur » est obligatoire et valide.
- Les champs nom, prénom, courriel, mot de passe, confirmation et matricule sont requis.
- Les erreurs de validation sont affichées clairement.
- L'inscription est refusée si le courriel ou le matricule existe déjà.

### Scénarios alternatifs

| # | Scénario | Résultat attendu |
|---|---|---|
| 3.1 | Champ obligatoire manquant | Message d'erreur sous le champ, requête bloquée |
| 3.2 | Mots de passe différents | Message d'erreur sous confirmation |
| 3.3 | Courriel déjà utilisé | Erreur 409, message « courriel déjà utilisé » |
| 3.4 | Matricule déjà utilisé | Erreur 409, message « matricule déjà utilisé » |

### Données de test

| Champ | Valeur |
|---|---|
| Rôle | Professeur |
| Prénom | Marie |
| Nom | Dupont |
| Courriel | `marie.dupont@cegep.ca` |
| Mot de passe | `Test1234!` |
| Matricule | `PROF001` |
