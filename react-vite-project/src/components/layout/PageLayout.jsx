import {Outlet} from "react-router-dom";
import Header from "@/components/layout/Header.jsx";
import Footer from "@/components/layout/Footer.jsx";
import '@/components/layout/PageLayout.css'
function PageLayout ({user}) {
  return (
    <div id="pagelayout" className="pageLayout">
      <Header user={user}/>
      <Outlet />
      <Footer />
    </div>
  );
}
export default PageLayout;
