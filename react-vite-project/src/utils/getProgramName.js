const getProgramName = (programs, enumName) => {
    if (!Array.isArray(programs)) return enumName;
    return programs.find((p) => p.enumName === enumName)?.name ?? enumName;
};

export default getProgramName;
