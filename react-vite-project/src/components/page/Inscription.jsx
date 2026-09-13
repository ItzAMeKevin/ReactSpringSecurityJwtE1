import {useState} from "react";

const Inscription = () => {

    const handleSubmit = (e) => {
        e.preventDefault()
        // ici tu récupères les données du formulaire
        console.log("Formulaire soumis !")
    }

    const programmes = ["1","2","3"]

    const [role, setRole] = useState(null)

    return (
        <>
        <h1 style={{ textAlign: "center" }}> Inscription</h1>
            <div>
                <label> Qui êtes-vous ?</label>
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
                            <input type="text"/>
                        </div>
                        <div className="flex flex-col">
                            <label>Prenom</label>
                            <input type="text"/>
                        </div>

                        <div>
                            <label>Courriel</label>
                            <input type="email"/>
                        </div>
                        <div>
                            {role === "student" &&(
                                <div>
                                    <div>
                                        <label>Matricule</label>
                                        <input type="text"/>
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
                            <input type="password"/>
                        </div>

                        <div>
                            <label>Confirmation de mot de passe</label>
                            <input type="password"/>
                        </div>
                    </div>


                </form>
                )}

            </div>

        </>


    )
}

export default Inscription