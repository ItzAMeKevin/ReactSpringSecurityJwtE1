import UserFields from "./UserFields";

const InscriptionForm = ({ role, errors, programmes, handleSubmit }) => (
  <form onSubmit={handleSubmit}>
    <div className="flex flex-col gap-4">
      <UserFields role={role} errors={errors} programmes={programmes} />
    </div>
    <div>
      <button type="submit" className="btn btn-primary">S'inscrire</button>
    </div>
  </form>
);

export default InscriptionForm;
