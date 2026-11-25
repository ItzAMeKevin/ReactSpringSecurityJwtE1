import "@/App.css";
import PageLayout from "@/components/layout/PageLayout.jsx";
import React, {useEffect, useState} from "react";
import {Navigate, Route, Routes, useNavigate} from "react-router-dom";
import About from "@/pages/About.jsx";
import LoginForm from "@/pages/auth/LoginForm.jsx";
import fetcher from "@/utils/fetcher.js";
import ErrorPage from "@/pages/ErrorPage.jsx";
import Logout from "@/pages/auth/Logout.jsx";
import PersonalSpaceStudent from "@/dashboards/student/PersonalSpaceStudent.jsx";
import PersonalSpaceManager from "@/dashboards/manager/PersonalSpaceManager.jsx";
import PersonalSpaceEmployer from "@/dashboards/employer/PersonalSpaceEmployer.jsx";
import Inscription from "@/pages/inscription/Inscription.jsx";
import ProtectedRoute from "@/components/routing/ProtectedRoute.jsx";
import { getApiErrorKey } from "@/utils/apiError.js";
import CandidatureTracking from "@/dashboards/manager/sections/CandidatureTracking.jsx";
import ManagerCvSection from "@/dashboards/manager/sections/ManagerCvSection.jsx";
import ManagerOfferSection from "@/dashboards/manager/sections/ManagerOfferSection.jsx";
import StudentCvSection from "@/dashboards/student/sections/StudentCvSection.jsx";
import StudentOfferSection from "@/dashboards/student/sections/StudentOfferSection.jsx";
import StudentHireOfferSection from "@/dashboards/student/sections/StudentHireOfferSection.jsx";
import ConvocationsPage from "@/dashboards/student/components/ConvocationsPage.jsx";
import { STUDENT_CV_SECTIONS } from "@/dashboards/student/studentCvSections.js";
import EmployerOfferSection from "@/dashboards/employer/sections/EmployerOfferSection.jsx";
import EmployerInterviewSection from "@/dashboards/employer/sections/EmployerInterviewSection.jsx";
import EmployerHirePendingSection from "@/dashboards/employer/sections/EmployerHirePendingSection.jsx";
import { EMPLOYER_OFFER_SECTIONS } from "@/dashboards/employer/employerOfferSections.js";

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
        throw new Error(await getApiErrorKey(response, "errors.authenticationFailed"));
      }

      const data = await response.json();
      sessionStorage.setItem("token", data.accessToken);

      const userResponse = await fetcher("user/me", {});
      if (!userResponse.ok) {
        throw new Error(await getApiErrorKey(userResponse));
      }

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
      Digit0: ["jaime.laguerre@yahoo.com", "Jaime1234"],
      Digit9: ["jaime.paolaguere@cmr.ca", "Paola1234"],
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
                      throw new Error(await getApiErrorKey(res, "errors.authenticationFailed"));
                    case 403:
                      throw new Error(await getApiErrorKey(res, "errors.accessDenied"));
                    case 404:
                      throw new Error(await getApiErrorKey(res));
                    default:
                      throw new Error(await getApiErrorKey(res));
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
        <Route path="/" element={<PageLayout user={user}/>}>
          <Route index element={<Navigate to="/login" replace />}/>
          <Route path='about' element={<About/>}/>
          <Route path='login' element={<LoginForm user={user} setUser={setUser} setError={setError}/>}/>
          <Route path='logout' element={<Logout setUser={setUser}/>}/>
          <Route path='etudiant' element={<ProtectedRoute user={user} authLoading={authLoading} allowedRoles={["ROLE_STUDENT"]}><PersonalSpaceStudent/></ProtectedRoute>}>
            <Route index element={<Navigate to="cv/en-attente" replace />}/>
            {STUDENT_CV_SECTIONS.map((section) => (
              <Route key={section.path} path={section.path} element={<StudentCvSection {...section}/>}/>
            ))}
            <Route path='offres' element={<StudentOfferSection/>}/>
            <Route path='embauches' element={<StudentHireOfferSection/>}/>
            <Route path='convocations' element={<ConvocationsPage/>}/>
          </Route>
          <Route path='gestionnaire' element={<ProtectedRoute user={user} authLoading={authLoading} allowedRoles={["ROLE_MANAGER"]}><PersonalSpaceManager/></ProtectedRoute>}>
            <Route index element={<Navigate to="cv" replace />}/>
            <Route path='cv' element={<ManagerCvSection/>}/>
            <Route path='offres' element={<ManagerOfferSection/>}/>
            <Route path='suivi' element={<CandidatureTracking/>}/>
          </Route>
          <Route path='employeur' element={<ProtectedRoute user={user} authLoading={authLoading} allowedRoles={["ROLE_EMPLOYER"]}><PersonalSpaceEmployer/></ProtectedRoute>}>
            <Route index element={<Navigate to="offres/validees" replace />}/>
            {EMPLOYER_OFFER_SECTIONS.map(({ path, ...section }) => (
              <Route key={path} path={path} element={<EmployerOfferSection {...section}/>}/>
            ))}
            <Route path='entrevues' element={<EmployerInterviewSection/>}/>
            <Route path='embauches-en-attente' element={<EmployerHirePendingSection/>}/>
          </Route>
          <Route path='inscription' element={<Inscription/>}/>
          <Route path='error' element={<ErrorPage error={error}/>}/>
        </Route>
      </Routes>

    </div>
  );
}

export default App;
