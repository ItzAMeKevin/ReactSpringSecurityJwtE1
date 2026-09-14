const StudentFields = ({ errors, programmes }) => {
    return (
        <>
            <div className="flex flex-col">
                <label>Matricule</label>
                <input type="text" name="matricule"/>
                {errors.matricule && <span className="text-red-500 text-sm">{errors.matricule}</span>}
            </div>
            <div className="flex flex-col">
                <label>Programme d'étude</label>
                <select id="programmes" name="programme">
                    <option value="">Sélectionnez un programme</option>
                    {programmes.map((programme) => (
                        <option value={programme} key={programme}>{programme}</option>
                    ))}
                </select>
                {errors.programme && <span className="text-red-500 text-sm">{errors.programme}</span>}
            </div>
        </>
    );
};

export default StudentFields;
