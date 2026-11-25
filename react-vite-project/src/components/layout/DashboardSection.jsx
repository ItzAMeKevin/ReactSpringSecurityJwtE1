import { sectionTitle, textMuted, textError } from "@/styles/classes.js";

const DashboardSection = ({ title, status, isEmpty, loadingText, errorText, emptyText, actions, children }) => (
    <section className="w-full rounded-xl border border-brand/20 bg-white/70 p-6">
        <div className="flex items-start justify-between gap-4">
            <h2 className={`${sectionTitle} flex-1`}>{title}</h2>
            {actions}
        </div>
        {status === "loading" && <p className={textMuted}>{loadingText}</p>}
        {status === "error" && <p className={textError} role="alert">{errorText}</p>}
        {status === "loaded" && isEmpty && <p className={textMuted}>{emptyText}</p>}
        {status === "loaded" && !isEmpty && <div className="flex flex-col gap-3">{children}</div>}
    </section>
);

export default DashboardSection;
