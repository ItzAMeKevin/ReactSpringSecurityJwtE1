const formatAddress = (loc) => {
    if (!loc) return null;
    return [
        [loc.numeroCivic, loc.rue].filter(Boolean).join(" "),
        loc.ville,
        loc.codePostal,
        loc.pays,
    ].filter(Boolean).join(", ") || null;
};

export default formatAddress;
