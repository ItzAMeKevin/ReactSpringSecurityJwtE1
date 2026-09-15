const labelClass = "mb-1 font-medium text-[#2b1a12]";
const inputClass = "rounded-md border border-[#5c4432] bg-white px-3 py-2 text-[#2b1a12] focus:outline-none focus:ring-2 focus:ring-[#4b1113]";

const StudentFields = ({ errors, programmes }) => {
    return (
        <>
            <div className="flex flex-col">
                <label className={labelClass}>Matricule</label>
                <input type="text" name="matricule" className={inputClass}/>
                {errors.matricule && <span className="text-red-700 text-sm text-center">{errors.matricule}</span>}
            </div>
            <div className="flex flex-col">
                <label className={labelClass}>Programme d'étude</label>
                <select id="programmes" name="programme" className={inputClass}>
                    <option value="" disabled hidden>Sélectionnez un programme</option>
                    {programmes.map((programme) => (
                        <option value={programme} key={programme}>{programme}</option>
                    ))}
                </select>
                {errors.programme && <span className="text-red-700 text-sm text-center">{errors.programme}</span>}
            </div>
        </>
    );
};

export default StudentFields;
