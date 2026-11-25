import { createContext, useContext } from "react";
import useNotifications from "@/hooks/useNotifications.js";

export const NotificationsContext = createContext(null);

const useSharedNotifications = () => {
    const shared = useContext(NotificationsContext);
    const local = useNotifications(shared === null);
    return shared ?? local;
};

export default useSharedNotifications;
