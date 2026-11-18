import { describe, expect, it } from "vitest";
import { getApiErrorKey, translateErrorKey } from "./apiError.js";

describe("apiError", () => {
    it("reads the backend error key without consuming the response", async () => {
        const response = new Response(JSON.stringify({ error: "errors.userNotFound" }), {
            headers: { "Content-Type": "application/json" },
        });

        await expect(getApiErrorKey(response)).resolves.toBe("errors.userNotFound");
        await expect(response.json()).resolves.toEqual({ error: "errors.userNotFound" });
    });

    it("falls back for non-JSON responses", async () => {
        const response = new Response("server failure", { status: 500 });

        await expect(getApiErrorKey(response)).resolves.toBe("errors.generic");
    });

    it("only translates known error keys", () => {
        const t = (key) => ({ "errors.generic": "Erreur générique" }[key] ?? key);

        expect(translateErrorKey("errors.userNotFound", t)).toBe("errors.userNotFound");
        expect(translateErrorKey("Unexpected failure", t)).toBe("Erreur générique");
    });
});
