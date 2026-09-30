import {useState, useEffect} from "react";
import {useNavigate} from "react-router-dom";
import fetcher from "../../utils/fetcher";
import {useTranslation} from "react-i18next";


const LoginForm = ({user, setUser, setError}) => {
  const navigate = useNavigate();
  const {t, i18n} = useTranslation()

  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [warnings, setWarnings] = useState({
    email: '',
    password: ''
  });
  const [authError, setAuthError] = useState('');

  useEffect(() => {
    if (!user?.isLoggedIn) return;
    if (user.role === "ROLE_STUDENT") navigate("/etudiant");
    else if (user.role === "ROLE_MANAGER") navigate("/gestionnaire");
    else if (user.role === "ROLE_EMPLOYER") navigate("/employeur");
  }, [user]);

  const validateUser = () => {
    let isValid = true;
    let updatedWarnings = {...warnings};

    if (!validateEmail()) {
      updatedWarnings.email = t("login.invalidEmail");
      isValid = false;
    } else {
      updatedWarnings.email = "";
    }

    if (!validatePassword()) {
      updatedWarnings.password = t("login.invalidPassword");
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
    //const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])\S{1,}$/;
    //return passwordRegex.test(formData.password);
    return true;
  }

  const handleChanges = (e) => {
    const {name, value} = e.target;
    setWarnings({...warnings, [name]: ""});
    setAuthError('');
    setFormData({...formData, [name]: value.trim()});
  }

  const handleSubmit = (e) => {
    e.preventDefault();

    if (validateUser()) {
      fetchFunc();
    }
  }

  const fetchFunc = async () => {
    setAuthError('');
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
        const errorData = await response.json();
        setAuthError(errorData.message || "Connexion échouée");
        return;
      }
      const data = await response.json();
      sessionStorage.setItem('token', data.accessToken);

      // Fetch user info to get role
      const userResponse = await fetcher('user/me', {});
      if (!userResponse.ok) {
        throw new Error("Failed to fetch user info");
      }
      const userData = await userResponse.json();

      // Navigate to role-specific page
      const role = userData.role;
      if (role === "ROLE_STUDENT") {
        navigate("/etudiant");
      } else if(role === "ROLE_MANAGER"){
        navigate("/gestionnaire");
      } else if (role === "ROLE_EMPLOYER") {
        navigate("/employeur");
      } else {
        navigate("/");
      }
    } catch(error) {
      setAuthError(error.message || "Server error occurred");
      console.log(error.message);
    }
  }

  return (
    <>
      {!user?.isLoggedIn ? (
          <div className="auth-page">
            <div
                className="pointer-events-none absolute inset-y-0 left-0 w-full md:w-[58%]"
                style={{ clipPath: "polygon(0 0, 100% 0, 78% 100%, 0 100%)" }}
            >
              <div className="absolute inset-0 bg-mint" />
              <div
                  className="absolute -inset-[50%] opacity-25"
                  style={{
                    backgroundImage: "radial-gradient(var(--color-mint-dots) 1.6px, transparent 1.7px)",
                    backgroundSize: "18px 18px",
                    transform: "rotate(32deg)",
                  }}
              />
              <div className="absolute inset-0 bg-gradient-to-r from-mint-dark via-transparent to-mint/40" />
            </div>

            <div className="relative z-10 grid min-h-screen md:grid-cols-2">
              <div className="hidden md:flex flex-col justify-center px-12 lg:px-16 text-white">
                <h2 className="text-4xl lg:text-5xl font-semibold leading-tight mb-4">
                  {t("login.welcome")}
                </h2>
                <p className="max-w-sm text-white/80 text-sm leading-relaxed">
                  {t("login.subtitle")}
                </p>
                <div className="mt-8 h-px w-24 bg-white/50" />
              </div>

              <div className="flex items-center justify-center px-4 py-12">
                <div className="auth-card w-full max-w-md">
                  <div className="flex items-center justify-between mb-2">
                    <h1 className="auth-title">{t("login.title")}</h1>
                    <div className="flex gap-2">
                      {["fr", "en"].map((lang) => (
                        <button
                          key={lang}
                          type="button"
                          onClick={() => i18n.changeLanguage(lang)}
                          className={`lang-btn ${i18n.language === lang ? "font-bold underline" : ""}`}
                        >
                          {lang.toUpperCase()}
                        </button>
                      ))}
                    </div>
                  </div>

                  <form id="login-form" onSubmit={handleSubmit} className="flex flex-col gap-4">
                    {authError && (
                      <div className="w-full rounded-md bg-red-50 border border-red-400 p-3">
                        <p className="text-red-800 text-sm font-semibold">{authError}</p>
                      </div>
                    )}
                    <div className="flex flex-col">
                      <label htmlFor="email" className="form-label">{t("login.email")}</label>
                      <input
                          id="email"
                          type="email"
                          name="email"
                          onChange={handleChanges}
                          required
                          placeholder={t("login.emailPlaceholder")}
                          className={warnings.email ? "form-input-error" : "form-input"}
                      />
                      {warnings.email && (
                          <span className="mt-1 text-red-800 text-xs font-semibold">{warnings.email}</span>
                      )}
                    </div>

                    <div className="flex flex-col">
                      <label htmlFor="password" className="form-label">{t("login.password")}</label>
                      <input
                          id="password"
                          type="password"
                          name="password"
                          onChange={handleChanges}
                          required
                          placeholder={t("login.passwordPlaceholder")}
                          className={warnings.password ? "form-input-error" : "form-input"}
                      />
                      {warnings.password && (
                          <span className="mt-1 text-red-800 text-xs font-semibold">{warnings.password}</span>
                      )}
                    </div>

                    <button type="submit" className="btn-primary mt-2">
                      {t("login.submit")}
                    </button>
                  </form>

                  <p className="text-center mt-6 text-slate">
                    {t("login.firstTime")}{" "}
                    <button
                        type="button"
                        onClick={() => navigate("/inscription")}
                        className="font-semibold underline hover:text-mint-dark"
                    >
                      {t("login.signUp")}
                    </button>
                  </p>
                </div>
              </div>
            </div>
          </div>
      ) : null}
    </>
  )
}

export default LoginForm;
