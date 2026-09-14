import FormFields from "./FormFields";
import PasswordFields from "./PasswordFields";

const InscriptionForm = ({ role, errors, programmes, handleSubmit }) => (
  <form onSubmit={handleSubmit}>
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      <FormFields role={role} errors={errors} programmes={programmes} />
      <PasswordFields errors={errors} />
    </div>
    <div>
      <button type="submit" className="btn btn-primary">S'inscrire</button>
    </div>
  </form>
);

export default InscriptionForm;
