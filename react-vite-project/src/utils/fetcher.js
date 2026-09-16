import mergeHeaders from "./mergeheader";
import BASE_URL from "../components/config/Config.jsx";

async function fetcher(input, options = {}) {
  const token = localStorage.getItem("token");
  const defaultHeaders = token
    ? { Authorization: `Bearer ${token}` }
    : {};
  const headers = mergeHeaders(defaultHeaders, options.headers);
  const cleanInput = input.replace(/^\//, "");

  return fetch(`${BASE_URL}${cleanInput}`, {
    ...options,
    headers,
  });
}

export default fetcher;
