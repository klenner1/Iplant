package puc.iot.com.iplant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import puc.iot.com.iplant.models.Plant;
import puc.iot.com.iplant.models.Tap;
import puc.iot.com.iplant.utils.ImageUtils;
import puc.iot.com.iplant.utils.UtilsFireBase;

public class PlantRegistrationActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_QR_CODE = 0,REQUEST_CODE_PLANT_PHOTO=1;
    public static final String TAG = "RegistrationActivity";

    private EditText editTextEquipmentCode,editTextName;
    private AutoCompleteTextView aCTextViewPlantType;
    private ImageView imageViewPlantPhoto;
    private Bitmap mPhotoBitmap;
    private String userId;
    private List<String> mTypes;
    private Plant mPlant;
    private boolean mIsNew=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_registration);
        editTextEquipmentCode = findViewById(R.id.editTextEquipmentCode);
        editTextName = findViewById(R.id.editTextName);
        ImageView imageViewScanCode = findViewById(R.id.imageViewScanCode);
        imageViewPlantPhoto = findViewById(R.id.imageViewPlantPhoto);
        aCTextViewPlantType= findViewById(R.id.aCTextViewPlantType);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null)
            userId = user.getUid();
        else {
            Toast.makeText(getApplicationContext(),R.string.login_error,Toast.LENGTH_LONG).show();
            finish();
        }

        imageViewScanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlantRegistrationActivity.this,QRCodeScannerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_QR_CODE);
            }
        });
        imageViewPlantPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_CODE_PLANT_PHOTO);
            }
        });
        imageViewPlantPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPhotoBitmap=null;
                imageViewPlantPhoto.setImageResource(R.drawable.notf_n);
                return true;
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    PlantRegistrationActivity.this.finish();
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    save();
            }
        });
        editTextEquipmentCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String equipmentCode = s.toString();
                if (equipmentCode.length()>=8)
                    getValues(equipmentCode);
            }
        });
        getTypes();
    }

    private void save() {
        String equipmentCode = editTextEquipmentCode.getText().toString();
        String plantName = editTextName.getText().toString();
        String plantType = aCTextViewPlantType.getText().toString();
        if (equipmentCode.length()==0){
            makeSnackBar(R.string.equipment_code_is_required);
            return;
        }
        if (equipmentCode.length()<8){
            makeSnackBar(R.string.wrong_equipment_code);
            return;
        }
        if (plantName.length()==0){
            makeSnackBar(R.string.plant_name_is_required);
            return;
        }
        Plant plant;
        if (mIsNew) {
            plant = new Plant();
            plant.setHumidity(0f);
        } else
            plant = mPlant;

        if (plantType.length()>0){
            plant.setType(plantType);
        }
        plant.setName(plantName);
        plant.setId(equipmentCode);
        plant.addListener(userId);
        Tap tap = new Tap(equipmentCode,plantName,false);

        UtilsFireBase.getPlantsReference().child(equipmentCode).setValue(plant);
        UtilsFireBase.getUserPlantsReference(userId).child(equipmentCode).setValue(tap);
        if (mTypes!=null){
            if (!mTypes.contains(plantType))
            UtilsFireBase.getPlantsTypesReference().push().setValue(plantType);
        }else {
            UtilsFireBase.getPlantsTypesReference().push().setValue(plantType);
        }
        if (mPhotoBitmap!=null)
        upPicture(plant.getId());
        else
        finish();
    }

    private void upPicture(final String id) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();
        UtilsFireBase.getImagesStorageReference(id).putBytes(data);
    }

    private void makeSnackBar(int value) {
        Snackbar mySnackBar = Snackbar.make(findViewById(R.id.content),
                value, Snackbar.LENGTH_SHORT);
        mySnackBar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_QR_CODE:{
                if (resultCode==RESULT_OK){
                    String equipmentCode= Objects.requireNonNull(data.getExtras()).getString(QRCodeScannerActivity.RESULT_ACTIVITY);
                    editTextEquipmentCode.setText(equipmentCode);
                    getValues(equipmentCode);
                }else {
                    Toast.makeText(this,"Cancel",Toast.LENGTH_LONG).show();
                }
            }case REQUEST_CODE_PLANT_PHOTO:{
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    assert extras != null;
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    mPhotoBitmap = ImageUtils.decodeImageFile(bitmap);
                    imageViewPlantPhoto.setImageBitmap(mPhotoBitmap);
                }
            }
        }
    }

    public void getTypes() {
        UtilsFireBase.getPlantsTypesReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> types = new ArrayList<>();
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    types.add(String.valueOf(dsp.getKey()));
                }
                mTypes = types;
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line, types);
                aCTextViewPlantType.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getValues(String equipmentCode) {
        if (validCode(equipmentCode)) {
            UtilsFireBase.getPlantReference(equipmentCode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        Plant plant = dataSnapshot.getValue(Plant.class);
                        if (plant != null) {
                            mPlant = plant;
                            editTextName.setText(plant.getName());
                            aCTextViewPlantType.setText(plant.getType());
                            mIsNew = false;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private boolean validCode(String equipmentCode) {
        return !equipmentCode.contains(".")&&
                !equipmentCode.contains("#")&&
                !equipmentCode.contains("%")&&
                !equipmentCode.contains("$")&&
                !equipmentCode.contains("[")&&
                !equipmentCode.contains("]");
    }
}
