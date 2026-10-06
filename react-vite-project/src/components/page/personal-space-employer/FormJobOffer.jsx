import {useTranslation} from "react-i18next";

const FormJobOffer = () => {

    const {t} = useTranslation();
    return (
        <form action="submit" method="post">
            <div className="space-y-4">
                <label htmlFor="title" className="block text-[#4b1113] font-bold mb-2">
                    {t("personalSpaceEmployer.fields.title")}
                </label>
                <input
                    type="text"
                    id="title"
                    name="title"
                    className="w-full border border-[#4b1113] rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#4b1113]"
                />
                <label htmlFor="description" className="block text-[#4b1113] font-bold mb-2">
                    {t("personalSpaceEmployer.fields.description")}
                </label>
                <textarea
                    id="description"
                    name="description"
                    className="min-h-24 w-full resize-y border border-[#4b1113] rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#4b1113]"
                ></textarea>
                <label htmlFor="requirements" className="block text-[#4b1113] font-bold mb-2">
                    {t("personalSpaceEmployer.fields.requirements")}
                </label>
                <textarea
                    id="requirements"
                    name="requirements"
                    className="min-h-24 w-full resize-y border border-[#4b1113] rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#4b1113]"
                ></textarea>
                <label htmlFor="salary" className="block text-[#4b1113] font-bold mb-2">
                    {t("personalSpaceEmployer.fields.salary")}
                </label>
                <input
                    type="text"
                    id="salary"
                    name="salary"
                    className="w-full border border-[#4b1113] rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#4b1113]"
                />
                <label htmlFor="startingDate" className="block text-[#4b1113] font-bold mb-2">
                    {t("personalSpaceEmployer.fields.startingDate")}
                </label>
                <input
                    type="date"
                    id="startingDate"
                    name="startingDate"
                    className="w-full border border-[#4b1113] rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#4b1113]"
                />
                <h2 className="text-[#4b1113] font-bold mb-2">{t("personalSpaceEmployer.fields.address")}</h2>
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <div>
                        <label htmlFor="city" className="block text-[#4b1113] font-bold mb-2">
                            {t("personalSpaceEmployer.fields.city")}
                        </label>
                        <input
                            type="text"
                            id="city"
                            name="city"
                            className="w-full border border-[#4b1113] rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#4b1113]"
                        />
                    </div>
                    <div>
                        <label htmlFor="country" className="block text-[#4b1113] font-bold mb-2">
                            {t("personalSpaceEmployer.fields.country")}
                        </label>
                        <input
                            type="text"
                            id="country"
                            name="country"
                            className="w-full border border-[#4b1113] rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#4b1113]"
                        />
                    </div>
                    <div>
                        <label htmlFor="postalCode" className="block text-[#4b1113] font-bold mb-2">
                            {t("personalSpaceEmployer.fields.postalCode")}
                        </label>
                        <input
                            type="text"
                            id="postalCode"
                            name="postalCode"
                            className="w-full border border-[#4b1113] rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#4b1113]"
                        />
                    </div>
                    <div>
                        <label htmlFor="civicNumber" className="block text-[#4b1113] font-bold mb-2">
                            {t("personalSpaceEmployer.fields.civicNumber")}
                        </label>
                        <input
                            type="text"
                            id="civicNumber"
                            name="civicNumber"
                            className="w-full border border-[#4b1113] rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#4b1113]"
                        />
                    </div>
                    <div className="sm:col-span-2">
                        <label htmlFor="street" className="block text-[#4b1113] font-bold mb-2">
                            {t("personalSpaceEmployer.fields.street")}
                        </label>
                        <input
                            type="text"
                            id="street"
                            name="street"
                            className="w-full border border-[#4b1113] rounded py-2 px-3 focus:outline-none focus:ring-2 focus:ring-[#4b1113]"
                        />
                    </div>
                </div>
                <button type="submit" className="bg-[#4b1113] text-white px-4 py-2 rounded hover:bg-[#6b1c1f] transition-colors duration-300">
                    {t("personalSpaceEmployer.submit")}
                </button>
            </div>
        </form>
    );
}

export default FormJobOffer;