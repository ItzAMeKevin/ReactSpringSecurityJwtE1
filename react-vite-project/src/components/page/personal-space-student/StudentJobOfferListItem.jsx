import { useId, useState } from "react";
import { useTranslation } from "react-i18next";
import { cardItem, statusBadge, statusBadgeColor } from "../../../styles/classes.js";
import fetcher from "../../../utils/fetcher.js";

const dtClass = "text-sm font-bold text-black";
const ddClass = "text-sm leading-relaxed text-gray-700";

const StudentJobOfferListItem = ({ offer, acceptedCv }) => {
    const { t } = useTranslation();
    const [expanded, setExpanded] = useState(false);
    const [applyState, setApplyState] = useState("idle");
    const detailsId = useId();

    const hasApplied = Boolean(offer.applied) || applyState === "success";
    const canApply = Boolean(acceptedCv) && !hasApplied && applyState !== "sending";

    const handleApply = async () => {
        if (!canApply) return;
        setApplyState("sending");
        try {
            const res = await fetcher(`/student-cv/${offer.id}/appliquer?offerID=${offer.id}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(acceptedCv),
            });
            setApplyState(res.ok ? "success" : "error");
        } catch (e) {
            console.error("Erreur lors de la candidature :", e);
            setApplyState("error");
        }
    };


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
                <div className="flex flex-col items-end gap-1">
                    {offer.status && (
                        <span className={`${statusBadge} ${statusBadgeColor(offer.status)}`}>
                            {t(`personalSpaceEmployer.statuts.${offer.status}`, offer.status)}
                        </span>
                    )}
                    {hasApplied && (
                        <span className={`${statusBadge} bg-green-100 text-green-800`}>
                            {t("etudiantOffres.applied")}
                        </span>
                    )}
                </div>
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
                    <div className="border-t border-gray-200 pt-4">
                        <button
                            type="button"
                            onClick={handleApply}
                            disabled={!canApply}
                            className="rounded-md bg-[#4b1113] px-4 py-2 text-sm font-semibold text-white
                                       hover:bg-[#3a0d0f] disabled:cursor-not-allowed disabled:opacity-50"
                        >
                            {hasApplied
                                ? t("etudiantOffres.applied")
                                : applyState === "sending"
                                    ? t("etudiantOffres.applying")
                                    : t("etudiantOffres.apply")}
                        </button>

                        {!acceptedCv && !hasApplied && (
                            <p className="mt-2 text-sm text-gray-600">{t("etudiantOffres.noValidatedCv")}</p>
                        )}
                        {applyState === "success" && (
                            <p className="mt-2 text-sm font-medium text-green-700" role="status">
                                {t("etudiantOffres.applySuccess")}
                            </p>
                        )}
                        {applyState === "error" && (
                            <p className="mt-2 text-sm font-medium text-red-700" role="alert">
                                {t("etudiantOffres.applyError")}
                            </p>
                        )}
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