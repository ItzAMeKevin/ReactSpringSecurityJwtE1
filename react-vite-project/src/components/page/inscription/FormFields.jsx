import StudentFields from "../StudentFields";

const FormFields = ({ role, errors, programmes }) => (
  <>
    <div className="flex flex-col">
      <label>Nom</label>
      <input type="text" name="nom"/>
      {errors.nom && <span className="text-red-500 text-sm">{errors.nom}</span>}
    </div>
    <div className="flex flex-col">
      <label>Prenom</label>
      <input type="text" name="prenom"/>
      {errors.prenom && <span className="text-red-500 text-sm">{errors.prenom}</span>}
    </div>
    <div className="flex flex-col">
      <label>Courriel</label>
      <input type="email" name="courriel"/>
      {errors.courriel && <span className="text-red-500 text-sm">{errors.courriel}</span>}
    </div>
    {role === "student" && <StudentFields errors={errors} programmes={programmes} />}
  </>
);

export default FormFields;
