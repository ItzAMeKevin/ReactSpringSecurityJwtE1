import { useState } from "react";
import { useTranslation } from "react-i18next";
import { validateField, validateInscription } from "../../../utils/validation.js";
import BASE_URL from "../../config/Config.jsx";
import RoleSelector from "./RoleSelector.jsx";
import InscriptionForm from "./InscriptionForm.jsx";

const Inscription = () => {
  const { t, i18n } = useTranslation();
  const [errors, setErrors] = useState({});
  const [role, setRole] = useState(null);
  const programmes = ["420.B0 Techniques de l'informatique", "430.A0 Techniques de gestion hôtelière", "221B0 Technologie du génie civil","180.A0 Soins infirmiers","244.A0 Technologie du génie physique","410.A0 Techniques de la logistique du transport"];

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.currentTarget));
    const errs = validateInscription(data);
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;

    const params = {
      firstName: data.prenom,
      lastname: data.nom,
      email: data.courriel,
      password: data.motDePasse,
    };

    switch (role) {
      case "student":
        params.role = "ROLE_STUDENT";
        params.matricule = data.matricule;
        params.programme = data.programme;
        break;
      case "professor":
        params.role = "ROLE_MANAGER";
        break;
      case "employer":
        params.role = "ROLE_EMPLOYER";
        break;
      default:
        break;
    }

    const res = await fetch(`${BASE_URL}user/inscription`, {
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
    <div className="relative min-h-screen overflow-hidden bg-[#4b1113] flex items-center justify-center px-4 py-10">
      <div
        className="pointer-events-none absolute inset-0 opacity-25"
        style={{
          backgroundImage: "radial-gradient(#e8d5b5 1.6px, transparent 1.7px)",
          backgroundSize: "18px 18px",
        }}
      />

      <div className="relative z-10 w-full max-w-lg rounded-2xl border border-[#8b6f52]/30 bg-[#8b6f52] p-8 shadow-2xl shadow-[#4b1113]/30">
        <div className="flex justify-end gap-2 mb-2">
          {["fr", "en"].map((lang) => (
            <button
              key={lang}
              type="button"
              onClick={() => i18n.changeLanguage(lang)}
              className={`text-sm px-2 py-1 rounded text-[#2b1a12] ${i18n.language === lang ? "font-bold underline" : ""}`}
            >
              {lang.toUpperCase()}
            </button>
          ))}
        </div>
        <h1 className="text-2xl font-bold text-center mb-2 text-[#2b1a12]">{t("inscription.title")}</h1>
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
