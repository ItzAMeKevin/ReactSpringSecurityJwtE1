import fetcher from "@/utils/fetcher.js";
import { getApiErrorKey } from "@/utils/apiError.js";

const sendPut = async (url, body) => {
    const options = body === undefined
        ? { method: "PUT" }
        : { method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) };
    const response = await fetcher(url, options);
    if (!response.ok) throw new Error(await getApiErrorKey(response));
    return response;
};

export default sendPut;
