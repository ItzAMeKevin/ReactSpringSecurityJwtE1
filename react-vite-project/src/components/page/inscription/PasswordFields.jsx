const PasswordFields = ({ errors }) => (
  <>
    <div className="flex flex-col">
      <label>Mot de passe</label>
      <input type="password" name="motDePasse"/>
    </div>
    <div className="flex flex-col">
      <label>Confirmation de mot de passe</label>
      <input type="password" name="confirmation"/>
      {errors.confirmation && <span className="text-red-500 text-sm">{errors.confirmation}</span>}
    </div>
  </>
);

export default PasswordFields;
