const formatSalary = (salary, t) => {
    if (Number(salary) === 0) return t("personalSpaceEmployer.salaryToDiscuss");
    return t("personalSpaceEmployer.salaryPerHour", { salary });
};

export default formatSalary;
