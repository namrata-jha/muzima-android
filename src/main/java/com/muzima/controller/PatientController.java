package com.muzima.controller;

import android.util.Log;
import com.muzima.api.model.CohortMember;
import com.muzima.api.model.Patient;
import com.muzima.api.model.PatientIdentifier;
import com.muzima.api.service.CohortService;
import com.muzima.api.service.PatientService;
import org.apache.lucene.queryParser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.muzima.utils.Constants.LOCAL_PATIENT;
import static org.apache.commons.lang.StringUtils.isEmpty;

public class PatientController {

    public static final String TAG = "PatientController";
    private PatientService patientService;
    private CohortService cohortService;

    public PatientController(PatientService patientService, CohortService cohortService) {
        this.patientService = patientService;
        this.cohortService = cohortService;
    }

    public void replacePatients(List<Patient> patients) throws PatientReplaceException {
        try {
            patientService.updatePatients(patients);
        } catch (IOException e) {
            throw new PatientReplaceException(e);
        }
    }

    public List<Patient> getPatients(String cohortId) throws PatientLoadException {
        try {
            List<CohortMember> cohortMembers = cohortService.getCohortMembers(cohortId);
            List<Patient> patients = new ArrayList<Patient>();
            for (CohortMember member : cohortMembers) {
                patients.add(patientService.getPatientByUuid(member.getPatientUuid()));
            }
            return patients;
        } catch (IOException e) {
            throw new PatientLoadException(e);
        }
    }

    public List<Patient> getAllPatients() throws PatientLoadException {
        try {
            return patientService.getAllPatients();
        } catch (IOException e) {
            throw new PatientLoadException(e);
        }
    }

    public int getTotalPatientsCount() throws PatientLoadException {
        try {
            return patientService.countAllPatients();
        } catch (IOException e) {
            throw new PatientLoadException(e);
        }
    }

    public Patient getPatientByUuid(String uuid) throws PatientLoadException {
        try {
            return patientService.getPatientByUuid(uuid);
        } catch (IOException e) {
            throw new PatientLoadException(e);
        }
    }

    public List<Patient> searchPatientLocally(String term, String cohortUuid) throws PatientLoadException {
        try {
            return isEmpty(cohortUuid)
                    ? patientService.searchPatients(term)
                    : patientService.searchPatients(term, cohortUuid);
        } catch (IOException e) {
            throw new PatientLoadException(e);
        } catch (ParseException e) {
            throw new PatientLoadException(e);
        }
    }


    public List<Patient> getPatientsForCohorts(String[] cohortUuids) throws PatientLoadException {
        List<Patient> allPatients = new ArrayList<Patient>();
        for (String cohortUuid : cohortUuids) {
            try {
                List<Patient> patients = getPatients(cohortUuid);
                allPatients.addAll(patients);
            } catch (PatientLoadException e) {
                throw new PatientLoadException(e);
            }
        }
        return allPatients;
    }

    public List<Patient> searchPatientOnServer(String name) {
        try {
            return patientService.downloadPatientsByName(name);
        } catch (IOException e) {
            Log.e(TAG, "Error while searching for patients in the server");
        }
        return new ArrayList<Patient>();
    }

    public List<Patient> getAllPatientsCreatedLocallyAndNotSynced() {
        //TODO: Try to replace this google guava
        try {
            List<Patient> localPatients = new ArrayList<Patient>();
            List<Patient> allPatients = getAllPatients();
            for (Patient patient : allPatients) {
                PatientIdentifier localPatientIdentifier = patient.getIdentifier(LOCAL_PATIENT);
                if (localPatientIdentifier != null && localPatientIdentifier.getIdentifier().equals(patient.getUuid())) {
                    localPatients.add(patient);
                }
            }
            return localPatients;
        } catch (PatientLoadException e) {
            Log.e(TAG, "Error while loading local patients");
        }
        return new ArrayList<Patient>();
    }

    public Patient consolidateTemporaryPatient(Patient patient) {
        try {
            return patientService.consolidateTemporaryPatient(patient);
        } catch (IOException e) {
            Log.e(TAG, "Error while consolidating the temporary patient.");
        }
        return null;
    }

    public void savePatient(Patient patient) {
        try {
            patientService.savePatient(patient);
        } catch (IOException e) {
            Log.e(TAG, "Error while saving the patient : " + patient.getUuid());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void deletePatient(Patient localPatient) {
        try {
            patientService.deletePatient(localPatient);
        } catch (IOException e) {
            Log.e(TAG, "Error while deleting local patient : " + localPatient.getUuid());
        }
    }


    public static class PatientReplaceException extends Throwable {
        public PatientReplaceException(Throwable throwable) {
            super(throwable);
        }
    }

    public static class PatientLoadException extends Throwable {
        public PatientLoadException(Throwable e) {
            super(e);
        }
    }
}
