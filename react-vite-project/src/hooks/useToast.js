import { useCallback, useEffect, useState } from "react";

const useToast = (duration = 3000) => {
    const [message, setMessage] = useState(null);

    useEffect(() => {
        if (!message) return undefined;
        const timeoutId = setTimeout(() => setMessage(null), duration);
        return () => clearTimeout(timeoutId);
    }, [message, duration]);

    const showMessage = useCallback((key, params, variant = "success") => setMessage({ key, params, variant }), []);
    const clearMessage = useCallback(() => setMessage(null), []);

    return { message, showMessage, clearMessage };
};

export default useToast;
