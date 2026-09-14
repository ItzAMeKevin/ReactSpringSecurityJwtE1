const RoleSelector = ({ role, setRole }) => (
  <div>
    <label>S'inscrire en tant que</label>
    <select id="role" value={role ?? ""} onChange={(e) => setRole(e.target.value)}>
      <option value="student">Étudiant</option>
      <option value="professor">Professeur</option>
      <option value="employer">Employeur</option>
    </select>
  </div>
);

export default RoleSelector;
