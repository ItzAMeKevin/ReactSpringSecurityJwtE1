package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class JobApplicationTest {

    private JobApplication applicationIn(Status status) {
        JobApplication application = new JobApplication();
        application.setStatus(status);
        return application;
    }

    private void assertRefused(JobApplication application, Runnable transition, String errorKey, Status unchanged) {
        InvalidStateTransitionException exception = assertThrows(InvalidStateTransitionException.class, transition::run);
        assertEquals(errorKey, exception.getMessage());
        assertEquals(unchanged, application.getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"PENDING", "PENDING_INTERVIEW", "INTERVIEW_SCHEDULED", "INTERVIEW_COMPLETED"})
    @DisplayName("offerHire passe une candidature ouverte à HIRE_PENDING")
    void offerHire_fromOpenStatus_setsHirePending(Status status) {
        JobApplication application = applicationIn(status);

        application.offerHire();

        assertEquals(Status.HIRE_PENDING, application.getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, mode = EXCLUDE, names = {"PENDING", "PENDING_INTERVIEW", "INTERVIEW_SCHEDULED", "INTERVIEW_COMPLETED"})
    @DisplayName("offerHire refuse une candidature qui n'est plus ouverte")
    void offerHire_fromClosedStatus_throws(Status status) {
        JobApplication application = applicationIn(status);

        assertRefused(application, application::offerHire, "errors.applicationNotOpen", status);
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"PENDING", "PENDING_INTERVIEW", "INTERVIEW_SCHEDULED", "INTERVIEW_COMPLETED"})
    @DisplayName("reject passe une candidature ouverte à DECLINED")
    void reject_fromOpenStatus_setsDeclined(Status status) {
        JobApplication application = applicationIn(status);

        application.reject();

        assertEquals(Status.DECLINED, application.getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, mode = EXCLUDE, names = {"PENDING", "PENDING_INTERVIEW", "INTERVIEW_SCHEDULED", "INTERVIEW_COMPLETED"})
    @DisplayName("reject refuse une candidature qui n'est plus ouverte")
    void reject_fromClosedStatus_throws(Status status) {
        JobApplication application = applicationIn(status);

        assertRefused(application, application::reject, "errors.applicationNotOpen", status);
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"HIRE_PENDING"})
    @DisplayName("acceptHire passe une offre d'embauche en attente à AWAITING_CONTRACT")
    void acceptHire_fromHirePending_setsAwaitingContract(Status status) {
        JobApplication application = applicationIn(status);

        application.acceptHire();

        assertEquals(Status.AWAITING_CONTRACT, application.getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, mode = EXCLUDE, names = {"HIRE_PENDING"})
    @DisplayName("acceptHire refuse une candidature sans offre d'embauche en attente")
    void acceptHire_fromOtherStatus_throws(Status status) {
        JobApplication application = applicationIn(status);

        assertRefused(application, application::acceptHire, "errors.applicationNotHirePending", status);
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"HIRE_PENDING"})
    @DisplayName("declineHire passe une offre d'embauche en attente à DECLINED")
    void declineHire_fromHirePending_setsDeclined(Status status) {
        JobApplication application = applicationIn(status);

        application.declineHire();

        assertEquals(Status.DECLINED, application.getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, mode = EXCLUDE, names = {"HIRE_PENDING"})
    @DisplayName("declineHire refuse une candidature sans offre d'embauche en attente")
    void declineHire_fromOtherStatus_throws(Status status) {
        JobApplication application = applicationIn(status);

        assertRefused(application, application::declineHire, "errors.applicationNotHirePending", status);
    }
}
