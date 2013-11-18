package com.muzima.view.forms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.muzima.R;
import com.muzima.api.model.Patient;
import com.muzima.api.model.PatientIdentifier;
import com.muzima.api.model.PatientIdentifierType;
import com.muzima.model.BaseForm;
import com.muzima.utils.barcode.IntentIntegrator;
import com.muzima.utils.barcode.IntentResult;
import com.muzima.view.BaseFragmentActivity;

import java.util.Arrays;
import java.util.UUID;

import static java.lang.String.valueOf;


public class RegistrationBarcodeActivity extends BaseFragmentActivity {

    public static String SELECTED_REG_FORM = "selected_registration_form";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_barcode);

        Button barcodeButton = (Button) findViewById(R.id.patient_id_barcode_btn);
        barcodeButton.setOnClickListener(startBarCodeIntent());

        Button barCodeProceedButton = (Button) findViewById(R.id.bar_code_proceed_btn);
        barCodeProceedButton.setOnClickListener(startFormViewIntent());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String barCodeContent = intent.getStringExtra("SCAN_RESULT");
            TextView patientId = (TextView) findViewById(R.id.patient_id_barcode);
            patientId.setText(barCodeContent);
        }
    }

    private View.OnClickListener startBarCodeIntent() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(RegistrationBarcodeActivity.this);
                intentIntegrator.initiateScan();
            }
        };
    }

    private View.OnClickListener startFormViewIntent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseForm selectedForm = (BaseForm) getIntent().getSerializableExtra(SELECTED_REG_FORM);
                TextView patientBarCode = (TextView) findViewById(R.id.patient_id_barcode);
                Patient patient = new Patient();
                if (patientBarCode.getText() != null) {
                    PatientIdentifier patientIdentifier = new PatientIdentifier();
                    patientIdentifier.setIdentifier(valueOf(patientBarCode.getText()));
                    patientIdentifier.setPreferred(true);
                    PatientIdentifierType identifierType = new PatientIdentifierType();
                    identifierType.setName("OpenMRS Identifier");
                    identifierType.setUuid(valueOf(UUID.randomUUID()));
                    patientIdentifier.setIdentifierType(identifierType);
                    patient.setIdentifiers(Arrays.asList(patientIdentifier));
                }
                startActivity(new FormViewIntent(RegistrationBarcodeActivity.this, selectedForm, patient));
            }
        };
    }
}
