export const validateInscription = (data) => {
    const errs = {};

    // Nom / Prénom: lettres, espaces, accents, tirets, apostrophes — pas de chiffres
    const nameRegex = /^[a-zA-ZÀ-ÿ\s'-]+$/;
    if (!nameRegex.test(data.nom)) errs.nom = "Lettres seulement";
    if (!nameRegex.test(data.prenom)) errs.prenom = "Lettres seulement";

    // Courriel: doit finir en @...ca ou @...com
    const emailRegex = /^[\w.-]+@[\w.-]+\.(ca|com)$/;
    if (!emailRegex.test(data.courriel)) errs.courriel = "Doit être @domain.ca ou @domain.com";

    // Mot de passe: correspondance sensible à la casse
    if (data.motDePasse !== data.confirmation) {
        errs.confirmation = "Les mots de passe ne correspondent pas";
    }

    // Matricule (étudiant): exactement 7 chiffres
    if (data.matricule !== undefined) {
        const matriculeRegex = /^\d{7}$/;
        if (!matriculeRegex.test(data.matricule)) {
            errs.matricule = "Doit être 7 chiffres";
        }
    }

    return errs;
};
