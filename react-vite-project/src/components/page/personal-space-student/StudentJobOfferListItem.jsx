import { useId, useState } from "react";
import { useTranslation } from "react-i18next";
import { cardItem, statusBadge, statusBadgeColor } from "../../../styles/classes.js";

const dtClass = "text-sm font-bold text-black";
const ddClass = "text-sm leading-relaxed text-gray-700";

const StudentJobOfferListItem = ({ offer }) => {
    const { t } = useTranslation();
    const [expanded, setExpanded] = useState(false);
    const detailsId = useId();

    const programs = t("inscription.programsList", { returnObjects: true });
    const programName = (enumName) => {
        if (!Array.isArray(programs)) return enumName;
        return programs.find((p) => p.enumName === enumName)?.name ?? enumName;
    };

    const loc = offer.location;
    const address = loc
        ? [
            [loc.numeroCivic, loc.rue].filter(Boolean).join(" "),
            loc.ville,
            loc.codePostal,
            loc.pays,
        ].filter(Boolean).join(", ")
        : null;

    return (
        <li className={`${cardItem} p-5 ${expanded ? "md:col-span-2" : ""}`}>
            <div className="flex items-start justify-between gap-3">
                <h3 className="text-lg font-bold text-black">{offer.title}</h3>
                {offer.status && (
                    <span className={`${statusBadge} ${statusBadgeColor(offer.status)}`}>
                        {t(`personalSpaceEmployer.statuts.${offer.status}`, offer.status)}
                    </span>
                )}
            </div>

            <p className="mt-1 text-sm text-gray-600">{programName(offer.programe)}</p>
            {offer.companyName && (
                <p className="mt-1 text-sm text-gray-600">
                    {t("etudiantOffres.company")}: <span className="font-medium">{offer.companyName}</span>
                </p>
            )}
            <p className="mt-1 text-sm text-gray-600">
                {offer.startingDate} · {offer.durationInWeeks} {t("etudiantOffres.weeks")}
            </p>

            {offer.description && (
                <p className={`mt-3 whitespace-pre-line leading-relaxed ${expanded ? "" : "line-clamp-2"}`}>
                    {offer.description}
                </p>
            )}

            {expanded && (
                <dl id={detailsId} className="mt-4 space-y-4 border-t border-gray-200 pt-4">
                    {offer.prerequisites && (
                        <div>
                            <dt className={dtClass}>{t("personalSpaceEmployer.fields.requirements")}</dt>
                            <dd className={`${ddClass} mt-1 whitespace-pre-line`}>{offer.prerequisites}</dd>
                        </div>
                    )}
                    <div className="grid grid-cols-1 gap-x-6 gap-y-3 sm:grid-cols-2">
                        {offer.salary != null && (
                            <div>
                                <dt className={dtClass}>{t("personalSpaceEmployer.fields.salary")}</dt>
                                <dd className={ddClass}>{offer.salary}</dd>
                            </div>
                        )}
                        <div>
                            <dt className={dtClass}>{t("personalSpaceEmployer.fields.startingDate")}</dt>
                            <dd className={ddClass}>{offer.startingDate}</dd>
                        </div>
                        <div>
                            <dt className={dtClass}>{t("personalSpaceEmployer.fields.durationInWeeks")}</dt>
                            <dd className={ddClass}>{offer.durationInWeeks} {t("etudiantOffres.weeks")}</dd>
                        </div>
                        <div>
                            <dt className={dtClass}>{t("personalSpaceEmployer.fields.address")}</dt>
                            <dd className={ddClass}>{address ?? "—"}</dd>
                        </div>
                    </div>
                </dl>
            )}

            <button
                type="button"
                onClick={() => setExpanded((prev) => !prev)}
                aria-expanded={expanded}
                aria-controls={expanded ? detailsId : undefined}
                className="mt-3 cursor-pointer text-sm font-medium text-[#4b1113] underline underline-offset-2 hover:text-[#3a0d0f]"
            >
                {expanded ? t("etudiantOffres.showLess") : t("etudiantOffres.readMore")}
            </button>
        </li>
    );
};

export default StudentJobOfferListItem;