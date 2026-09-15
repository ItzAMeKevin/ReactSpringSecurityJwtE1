const RoleSelector = ({ role, setRole }) => (
  <div className="flex flex-col mb-4">
    <label className="mb-1 font-medium text-[#2b1a12]">S'inscrire en tant que</label>
    <select
      id="role"
      value={role ?? ""}
      onChange={(e) => setRole(e.target.value)}
      className="
          rounded-md
          border
          border-[#5c4432]
          bg-white
          px-3
          py-2
          text-[#2b1a12]
          focus:outline-none
          focus:ring-2
         focus:ring-[#4b1113]"
    >
      <option value="" disabled hidden>Sélectionnez un rôle</option>
      <option value="student">Étudiant</option>
      <option value="professor">Professeur</option>
      <option value="employer">Employeur</option>
    </select>
  </div>
);

export default RoleSelector;
