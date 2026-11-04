const getLocalizedNotification = (notification, t) => {
    const { event, category, message } = notification;

    if (event) {
        const typePath = `notifications.types.${event}`;
        const localizedTitle = t(`${typePath}.title`);
        const localizedMessage = t(`${typePath}.message`);

        if (
            !localizedTitle.startsWith("notifications.types.") &&
            !localizedMessage.startsWith("notifications.types.")
        ) {
            return {
                title: localizedTitle,
                message: localizedMessage,
            };
        }
    }

    return {
        title: t(`notifications.categories.${category}`),
        message: message ?? "",
    };
};

export default getLocalizedNotification;
