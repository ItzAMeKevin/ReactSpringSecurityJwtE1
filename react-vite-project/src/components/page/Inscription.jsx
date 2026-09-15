import { useState } from "react";
import { validateField, validateInscription } from "../../utils/validation.js";
import BASE_URL from "../config/Config.jsx";
import RoleSelector from "./inscription/RoleSelector";
import InscriptionForm from "./inscription/InscriptionForm";

const Inscription = () => {
  const [errors, setErrors] = useState({});
  const [role, setRole] = useState(null);
  const programmes = ["1", "2", "3"];

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.currentTarget));
    const errs = validateInscription(data);
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;

    const { confirmation, ...params } = data;

    switch (role) {
      case "student":
        params.role = "STUDENT";
        params.matricule = data.matricule;
        params.programme = data.programme;
        break;
    }

    const res = await fetch(`${BASE_URL}inscription/${RoleSelector.value}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(params),
    });
    return res.json();
  };

  const handleChange = (e) => {
    const { name, value, form } = e.target;
    const data = Object.fromEntries(new FormData(form));

    setErrors((prev) => {
      const next = { ...prev };

      const setOrClear = (field, fieldValue) => {
        if (fieldValue === "") {
          delete next[field];
          return;
        }
        const error = validateField(field, fieldValue, data);
        if (error) {
          next[field] = error;
        } else {
          delete next[field];
        }
      };

      setOrClear(name, value);

      // Keep confirmation's error in sync when motDePasse changes
      if (name === "motDePasse") {
        setOrClear("confirmation", data.confirmation ?? "");
      }

      return next;
    });
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
            handleChange={handleChange}
          />
        )}
      </div>
    </div>
  );
}

export default Inscription
