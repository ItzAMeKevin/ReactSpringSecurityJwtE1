import useNotifications from "@/hooks/useNotifications.js";
import { NotificationsContext } from "@/hooks/useSharedNotifications.js";

const NotificationsProvider = ({ enabled, children }) => {
    const notifications = useNotifications(enabled);
    return <NotificationsContext.Provider value={notifications}>{children}</NotificationsContext.Provider>;
};

export default NotificationsProvider;
