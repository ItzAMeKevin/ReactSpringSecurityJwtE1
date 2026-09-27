# En tant qu'étudiant, je veux m'inscrire

## Scénario happy end

1. Je suis sur la page d'enregistrement et j'accède au formulaire d'inscription.
2. Je sélectionne le rôle "Étudiant" dans la liste déroulante.
3. Je saisis mon nom "Toto" et mon prénom"lobo", puis mon adresse e-mail"toto.lobo@mail.com".
4. Je saisis mon mot "Pass123" de passe "Pass123" et je le confirme.
5. Je saisis mon matricule "1234567".
6. Je choisi le programe dans lequelle je suis inscrit
7. Je vois le bouton d'inscription pour confirmer mon inscription.
8. Je vois les messages d'erreur si les informations saisies sont invalides.
9. Je résoit un message de confirmation si tout les informations saisies sont valides

## Scénario erruer

1. Je suis sur la page d'enregistrement et j'accède au formulaire d'inscription.
2. Je sélectionne le rôle "Étudiant" dans la liste déroulante.
3. Je saisis mon nom "Toto" et mon prénom"lobo", puis mon adresse e-mail"toto.lobo@mail.com".
4. Je saisis mon mot "Pass123" de passe "Pass123" et je le confirme.
5. Je saisis mon matricule "1234567".
6. Je choisi le programe dans lequelle je suis inscrit
7. Je vois le bouton d'inscription pour confirmer mon inscription.
8. Si l'étudiant existe déjà dans le système, il reçoit un message d'erreur lorsqu'il tente de s'inscrire à nouveau.

## Critères d'acceptation

- Le formulaire d'inscription s'affiche correctement pour un utilisateur étudiant.
- La sélection du rôle "Étudiant" est obligatoire et valide.
- Les champs nom, prénom, e-mail, mot de passe, confirmation et matricule sont requis.
- Les erreurs de validation sont affichées clairement à l'utilisateur.
- L'inscription est refusée si l'utilisateur a déjà un compte existant.