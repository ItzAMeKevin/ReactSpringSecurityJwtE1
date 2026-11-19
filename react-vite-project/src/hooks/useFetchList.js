import { useCallback, useEffect, useState } from "react";
import fetcher from "../utils/fetcher.js";

const useFetchList = (url) => {
    const [items, setItems] = useState([]);
    const [status, setStatus] = useState("loading");

    const reload = useCallback(async () => {
        setStatus("loading");
        try {
            const response = await fetcher(url);
            if (!response.ok) {
                setStatus("error");
                return;
            }
            const data = await response.json();
            setItems(Array.isArray(data) ? data : []);
            setStatus("loaded");
        } catch (error) {
            console.error(`Erreur lors du chargement de ${url} :`, error);
            setStatus("error");
        }
    }, [url]);

    useEffect(() => {
        void reload();
    }, [reload]);

    const removeById = useCallback(
        (id) => setItems((current) => current.filter((item) => item.id !== id)),
        []
    );

    const count = status === "loaded" ? items.length : null;

    return { items, setItems, status, reload, removeById, count };
};

export default useFetchList;
