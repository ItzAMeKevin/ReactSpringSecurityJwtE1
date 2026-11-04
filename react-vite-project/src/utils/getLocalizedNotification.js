const getLocalizedNotification = (notification, t) => {
    const rawMessage = notification.message || "";

    // Backend stores i18n key as message (e.g., "notification.types.CV_ACCEPTED.message")
    // with a prefix typo: "notification." instead of "notifications."
    const fixedMsgKey = rawMessage.replace(/^notification\./, "notifications.");
    const titleKey = fixedMsgKey.replace(/\.message$/, ".title");

    const translatedTitle = t(titleKey);
    const translatedMessage = t(fixedMsgKey);

    if (!translatedTitle.startsWith("notifications.")) {
        return {
            title: translatedTitle,
            message: translatedMessage.startsWith("notifications.") ? "" : translatedMessage,
        };
    }

    // Fallback: type-based lookup (e.g., notifications.types.CV)
    const notificationType = notification.type;
    if (notificationType) {
        const typePath = `notifications.types.${notificationType}`;
        try {
            const localizedTitle = t(`${typePath}.title`);
            const localizedMessage = t(`${typePath}.message`);
            if (!localizedTitle.startsWith("notifications.types.")) {
                return { title: localizedTitle, message: localizedMessage };
            }
        } catch (error) {
            console.warn(`No translation found for notification type: ${notificationType}`);
        }
    }

    return {
        title: notification.title || rawMessage,
        message: rawMessage,
    };
};

export default getLocalizedNotification;
