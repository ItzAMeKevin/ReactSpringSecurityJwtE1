import "./App.css";
import PageLayout from "./components/PageLayout.jsx";
import React, {useEffect, useState} from "react";
import {Navigate, Route, Routes, useNavigate} from "react-router-dom";
import About from "./components/About.jsx";
import LoginForm from "./components/auth/LoginForm.jsx";
import fetcher from "./utils/fetcher.js";
import ErrorPage from "./components/ErrorPage.jsx";
import Logout from "./components/auth/Logout.jsx";
import EmprunteurHome from "./components/page/EmprunteurHome.jsx";
import PersonalSpaceStudent from "./components/page/personal-space-student/PersonalSpaceStudent.jsx";
import PersonalSpaceManager from "./components/page/personal-space-manager/PersonalSpaceManager.jsx";
import PersonalSpaceEmployer from "./components/page/personal-space-employer/PersonalSpaceEmployer.jsx";
import Inscription from "./components/page/inscription/Inscription.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import { ManagerNotificationsProvider } from "./context/ManagerNotificationsContext.jsx";

function App() {
  const [user, setUser] = useState({})
  const [error, setError] = useState(null)
  const [authLoading, setAuthLoading] = useState(true)
  const navigate = useNavigate();

  let token = sessionStorage.getItem('token')

  const navigateForRole = (role) => {
    if (role === "ROLE_STUDENT") navigate("/etudiant");
    else if (role === "ROLE_MANAGER") navigate("/gestionnaire");
    else if (role === "ROLE_EMPLOYER") navigate("/employeur");
    else navigate("/");
  };

  const loginWithCredentials = async (email, password) => {
    try {
      setError(null);
      const response = await fetcher("/user/login", {
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json;charset=UTF-8",
        },
        body: JSON.stringify({email, password}),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || "Login failed");
      }

      const data = await response.json();
      sessionStorage.setItem("token", data.accessToken);

      const userResponse = await fetcher("user/me", {});
      if (!userResponse.ok) throw new Error("Failed to fetch user info");

      const userData = await userResponse.json();
      setUser({...userData, isLoggedIn: true});
      navigateForRole(userData.role);
    } catch (loginError) {
      setError(loginError);
      navigate("/error");
    }
  };

  useEffect(() => {
    const shortcuts = {
      Digit1: ["l@l.com", "bib"],
      Digit2: ["ll@l.com", "bib"],
      Digit3: ["lll@l.com", "bib"],
    };

    const handleShortcut = (event) => {
      if (!event.ctrlKey || !event.shiftKey || !event.altKey) return;
      const credentials = shortcuts[event.code];
      if (!credentials) return;

      event.preventDefault();
      loginWithCredentials(...credentials);
    };

    window.addEventListener("keydown", handleShortcut);
    return () => window.removeEventListener("keydown", handleShortcut);
  }, [navigate]);

  useEffect(() => {
      if (token) {

        try {
          fetcher('user/me', {})
            .then(async (res) => {
                if (!res.ok) {
                  switch (res.status) {
                    case 401:
                      sessionStorage.clear();
                      setUser(null);
                    case 403:
                      throw new Error("Forbidden")
                    case 404:
                      throw new Error("Nothing here 404");
                  }
                }
                const data = await res.json();
                let newUser = {...data, isLoggedIn: true}
                setUser(newUser)
              }
            ).catch(async (err) => {
              setError(err)
              navigate('/error')
          }).finally(() => setAuthLoading(false))

        } catch (err) {
          if (!error) {
            setError(err)
            navigate('/error')
          }
          setAuthLoading(false)
        }
      } else {
        setAuthLoading(false)
      }
    }, [token]
  );

  return (
    <div>
      <Routes>
        <Route path="/" element={<ManagerNotificationsProvider isManager={user?.role === "ROLE_MANAGER"}><PageLayout user={user}/></ManagerNotificationsProvider>}>
          <Route index element={<Navigate to="/login" replace />}/>
          <Route path='about' element={<About/>}/>
          <Route path='login' element={<LoginForm user={user} setUser={setUser} setError={setError}/>}/>
          <Route path='logout' element={<Logout setUser={setUser}/>}/>
          <Route path='emprunteur' element={<EmprunteurHome/>}/>
          <Route path='etudiant' element={<ProtectedRoute user={user} authLoading={authLoading} allowedRoles={["ROLE_STUDENT"]}><PersonalSpaceStudent/></ProtectedRoute>}/>
          <Route path='gestionnaire' element={<ProtectedRoute user={user} authLoading={authLoading} allowedRoles={["ROLE_MANAGER"]}><PersonalSpaceManager/></ProtectedRoute>}/>
          <Route path='employeur' element={<ProtectedRoute user={user} authLoading={authLoading} allowedRoles={["ROLE_EMPLOYER"]}><PersonalSpaceEmployer/></ProtectedRoute>}/>
          <Route path='inscription' element={<Inscription/>}/>
          <Route path='error' element={<ErrorPage error={error}/>}/>
        </Route>
      </Routes>

    </div>
  );
}

export default App;
