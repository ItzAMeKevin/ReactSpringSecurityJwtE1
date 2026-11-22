import mergeHeaders from "@/utils/mergeheader";
import BASE_URL from "@/config/api.js";

async function fetcher(input, options = {}) {
  const token = sessionStorage.getItem("token");
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
  