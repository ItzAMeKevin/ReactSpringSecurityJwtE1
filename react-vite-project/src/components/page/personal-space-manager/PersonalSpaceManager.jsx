import {useCallback, useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import fetcher from "../../../utils/fetcher.js";
import OfferDetailModal from "./OfferDetailModal.jsx";

const NOTICE_DURATION_MS = 5000;

const PersonalSpaceManager = () => {
    const {t} = useTranslation();

    const [offers, setOffers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [loadError, setLoadError] = useState(false);

    const [selected, setSelected] = useState(null);
    const [deciding, setDeciding] = useState(false);
    const [decisionError, setDecisionError] = useState(false);

    const [notice, setNotice] = useState(null);

    const programs = t("inscription.programsList", {returnObjects: true});
    const programName = (enumName) =>
        programs.find((p) => p.enumName === enumName)?.name ?? enumName;

    const loadOffers = useCallback(async () => {
        setLoading(true);
        setLoadError(false);
        try {
            const res = await fetcher("/gestionnaire/offres/pending", {method: "GET"});
            if (!res.ok) throw new Error(`${res.status}`);
            setOffers(await res.json());
        } catch {
            setLoadError(true);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadOffers();
    }, [loadOffers]);

    useEffect(() => {
        if (!notice) return undefined;
        const timer = setTimeout(() => setNotice(null), NOTICE_DURATION_MS);
        return () => clearTimeout(timer);
    }, [notice]);

    const closeModal = useCallback(() => {
        setSelected(null);
        setDecisionError(false);
    }, []);

    const openOffer = (offer) => {
        setDecisionError(false);
        setSelected(offer);
    };

    const decide = async (action) => {
        if (!selected) return;
        setDeciding(true);
        setDecisionError(false);
        try {
            const res = await fetcher(`/gestionnaire/offres/${selected.id}/${action}`, {method: "PUT"});

            // 404: the offer no longer exists, so it simply leaves the list
            if (!res.ok && res.status !== 404) throw new Error(`${res.status}`);

            setOffers((prev) => prev.filter((o) => o.id !== selected.id));
            if (res.ok) {
                setNotice({type: action === "accept" ? "accepted" : "refused", title: selected.title});
            }
            setSelected(null);
        } catch {
            setDecisionError(true);
        } finally {
            setDeciding(false);
        }
    };

    const showList = !loading && !loadError && offers.length > 0;
    const showEmpty = !loading && !loadError && offers.length === 0;

    return (
        <div className="relative min-h-screen overflow-hidden bg-[#f3ebe3]">
            <div className="flex flex-col items-center gap-4 pt-20 pb-20 px-4">
                <h1 className="text-2xl font-bold text-[#4b1113]">{t("personalSpaceManager.title")}</h1>

                {/* Subtitle with live count (count hidden when there is nothing to process) */}
                <h2 className="text-xl font-bold text-[#4b1113]">
                    {t("personalSpaceManager.pendingOffers")}
                    {showList && ` (${offers.length})`}
                </h2>

                {/* Temporary confirmation message */}
                <div role="status" aria-live="polite" className="w-full max-w-5xl">
                    {notice && (
                        <p className="rounded border border-[#4b1113] bg-white px-4 py-3 text-[#4b1113]">
                            {t(`personalSpaceManager.notice.${notice.type}`, {
                                title: notice.title,
                                interpolation: {escapeValue: false},
                            })}
                        </p>
                    )}
                </div>

                {loading && <p className="text-[#4b1113]">...</p>}

                {!loading && loadError && (
                    <div className="flex flex-col items-center gap-3">
                        <p role="alert" className="text-red-600">{t("errors.generic")}</p>
                        <button
                            type="button"
                            className="bg-[#4b1113] text-white px-4 py-2 rounded hover:bg-[#6b1c1f] transition-colors duration-300"
                            onClick={loadOffers}
                        >
                            {t("personalSpaceManager.retry")}
                        </button>
                    </div>
                )}

                {showEmpty && (
                    <p className="text-[#4b1113]">{t("personalSpaceManager.noPendingOffers")}</p>
                )}

                {showList && (
                    <div className="w-full max-w-5xl overflow-x-auto rounded border border-[#4b1113] bg-white shadow">
                        <table className="w-full text-left text-sm">
                            <thead className="border-b border-[#4b1113] text-[#4b1113]">
                            <tr>
                                <th scope="col" className="px-4 py-3 font-bold">{t("personalSpaceEmployer.fields.title")}</th>
                                <th scope="col" className="px-4 py-3 font-bold">{t("personalSpaceEmployer.fields.programe")}</th>
                                <th scope="col" className="px-4 py-3 font-bold">{t("personalSpaceEmployer.fields.city")}</th>
                                <th scope="col" className="px-4 py-3 font-bold">{t("personalSpaceEmployer.fields.startingDate")}</th>
                                <th scope="col" className="px-4 py-3"><span className="sr-only">{t("personalSpaceManager.actions")}</span></th>
                            </tr>
                            </thead>
                            <tbody className="divide-y divide-[#4b1113]/20">
                            {offers.map((offer) => (
                                <tr key={offer.id}>
                                    <td className="px-4 py-3 font-bold text-[#4b1113]">{offer.title}</td>
                                    <td className="px-4 py-3">{programName(offer.programe)}</td>
                                    <td className="px-4 py-3">{offer.location?.ville ?? "-"}</td>
                                    <td className="px-4 py-3 whitespace-nowrap">{offer.startingDate}</td>
                                    <td className="px-4 py-3 text-right">
                                        <button
                                            type="button"
                                            className="bg-[#4b1113] text-white px-4 py-2 rounded hover:bg-[#6b1c1f] transition-colors duration-300"
                                            onClick={() => openOffer(offer)}
                                        >
                                            {t("personalSpaceManager.view")}
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            {selected && (
                <OfferDetailModal
                    offer={selected}
                    programName={programName}
                    deciding={deciding}
                    hasError={decisionError}
                    onAccept={() => decide("accept")}
                    onRefuse={() => decide("refuse")}
                    onClose={closeModal}
                />
            )}
        </div>
    );
};

export default PersonalSpaceManager;
