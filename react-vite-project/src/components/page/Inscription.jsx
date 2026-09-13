import {useState} from "react";

const Inscription = () => {

    const [errors, setErrors] = useState({});

    const validate = (data) => {
        const errs = {};

        //pour que les fields soient biens respectés

        // Nom / Prénom: lettres, espaces, accents, tirets, apostrophes — pas de chiffres
        const nameRegex = /^[a-zA-ZÀ-ÿ\s'-]+$/;
        if (!nameRegex.test(data.nom)) errs.nom = "Lettres seulement";
        if (!nameRegex.test(data.prenom)) errs.prenom = "Lettres seulement";

        // Courriel: doit finir en @...ca ou @...com
        const emailRegex = /^[\w.-]+@[\w.-]+\.(ca|com)$/;
        if (!emailRegex.test(data.courriel)) errs.courriel = "Doit être @domain.ca ou @domain.com";

        // Mot de passe: correspondance sensible à la casse
        if (data.motDePasse !== data.confirmation) {
            errs.confirmation = "Les mots de passe ne correspondent pas";
        }

        return errs;
    };

    const handleSubmit = (e) => {
        e.preventDefault()
        // ici tu récupères les données du formulaire
        const data = Object.fromEntries(new FormData(e.currentTarget));
        const errs = validate(data);
        setErrors(errs);
        if (Object.keys(errs).length === 0) {
            console.log("Inscription valide:", data);
        }
    }

    const programmes = ["1","2","3"]

    const [role, setRole] = useState(null)

    return (
        <>
        <h1 style={{ textAlign: "center" }}> Inscription</h1>
            <div>
                <label> S'inscrire en tant que </label>
                <select id={'role'} value={role ?? ""} onChange={(e) => setRole(e.target.value)}>
                    <option value={"student"}> Étudiant </option>
                    <option value={"professor"}> Professeur </option>
                    <option value={"employer"}> Employeur </option>
                </select>
            </div>

            <div >
                {role != null &&(

                <form onSubmit={handleSubmit}>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 ">
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

                        <div>
                            <label>Courriel</label>
                            <input type="email" name="courriel" />
                            {errors.courriel && <span className="text-red-500 text-sm">{errors.courriel}</span>}
                        </div>
                        <div>
                            {role === "student" &&(
                                <div>
                                    <div>
                                        <label>Matricule</label>
                                        <input type="text" name="matricule"/>
                                    </div>
                                    <div>
                                        <label>Progame d'étude</label>
                                        <select id={'programmes'}>
                                            {programmes.map((programe) => (
                                                <option value={programe} key={programe}></option>
                                            ))}
                                        </select>
                                    </div>
                                </div>
                                )}
                        </div>

                        <div>
                            <label>Mot de passe</label>
                            <input type="password" name="MDP"/>
                        </div>

                        <div>
                            <label>Confirmation de mot de passe</label>
                            <input type="password" name="Confirmation"/>
                            {errors.confirmation && <span className="text-red-500 text-sm">{errors.confirmation}</span>}
                        </div>
                    </div>

                    <div>
                        <button type="submit" className="btn btn-primary">S'inscrire</button>
                    </div>
                </form>
                )}

            </div>

        </>


    )
}

export default Inscription