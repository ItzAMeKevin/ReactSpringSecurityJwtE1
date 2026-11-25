import {useEffect} from "react";
import {useNavigate} from "react-router-dom";

const Logout = ({setUser}) => {
  const navigate = useNavigate();
  useEffect(() => {
    sessionStorage.clear();
    setUser(null);
    navigate('/');
  }, []);
}
export default Logout;
