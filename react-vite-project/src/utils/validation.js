// Nom / Prénom: lettres, espaces, accents, tirets, apostrophes — pas de chiffres
const nameRegex = /^[a-zA-ZÀ-ÿ\s'-]+$/;
// Courriel: doit finir en @...ca ou @...com
const emailRegex = /^[\w.-]+@[\w.-]+\.(ca|com)$/;
// Matricule (étudiant): exactement 7 chiffres
const matriculeRegex = /^\d{7}$/;
// Mot de passe: 8+ caractères, 1 majuscule, 1 minuscule, 1 chiffre
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
// Format de telephone: doit contenire 10 chiffres
const telephoneRegex = /^\d{10}$/;
// Code postal en format Canadien, ex: A1A1A1
const postalCodeRegex = /^[A-Za-z]\d[A-Za-z]\d[A-Za-z]\d$/;
// Identifiant d'entreprise: Alphanomeric, de 3 à 20 caractères, ex: ACME-01
const enterpriseIdRegex = /^[a-zA-Z0-9]{3,20}$/;

export const validateField = (name, value, data = {}) => {
    switch (name) {
        case "nom":
        case "prenom":
            return nameRegex.test(value) ? undefined : "errors.lettersOnly";
        case "courriel":
            return emailRegex.test(value) ? undefined : "errors.emailFormat";
        case "motDePasse":
            return passwordRegex.test(value) ? undefined : "errors.passwordComplexity";
        case "confirmation":
            return value === data.motDePasse ? undefined : "errors.passwordMismatch";
        case "matricule":
            return matriculeRegex.test(value) ? undefined : "errors.matriculeFormat";
        case "telephone":
            return telephoneRegex.test(value) ? undefined : "errors.telephoneFormat";
        case "codePostal":
            return postalCodeRegex.test(value) ? undefined : "errors.postalCodeFormat";
        case "identifiant":
            return enterpriseIdRegex.test(value) ? undefined : "errors.enterpriseIdFormat";
        case "nomEntreprise":
        case "adresse":
        case "ville":
            return value.trim() !== "" ? undefined : "errors.required";

        default:
            return undefined;
    }
};

export const validateInscription = (data) => {
    const errs = {};

    ["nom", "prenom", "courriel", "motDePasse", "confirmation", "matricule", "telephone", "codePostal", "identifiant", "nomEntreprise", "adresse", "ville"].forEach((field) => {
        if (data[field] === undefined) return;
        const error = validateField(field, data[field], data);
        if (error) errs[field] = error;
    });

    return errs;
};
