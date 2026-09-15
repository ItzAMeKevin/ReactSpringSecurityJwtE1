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
    <div className="min-h-screen flex items-center justify-center bg-[#4b1113] px-4 py-10">
      <div className="w-full max-w-2xl bg-[#8b6f52] rounded-xl shadow-lg p-8">
        <h1 className="text-2xl font-bold text-center mb-6 text-[#2b1a12]">Inscription</h1>
        <RoleSelector role={role} setRole={setRole} />
        {role != null && (
          <InscriptionForm
            role={role}
            errors={errors}
            programmes={programmes}
            handleSubmit={handleSubmit}
          />
        )}
      </div>
    </div>
  );
}

export default Inscription
