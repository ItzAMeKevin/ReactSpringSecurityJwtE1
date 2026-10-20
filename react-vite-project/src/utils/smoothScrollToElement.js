const easeInOutCubic = (t) =>
    t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;

const smoothScrollToElement = (element, { minDuration = 800, maxDuration = 2000 } = {}) => {
    const startY = window.scrollY;
    const rect = element.getBoundingClientRect();

    const targetY = startY + rect.top - (window.innerHeight / 2 - rect.height / 2);
    const maxY = document.documentElement.scrollHeight - window.innerHeight;
    const finalY = Math.max(0, Math.min(targetY, maxY));
    const distance = finalY - startY;

    const duration = Math.min(maxDuration, Math.max(minDuration, Math.abs(distance) * 0.6));

    let startTime = null;
    let frameId;

    const step = (now) => {
        if (startTime === null) startTime = now;
        const progress = Math.min((now - startTime) / duration, 1);
        window.scrollTo(0, startY + distance * easeInOutCubic(progress));
        if (progress < 1) {
            frameId = requestAnimationFrame(step);
        }
    };

    frameId = requestAnimationFrame(step);
    return () => cancelAnimationFrame(frameId);
};

export default smoothScrollToElement;