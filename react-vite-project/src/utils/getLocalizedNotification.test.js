import { describe, it, expect, vi } from "vitest";
import getLocalizedNotification from "./getLocalizedNotification.js";

const makeT = (dictionary) => (key) => dictionary[key] ?? key;

describe("getLocalizedNotification", () => {
    it("should return the translated title and message when the type has a translation", () => {
        // Arrange
        const t = makeT({
            "notifications.types.JOB_OFFER_UPLOADED.title": "Nouvelle offre de stage",
            "notifications.types.JOB_OFFER_UPLOADED.message": "Une offre a été déposée.",
        });
        const notification = { type: "JOB_OFFER_UPLOADED", title: "brut", message: "brut" };

        // Act
        const result = getLocalizedNotification(notification, t);

        // Assert
        expect(result).toEqual({
            title: "Nouvelle offre de stage",
            message: "Une offre a été déposée.",
        });
    });

    it("should fall back to the raw title and message when the translation is missing", () => {
        const t = makeT({});
        const notification = { type: "TYPE_INCONNU", title: "Titre brut", message: "Message brut" };

        const result = getLocalizedNotification(notification, t);

        expect(result).toEqual({ title: "Titre brut", message: "Message brut" });
    });

    it("should fall back when only the title is translated", () => {
        const t = makeT({
            "notifications.types.CV_REVIEW_READY.title": "Revue prête",
        });
        const notification = { type: "CV_REVIEW_READY", title: "Titre brut", message: "Message brut" };

        const result = getLocalizedNotification(notification, t);

        expect(result).toEqual({ title: "Titre brut", message: "Message brut" });
    });

    it("should fall back when the notification has no type", () => {
        const t = vi.fn();
        const notification = { title: "Titre brut", message: "Message brut" };

        const result = getLocalizedNotification(notification, t);

        expect(result).toEqual({ title: "Titre brut", message: "Message brut" });
        expect(t).not.toHaveBeenCalled();
    });

    it("should fall back and warn when t throws", () => {
        const warn = vi.spyOn(console, "warn").mockImplementation(() => {});
        const t = () => {
            throw new Error("i18n not ready");
        };
        const notification = { type: "CV_UPLOADED", title: "Titre brut", message: "Message brut" };

        const result = getLocalizedNotification(notification, t);

        expect(result).toEqual({ title: "Titre brut", message: "Message brut" });
        expect(warn).toHaveBeenCalledTimes(1);

        warn.mockRestore();
    });
});
