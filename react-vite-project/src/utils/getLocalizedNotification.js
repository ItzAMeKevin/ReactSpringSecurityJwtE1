const getLocalizedNotification = (notification, t) => {
    const { event, category, message, params = [] } = notification;

    if (event) {
        const typePath = `notifications.types.${event}`;
        const [name, date] = params;
        const interpolation = name ? { name, date } : {};
        const localizedTitle = t(`${typePath}.title`);
        const localizedMessage = t(`${typePath}.message`, interpolation);

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
