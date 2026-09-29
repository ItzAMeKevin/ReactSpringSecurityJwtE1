import {useNavigate} from "react-router-dom";

const Logout = ({setUser}) => {
  const navigate = useNavigate();
sessionStorage.clear();
  setUser(null);
  navigate('/');

}
export default Logout;