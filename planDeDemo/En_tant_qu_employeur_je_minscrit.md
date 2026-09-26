# 📗 Story 2 — Inscription Employeur

## En tant qu'employeur, je veux m'inscrire

### Scénario principal — Inscription réussie

1. Je suis sur la page d'enregistrement et j'accède au formulaire d'inscription.
2. Je sélectionne le rôle « Employeur » dans la liste déroulante.
3. Je saisis l'adresse courriel de l'entreprise.
4. Je saisis mon mot de passe et je le confirme.
5. Je saisis le nom de l'entreprise.
6. Je saisis le NEQ (identifiant d'entreprise).
7. Je saisis l'adresse, la ville et le code postal.
8. Je saisis le numéro de téléphone.
9. Je clique sur le bouton « S'inscrire ».
10. L'inscription est confirmée et je suis redirigé vers la page de connexion.

**Résultat attendu :** le compte employeur est créé, l'utilisateur peut se connecter.

### Critères d'acceptation

- Le formulaire s'affiche correctement pour un employeur.
- La sélection du rôle « Employeur » est obligatoire et valide.
- Les champs courriel, mot de passe, confirmation, nom d'entreprise, NEQ, adresse, ville, code postal et téléphone sont requis.
- Les erreurs de validation sont affichées clairement.
- L'inscription est refusée si le courriel ou le NEQ existe déjà.

### Scénarios alternatifs

| # | Scénario | Résultat attendu |
|---|---|---|
| 2.1 | Champ obligatoire manquant | Message d'erreur sous le champ, requête bloquée |
| 2.2 | Mots de passe différents | Message d'erreur sous confirmation |
| 2.3 | Courriel déjà utilisé | Erreur 409, message « courriel déjà utilisé » |
| 2.4 | NEQ déjà utilisé | Erreur 409, message « NEQ déjà utilisé » |
| 2.5 | Format de code postal invalide | Message d'erreur sous le champ |


### Données de test

| Champ | Valeur |
|---|---|
| Rôle | Employeur |
| Courriel | `contact@technoinc.ca` |
| Mot de passe | `Test1234!` |
| Nom d'entreprise | Techno Inc. |

