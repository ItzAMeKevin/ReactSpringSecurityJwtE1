import StudentFields from "../StudentFields";

const UserFields = ({ role, errors, programmes }) => (
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
    <div className="flex flex-col">
      <label>Mot de passe</label>
      <input type="password" name="motDePasse"/>
    </div>
    <div className="flex flex-col">
      <label>Confirmation de mot de passe</label>
      <input type="password" name="confirmation"/>
      {errors.confirmation && <span className="text-red-500 text-sm">{errors.confirmation}</span>}
    </div>
    {role === "student" && <StudentFields errors={errors} programmes={programmes} />}
  </>
);

export default UserFields;
