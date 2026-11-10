const scrollIntoViewOnMount = (element) => {
    if (!element) return;
    const timer = setTimeout(() => {
        element.scrollIntoView({ behavior: "smooth", block: "center" });
    }, 300);
    return () => clearTimeout(timer);
};

export default scrollIntoViewOnMount;
