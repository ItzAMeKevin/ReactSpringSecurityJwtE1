// Nom / Prénom: lettres, espaces, accents, tirets, apostrophes — pas de chiffres
const nameRegex = /^[a-zA-ZÀ-ÿ\s'-]+$/;
// Courriel: doit finir en @...ca ou @...com
const emailRegex = /^[\w.-]+@[\w.-]+\.(ca|com)$/;
// Matricule (étudiant): exactement 7 chiffres
const matriculeRegex = /^\d{7}$/;
// Mot de passe: 8+ caractères, 1 majuscule, 1 minuscule, 1 chiffre
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;

export const validateField = (name, value, data = {}) => {
    switch (name) {
        case "nom":
        case "prenom":
            return nameRegex.test(value) ? undefined : "Lettres seulement";
        case "courriel":
            return emailRegex.test(value) ? undefined : "Doit être @domain.ca ou @domain.com";
        case "motDePasse":
            return passwordRegex.test(value) ? undefined : "8+ caractères, 1 majuscule, 1 minuscule, 1 chiffre";
        case "confirmation":
            return value === data.motDePasse ? undefined : "Les mots de passe ne correspondent pas";
        case "matricule":
            return matriculeRegex.test(value) ? undefined : "Doit être 7 chiffres";
        default:
            return undefined;
    }
};

export const validateInscription = (data) => {
    const errs = {};

    ["nom", "prenom", "courriel", "motDePasse", "confirmation", "matricule"].forEach((field) => {
        if (data[field] === undefined) return;
        const error = validateField(field, data[field], data);
        if (error) errs[field] = error;
    });

    return errs;
};
