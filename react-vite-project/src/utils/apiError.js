const FALLBACK_ERROR_KEY = "errors.generic";

export const getApiErrorKey = async (response, fallback = FALLBACK_ERROR_KEY) => {
    try {
        const payload = await response.clone().json();
        if (typeof payload?.error === "string" && payload.error) return payload.error;
        if (typeof payload?.message === "string" && payload.message) return payload.message;
    } catch {
        // Non-JSON responses use the fallback key.
    }
    return fallback;
};

export const translateErrorKey = (key, t, fallback = FALLBACK_ERROR_KEY) => {
    if (typeof key !== "string" || !key) return t(fallback);
    return t(key.startsWith("errors.") ? key : fallback);
};
