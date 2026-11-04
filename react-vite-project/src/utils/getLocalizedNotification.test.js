import { describe, it, expect } from "vitest";
import getLocalizedNotification from "./getLocalizedNotification.js";

const makeT = (dictionary) => (key) => dictionary[key] ?? key;

describe("getLocalizedNotification", () => {
    it("should return the translated title and message when the event has a translation", () => {
        // Arrange
        const t = makeT({
            "notifications.types.JOB_OFFER_CREATED.title": "Offre de stage soumise",
            "notifications.types.JOB_OFFER_CREATED.message": "Votre offre est en attente de validation.",
        });
        const notification = { category: "JOB_OFFER", event: "JOB_OFFER_CREATED", message: null };

        // Act
        const result = getLocalizedNotification(notification, t);

        // Assert
        expect(result).toEqual({
            title: "Offre de stage soumise",
            message: "Votre offre est en attente de validation.",
        });
    });

    it("should fall back to the category when the event has no translation", () => {
        const t = makeT({ "notifications.categories.CV": "CV" });
        const notification = { category: "CV", event: "EVENEMENT_INCONNU", message: null };

        const result = getLocalizedNotification(notification, t);

        expect(result).toEqual({ title: "CV", message: "" });
    });

    it("should fall back when only the title is translated", () => {
        const t = makeT({
            "notifications.types.CV_ACCEPTED.title": "Votre CV a été accepté",
            "notifications.categories.CV": "CV",
        });
        const notification = { category: "CV", event: "CV_ACCEPTED", message: null };

        const result = getLocalizedNotification(notification, t);

        expect(result).toEqual({ title: "CV", message: "" });
    });

    it("should use the category and the raw message when there is no event", () => {
        const t = makeT({ "notifications.categories.CONTRACT": "Contrat" });
        const notification = { category: "CONTRACT", event: null, message: "Message brut" };

        const result = getLocalizedNotification(notification, t);

        expect(result).toEqual({ title: "Contrat", message: "Message brut" });
    });
});
