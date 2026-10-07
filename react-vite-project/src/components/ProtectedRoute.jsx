import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ user, authLoading, allowedRoles, children }) => {
    if (authLoading) {
        return null;
    }
    if (!user?.isLoggedIn) {
        return <Navigate to="/login" replace />;
    }
    if (allowedRoles && !allowedRoles.includes(user.role)) {
        return <Navigate to="/" replace />;
    }
    return children;
};

export default ProtectedRoute;