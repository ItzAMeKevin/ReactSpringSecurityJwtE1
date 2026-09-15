import StudentFields from "../StudentFields";

const labelClass = "mb-1 font-medium text-[#2b1a12]";
const inputClass = "rounded-md border border-[#5c4432] bg-white px-3 py-2 text-[#2b1a12] focus:outline-none focus:ring-2 focus:ring-[#4b1113]";

const UserFields = ({ role, errors, programmes }) => (
  <>
    <div className="flex flex-col">
      <label className={labelClass}>Nom</label>
      <input type="text" name="nom" className={inputClass}/>
      {errors.nom && <span className="text-red-700 text-sm text-center">{errors.nom}</span>}
    </div>
    <div className="flex flex-col">
      <label className={labelClass}>Prenom</label>
      <input type="text" name="prenom" className={inputClass}/>
      {errors.prenom && <span className="text-red-700 text-sm text-center">{errors.prenom}</span>}
    </div>
    <div className="flex flex-col">
      <label className={labelClass}>Courriel</label>
      <input type="email" name="courriel" className={inputClass}/>
      {errors.courriel && <span className="text-red-700 text-sm text-center">{errors.courriel}</span>}
    </div>
    <div className="flex flex-col">
      <label className={labelClass}>Mot de passe</label>
      <input type="password" name="motDePasse" className={inputClass}/>
    </div>
    <div className="flex flex-col">
      <label className={labelClass}>Confirmation de mot de passe</label>
      <input type="password" name="confirmation" className={inputClass}/>
      {errors.confirmation && <span className="text-red-700 text-sm text-center">{errors.confirmation}</span>}
    </div>
    {role === "student" && <StudentFields errors={errors} programmes={programmes} />}
  </>
);

export default UserFields;
