import fetcher from "./fetcher.js";

export const fetchBlobUrl = async (url) => {
    const response = await fetcher(url);
    if (!response.ok) {
        throw new Error(`Erreur HTTP ${response.status}: ${response.statusText}`);
    }
    return URL.createObjectURL(await response.blob());
};

export const downloadFile = async (url, filename) => {
    const blobUrl = await fetchBlobUrl(url);
    const link = document.createElement("a");
    try {
        link.href = blobUrl;
        link.download = filename;
        document.body.appendChild(link);
        link.click();
    } finally {
        document.body.removeChild(link);
        URL.revokeObjectURL(blobUrl);
    }
};


