import {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import fetcher from "../../../utils/fetcher.js";

const ListJobOffer = ({refreshKey}) => {
    const {t} = useTranslation();
    const [offers, setOffers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const programs = t("inscription.programsList", {returnObjects: true});
    const programName = (enumName) =>
        programs.find((p) => p.enumName === enumName)?.name ?? enumName;
    const statusLabel = (status) =>
        t(`personalSpaceEmployer.status.${status}`, {defaultValue: status});
    const statusStyles = {
        WAITING: "bg-yellow-100 text-yellow-800",
        ACCEPTED: "bg-green-100 text-green-800",
        REFUSED: "bg-red-100 text-red-800",
    };

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
            setLoading(true);
            setError(null);
            try {
                const res = await fetcher("/employer", {method: "GET"});
                if (!res.ok) throw new Error(`${res.status}`);
                const data = await res.json();
                if (!cancelled) setOffers(data);
            } catch {
                if (!cancelled) setError(t("errors.generic"));
            } finally {
                if (!cancelled) setLoading(false);
            }
        };

        load();
        return () => {
            cancelled = true;
        };
    }, [refreshKey, t]);

    if (loading) return <p className="text-[#4b1113]">...</p>;
    if (error) return <p className="text-red-600">{error}</p>;
    if (offers.length === 0) {
        return <p className="text-[#4b1113]">{t("personalSpaceEmployer.noJobOffers")}</p>;
    }

    return (
        <ul className="grid w-full max-w-4xl grid-cols-1 gap-4 md:grid-cols-2">
            {offers.map((offer, i) => (
                <li key={offer.id ?? i} className="rounded border border-[#4b1113] bg-white p-4 shadow">
                    <span className={`w-fit rounded px-2 py-0.5 text-xs font-bold ${statusStyles[offer.status] ?? "bg-gray-100 text-gray-800"}`}>
                        {statusLabel(offer.status)}
                    </span>
                    <h3 className="text-lg font-bold text-[#4b1113]">{offer.title}</h3>
                    <p className="text-sm text-gray-600">{programName(offer.programe)}</p>
                    <p className="mt-2 line-clamp-3">{offer.description}</p>
                    <dl className="mt-3 grid grid-cols-2 gap-1 text-sm">
                        <dt className="font-bold text-[#4b1113]">{t("personalSpaceEmployer.fields.salary")}</dt>
                        <dd>{offer.salary}</dd>
                        <dt className="font-bold text-[#4b1113]">{t("personalSpaceEmployer.fields.startingDate")}</dt>
                        <dd>{offer.startingDate}</dd>
                        <dt className="font-bold text-[#4b1113]">{t("personalSpaceEmployer.fields.durationInWeeks")}</dt>
                        <dd>{offer.durationInWeeks}</dd>
                        <dt className="font-bold text-[#4b1113]">{t("personalSpaceEmployer.fields.address")}</dt>
                        <dd>{offer.location.numeroCivic} {offer.location.rue}, {offer.location.ville}, {offer.location.pays}</dd>
                    </dl>
                </li>
            ))}
        </ul>
    );
};

export default ListJobOffer;