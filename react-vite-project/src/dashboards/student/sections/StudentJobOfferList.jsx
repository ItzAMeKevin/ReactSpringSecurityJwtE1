import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import fetcher from "@/utils/fetcher.js";
import { cardItem, statusBadge, statusBadgeColor, textError, textMuted } from "@/styles/classes.js";

const OfferCard = ({ offer, acceptedCv }) => {
    const { t } = useTranslation();
    const [expanded, setExpanded] = useState(false);
    const [applyState, setApplyState] = useState("idle");

    const hasApplied = Boolean(offer.applied) || applyState === "success";
    const hasValidCv = Boolean(acceptedCv);
    const loc = offer.location;
    const address = loc ? `${loc.numeroCivic} ${loc.rue}, ${loc.ville}, ${loc.pays}` : null;

    const programs = t("inscription.programsList", { returnObjects: true });
    const programName = Array.isArray(programs)
        ? (programs.find((p) => p.enumName === offer.programe)?.name ?? offer.programe)
        : offer.programe;

    const applyLabel = hasApplied
        ? t("etudiantOffres.applied")
        : applyState === "sending"
            ? t("etudiantOffres.applying")
            : t("etudiantOffres.apply");

    const handleApply = async () => {
        if (!hasValidCv || hasApplied || applyState === "sending") return;
        setApplyState("sending");
        try {
            const res = await fetcher(`/etudiant/cv/${offer.id}/appliquer`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(acceptedCv),
            });
            if (!res.ok) throw new Error();
            setApplyState("success");
        } catch {
            setApplyState("error");
        }
    };

    return (
        <li className={cardItem}>
            <div className="flex items-start justify-between gap-3">
                <h3 className="text-lg font-bold text-main">{offer.title}</h3>
                {hasApplied && (
                    <span className={`${statusBadge} ${statusBadgeColor("ACCEPTED")}`}>
                        {t("etudiantOffres.applied")}
                    </span>
                )}
            </div>

            <p className="mt-1 text-sm text-main/70">{programName}</p>
            {offer.status && (
                <span className={`${statusBadge} ${statusBadgeColor(offer.status)}`}>
                    {t(`personalSpaceEmployer.statuts.${offer.status}`)}
                </span>
            )}
            {offer.companyName && (
                <p className="mt-1 text-sm text-main/70">{offer.companyName}</p>
            )}
            <p className="mt-1 text-sm text-main/70">
                {offer.startingDate} · {offer.durationInWeeks} {t("etudiantOffres.weeks")}
            </p>
            {offer.description && (
                <p className="mt-2 text-sm">{offer.description}</p>
            )}

            {expanded && (
                <div className="mt-4 space-y-2 border-t border-gray-200 pt-4">
                    {offer.prerequisites && <p className="text-sm">{offer.prerequisites}</p>}
                    {offer.salary != null && <p className="text-sm">{offer.salary}</p>}
                    <p className="text-sm">{offer.startingDate}</p>
                    <p className="text-sm">{offer.durationInWeeks} {t("etudiantOffres.weeks")}</p>
                    <p className="text-sm">{address ?? "—"}</p>

                    <div className="mt-3 border-t border-gray-200 pt-3">
                        <button
                            type="button"
                            onClick={handleApply}
                            disabled={!hasValidCv || hasApplied || applyState === "sending"}
                            className="rounded bg-brand px-3 py-1 text-sm font-medium text-white disabled:cursor-not-allowed disabled:opacity-50"
                        >
                            {applyLabel}
                        </button>
                        {!hasValidCv && (
                            <p className={`mt-2 text-sm ${textMuted}`}>{t("etudiantOffres.noValidatedCv")}</p>
                        )}
                        {applyState === "success" && (
                            <p className="mt-2 text-sm font-medium text-green-700" role="status">
                                {t("etudiantOffres.applySuccess")}
                            </p>
                        )}
                        {applyState === "error" && (
                            <p className={`mt-2 text-sm font-medium ${textError}`} role="alert">
                                {t("etudiantOffres.applyError")}
                            </p>
                        )}
                    </div>
                </div>
            )}

            <button
                type="button"
                onClick={() => setExpanded((prev) => !prev)}
                aria-expanded={expanded}
                className="mt-3 cursor-pointer text-sm font-medium text-main underline"
            >
                {expanded ? t("etudiantOffres.showLess") : t("etudiantOffres.readMore")}
            </button>
        </li>
    );
};

const StudentJobOfferList = () => {
    const { t } = useTranslation();
    const [offersState, setOffersState] = useState("loading");
    const [offers, setOffers] = useState([]);
    const [acceptedCv, setAcceptedCv] = useState(null);

    useEffect(() => {
        let cancelled = false;

        fetcher("/etudiant/offres")
            .then(async (res) => {
                if (cancelled) return;
                if (res.status === 403) { setOffersState("noAcceptedCv"); return; }
                if (!res.ok) { setOffersState("error"); return; }
                const data = await res.json();
                if (!Array.isArray(data)) { setOffersState("error"); return; }
                setOffers(data);
                setOffersState("loaded");
            })
            .catch(() => { if (!cancelled) setOffersState("error"); });

        fetcher("/etudiant/cv")
            .then(async (res) => {
                if (cancelled || !res.ok) return;
                const data = await res.json();
                if (!Array.isArray(data)) return;
                if (!cancelled) setAcceptedCv(data.find((cv) => cv.status === "ACCEPTED") ?? null);
            })
            .catch(() => {});

        return () => { cancelled = true; };
    }, []);

    if (offersState === "loading") {
        return <p className={textMuted}>{t("etudiantOffres.loading")}</p>;
    }
    if (offersState === "noAcceptedCv") {
        return <p className={textMuted}>{t("etudiantOffres.noAcceptedCv")}</p>;
    }
    if (offersState === "error") {
        return <p className={textError}>{t("errors.generic")}</p>;
    }
    if (offers.length === 0) {
        return <p className={textMuted}>{t("etudiantOffres.noOffers")}</p>;
    }

    return (
        <ul className="grid w-full grid-cols-1 items-start gap-4 lg:grid-cols-2">
            {offers.map((offer, i) => (
                <OfferCard key={offer.id ?? `fallback-${i}`} offer={offer} acceptedCv={acceptedCv} />
            ))}
        </ul>
    );
};

export default StudentJobOfferList;
