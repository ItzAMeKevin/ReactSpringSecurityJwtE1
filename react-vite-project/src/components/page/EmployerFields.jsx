const EmployerFields = ({ errors }) => {
    return (
        <>
            <div>
                <label>Nom de l'entreprise</label>
                <input type="text" name="nomEntreprise"/>
                {errors.nomEntreprise && <span className="text-red-500 text-sm">{errors.nomEntreprise}</span>}
            </div>
            <div>
                <label>Secteur d'activité</label>
                <input type="text" name="secteur"/>
                {errors.secteur && <span className="text-red-500 text-sm">{errors.secteur}</span>}
            </div>
        </>
    );
};

export default EmployerFields;
