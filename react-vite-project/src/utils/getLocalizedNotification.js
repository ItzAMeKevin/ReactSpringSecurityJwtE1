const getLocalizedNotification = (notification, t) => {
    let notificationType = notification.type;
    
    if (!notificationType) {
        if (notification.title?.includes("accepté")) {
            notificationType = "CV_ACCEPTED";
        } else if (notification.title?.includes("refusé")) {
            notificationType = "CV_REJECTED";
        }
    }

    if (notificationType) {
        const typePath = `notifications.types.${notificationType}`;
        try {
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
        } catch (error) {
            console.warn(`No translation found for notification type: ${notificationType}`);
        }
    }

    return {
        title: notification.title,
        message: notification.message,
    };
};

export default getLocalizedNotification;

