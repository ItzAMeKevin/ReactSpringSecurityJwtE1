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
                    <h3 className="text-lg font-bold text-[#4b1113]">{offer.title}</h3>
                    <p className="text-sm text-gray-600">{programName(offer.programe)}</p>
                    <p className="mt-2 line-clamp-3">{offer.description}</p>
                    <dl className="mt-3 grid grid-cols-2 gap-1 text-sm">
                        <dt className="font-bold text-[#4b1113]">{t("personalSpaceEmployer.fields.startingDate")}</dt>
                        <dd>{offer.startingDate}</dd>
                        <dt className="font-bold text-[#4b1113]">{t("personalSpaceEmployer.fields.durationInWeeks")}</dt>
                        <dd>{offer.durationInWeeks}</dd>
                        <dt className="font-bold text-[#4b1113]">{t("personalSpaceEmployer.fields.city")}</dt>
                        <dd>{offer.adresse?.ville}</dd>
                    </dl>
                </li>
            ))}
        </ul>
    );
};

export default ListJobOffer;