import Field from "./Field";

const labelClass = "mb-1 font-medium text-[#2b1a12]";

const StudentFields = ({ errors, programmes }) => {
    return (
        <>
            <Field label="Matricule" name="matricule" error={errors.matricule} />
            <div className="flex flex-col">
                <label className={labelClass}>Programme d'étude</label>
                <div className="relative">
                    <select
                        id="programmes"
                        name="programme"
                        className={`w-full rounded-md border px-3 py-2 pr-28 focus:outline-none focus:ring-2 focus:ring-[#4b1113] text-[#2b1a12] ${
                            errors.programme ? "bg-red-50 border-red-400" : "bg-white border-[#5c4432]"
                        }`}
                    >
                        <option value="" disabled hidden>Sélectionnez un programme</option>
                        {programmes.map((programme) => (
                            <option value={programme} key={programme}>{programme}</option>
                        ))}
                    </select>
                    {errors.programme && (
                        <span className="absolute inset-y-0 right-8 flex items-center text-red-800 text-xs font-semibold pointer-events-none">
                            {errors.programme}
                        </span>
                    )}
                </div>
            </div>
        </>
    );
};

export default StudentFields;
