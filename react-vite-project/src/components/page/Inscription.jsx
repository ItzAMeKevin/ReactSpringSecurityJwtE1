import { useState } from "react";
import { validateInscription } from "../../utils/validation.js";
import RoleSelector from "./inscription/RoleSelector";
import InscriptionForm from "./inscription/InscriptionForm";

const Inscription = () => {
  const [errors, setErrors] = useState({});
  const [role, setRole] = useState(null);
  const programmes = ["1", "2", "3"];

  const handleSubmit = (e) => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.currentTarget));
    const errs = validateInscription(data);
    setErrors(errs);
    if (Object.keys(errs).length === 0) {
      console.log("Inscription valide:", data);
    }
  };

  return (
    <>
      <h1 className="text-center mb-6">Inscription</h1>
      <RoleSelector role={role} setRole={setRole} />
      {role != null && (
        <InscriptionForm
          role={role}
          errors={errors}
          programmes={programmes}
          handleSubmit={handleSubmit}
        />
      )}
    </>
  );
}

export default Inscription