import {Outlet} from "react-router-dom";
import Header from "@/components/layout/Header.jsx";
import Footer from "@/components/layout/Footer.jsx";
import NotificationsProvider from "@/components/layout/NotificationsProvider.jsx";
import '@/components/layout/PageLayout.css'
function PageLayout ({user}) {
  return (
    <NotificationsProvider enabled={Boolean(user?.isLoggedIn)}>
      <div id="pagelayout" className="pageLayout">
        <Header user={user}/>
        <Outlet />
        <Footer />
      </div>
    </NotificationsProvider>
  );
}
export default PageLayout;
