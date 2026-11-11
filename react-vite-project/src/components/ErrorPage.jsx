import React from "react";
import { useTranslation } from "react-i18next";

function ErrorPage({error}) {
  const { t } = useTranslation();
  return <p>{t(error?.message?.startsWith("errors.") ? error.message : "errors.generic")}</p>;
}
export default ErrorPage;
