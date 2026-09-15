import UserFields from "./UserFields";

const InscriptionForm = ({ role, errors, programmes, handleSubmit }) => (
  <form onSubmit={handleSubmit}>
    <div className="flex flex-col gap-4">
      <UserFields role={role} errors={errors} programmes={programmes} />
    </div>
    <div>
      <button
        type="submit"
        className="
        w-full mt-6
        bg-[#4b1113]
        text-white
        font-medium
        py-2
        rounded-md
        hover:bg-[#3a0d0f]
        transition-colors"
      >
        S'inscrire
      </button>
    </div>
  </form>
);

export default InscriptionForm;
