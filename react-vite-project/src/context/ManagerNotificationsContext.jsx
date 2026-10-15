import { createContext, useContext } from "react";
import useManagerNotifications from "../hooks/useManagerNotifications.js";

const ManagerNotificationsContext = createContext(null);

export const ManagerNotificationsProvider = ({ children, isManager }) => {
    const value = useManagerNotifications(isManager);
    return (
        <ManagerNotificationsContext.Provider value={value}>
            {children}
        </ManagerNotificationsContext.Provider>
    );
};

export const useManagerNotificationsContext = () => useContext(ManagerNotificationsContext);
