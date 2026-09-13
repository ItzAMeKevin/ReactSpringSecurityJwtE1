const ProfessorFields = ({ errors }) => {
    return (
        <>
            <div>
                <label>Numéro d'employé</label>
                <input type="text" name="numeroEmploye"/>
                {errors.numeroEmploye && <span className="text-red-500 text-sm">{errors.numeroEmploye}</span>}
            </div>
            <div>
                <label>Département</label>
                <input type="text" name="departement"/>
                {errors.departement && <span className="text-red-500 text-sm">{errors.departement}</span>}
            </div>
        </>
    );
};

export default ProfessorFields;
