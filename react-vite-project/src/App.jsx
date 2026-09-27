import "./App.css";
import PageLayout from "./components/PageLayout.jsx";
import React, {useEffect, useState} from "react";
import {Navigate, Route, Routes, useNavigate} from "react-router-dom";
import MainContainer from "./components/MainContainer.jsx";
import About from "./components/About.jsx";
import LoginForm from "./components/auth/LoginForm.jsx";
import fetcher from "./utils/fetcher.js";
import ErrorPage from "./components/ErrorPage.jsx";
import Logout from "./components/auth/Logout.jsx";
import EmprunteurHome from "./components/page/EmprunteurHome.jsx";
import Inscription from "./components/page/inscription/Inscription.jsx";

function App() {
  const [user, setUser] = useState({})
  const [error, setError] = useState(null)
  const navigate = useNavigate();

  let token = localStorage.getItem('token')

  useEffect(() => {
      if (token) {

        try {
          fetcher('user/me', {})
            .then(async (res) => {
                if (!res.ok) {
                  switch (res.status) {
                    case 401:
                      localStorage.clear();
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
          })

        } catch (err) {
          if (!error) {
            setError(err)
            navigate('/error')
          }
        }
      }
    }, [token]
  );

  return (
    <div>
      <Routes>
        <Route path="/" element={<PageLayout user={user}/>}>
          <Route index element={<Navigate to="/login" replace />}/>
          <Route path='about' element={<About/>}/>
          <Route path='login' element={<LoginForm setError={setError}/>}/>
          <Route path='logout' element={<Logout setUser={setUser}/>}/>
          <Route path='emprunteur' element={<EmprunteurHome/>}/>
          <Route path='inscription' element={<Inscription/>}/>
          <Route path='error' element={<ErrorPage error={error}/>}/>
        </Route>
      </Routes>

    </div>
  );
}

export default App;
