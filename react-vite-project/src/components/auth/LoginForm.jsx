import {useState} from "react";
import {useNavigate} from "react-router-dom";
import fetcher from "../../utils/fetcher";


const LoginForm = ({user, setUser, setError}) => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [warnings, setWarnings] = useState({
    email: '',
    password: ''
  });

  const validateUser = () => {
    let isValid = true;
    let updatedWarnings = {...warnings};

    if (!validateEmail()) {
      updatedWarnings.email = "courriel invalide";
      isValid = false;
    } else {
      updatedWarnings.email = "";
    }

    if (!validatePassword()) {
      updatedWarnings.password = "mot de passe invalide";
      isValid = false;
    } else {
      updatedWarnings.password = "";
    }

    setWarnings(updatedWarnings);
    return isValid;
  };

  const validateEmail = () => {
    const emailRegex = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;
    return emailRegex.test(formData.email);
  }

  const validatePassword = () => {
    // const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;
    // return passwordRegex.test(formData.password);
    return true;
  }

  const handleChanges = (e) => {
    const {name, value} = e.target;
    setWarnings({...warnings, [name]: ""});
    setFormData({...formData, [name]: value.trim()});
  }

  const handleSubmit = (e) => {
    e.preventDefault();

    if (validateUser()) {
      fetchFunc();
    }
  }

  const fetchFunc = async () => {
    try {
      const response = await fetcher('/user/login', {
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json;charset=UTF-8",
        },
        body: JSON.stringify({
          email: formData.email.toLowerCase(),
          password: formData.password
        }),
      });
      if (!response.ok) {
        switch (response.status) {
          case 401:
            throw new Error("Not authorized");
            break;
          case 404:
            throw new Error("No server available");
          default:
            throw new Error("Not ok")
        }
      }
      const data = await response.json();
      localStorage.setItem('token', data.accessToken);

      // Fetch user info to get role
      const userResponse = await fetcher('user/me', {});
      if (!userResponse.ok) {
        throw new Error("Failed to fetch user info");
      }
      const userData = await userResponse.json();

      // Navigate to role-specific page
      const role = userData.role;
      if (role === "ROLE_EMPRUNTEUR") {
        navigate("/emprunteur");
      } else if (role === "ROLE_PREPOSE") {
        navigate("/prepose");
      } else if (role === "ROLE_GESTIONNAIRE") {
        navigate("/gestionnaire");
      } else {
        navigate("/");
      }
    } catch(error) {
      setError(error)
      navigate('/error')
    }


  }

  // const axiosFetch = () => {
  //   axiosInstance.post("/user/login", {
  //     email: formData.email.toLowerCase(),
  //     password: formData.password
  //   }).then((response) => {
  //
  //     axiosInstance.defaults.headers.common['Authorization'] = response.data.accessToken;
  //     sessionStorage.setItem('token', response.data.accessToken);
  //
  //     axiosInstance.get('/user/me')
  //       .then(res => {
  //         let newUser = {...res.data, isLoggedin: true}
  //         setUser(newUser)
  //       })
  //       .catch(err => {
  //         setWarnings({...warnings, email: err.response?.data.message})
  //       })
  //   }).catch((error) => {
  //     if (error.response) {
  //       if (error.response?.status === 406) {
  //         setWarnings({...warnings, email: "wrongEmail"});
  //         setWarnings({...warnings, password: "wrongPassword"});
  //       }
  //     } else {
  //       //toast.error(t('fetchError') + t(error.response?.data.message));
  //       setWarnings({...warnings, email: "wrongEmail", password: "wrongPassword"});
  //     }
  //   });
  // }

  return (
    <>
      {user?.isLoggedIn ? (
        user.role === "ROLE_STUDENT" ? navigate("/persospace") :
          user.role === "ROLE_EMPLOYER" ? navigate("/prepose") :
            user.role === "ROLE_MANAGER" ? navigate("/gestionnaire") :
              navigate("/")
      ) : (
          <div className="relative min-h-screen overflow-hidden bg-[#f3ebe3]">
            <div
                className="pointer-events-none absolute inset-y-0 left-0 w-full md:w-[58%]"
                style={{ clipPath: "polygon(0 0, 100% 0, 78% 100%, 0 100%)" }}
            >
              <div className="absolute inset-0 bg-[#4b1113]" />
              <div
                  className="absolute -inset-[50%] opacity-25"
                  style={{
                    backgroundImage: "radial-gradient(#e8d5b5 1.6px, transparent 1.7px)",
                    backgroundSize: "18px 18px",
                    transform: "rotate(32deg)",
                  }}
              />
              <div className="absolute inset-0 bg-gradient-to-r from-[#4b1113] via-transparent to-[#4b1113]/40" />
            </div>

            <div className="relative z-10 grid min-h-screen md:grid-cols-2">
              <div className="hidden md:flex flex-col justify-center px-12 lg:px-16 text-[#f3ebe3]">
                <h2 className="text-4xl lg:text-5xl font-semibold leading-tight mb-4">
                  Bienvenue
                </h2>
                <p className="max-w-sm text-[#f3ebe3]/80 text-sm leading-relaxed">
                  Connectez-vous pour accéder à votre espace et poursuivre vos démarches.
                </p>
                <div className="mt-8 h-px w-24 bg-[#e8d5b5]/50" />
              </div>

              <div className="flex items-center justify-center px-4 py-12">
                <div className="w-full max-w-md rounded-2xl border border-[#8b6f52]/30 bg-[#8b6f52] p-8 shadow-2xl shadow-[#4b1113]/30">
                  <h1 className="text-2xl font-bold text-center mb-2 text-[#2b1a12]">Connexion</h1>

                  <form id="login-form" onSubmit={handleSubmit} className="flex flex-col gap-4">
                    <div className="flex flex-col">
                      <label htmlFor="email" className="mb-1 font-medium text-[#2b1a12]">Courriel</label>
                      <input
                          id="email"
                          type="email"
                          name="email"
                          onChange={handleChanges}
                          required
                          placeholder="prenom.nom@domain.com"
                          className={`w-full rounded-md border px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-[#4b1113] text-[#2b1a12] ${
                              warnings.email ? "bg-red-50 border-red-400" : "bg-white border-[#5c4432]"
                          }`}
                      />
                      {warnings.email && (
                          <span className="mt-1 text-red-800 text-xs font-semibold">{warnings.email}</span>
                      )}
                    </div>

                    <div className="flex flex-col">
                      <label htmlFor="password" className="mb-1 font-medium text-[#2b1a12]">Mot de passe</label>
                      <input
                          id="password"
                          type="password"
                          name="password"
                          onChange={handleChanges}
                          required
                          placeholder="Votre mot de passe"
                          className={`w-full rounded-md border px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-[#4b1113] text-[#2b1a12] ${
                              warnings.password ? "bg-red-50 border-red-400" : "bg-white border-[#5c4432]"
                          }`}
                      />
                      {warnings.password && (
                          <span className="mt-1 text-red-800 text-xs font-semibold">{warnings.password}</span>
                      )}
                    </div>

                    <button
                        type="submit"
                        className="w-full mt-2 bg-[#4b1113] text-white font-medium py-2.5 rounded-md hover:bg-[#3a0d0f] transition-colors"
                    >
                      Se connecter
                    </button>
                  </form>

                  <p className="text-center mt-6 text-[#2b1a12]">
                    Première fois ?{" "}
                    <button
                        type="button"
                        onClick={() => navigate("/inscription")}
                        className="font-semibold underline hover:text-[#3a0d0f]"
                    >
                      Inscrivez-vous
                    </button>
                  </p>
                </div>
              </div>
            </div>
          </div>
      )}
    </>
  )
}

export default LoginForm;
