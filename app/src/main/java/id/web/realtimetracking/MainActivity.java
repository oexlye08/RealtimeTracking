package id.web.realtimetracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import id.web.realtimetracking.Model.User;
import id.web.realtimetracking.Utils.Common;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    DatabaseReference user_information;
    private static final int MY_REQUEST_CODE = 7117; //any number you want
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Paper.init(this);

        //init firebase
        user_information = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION);

        //init providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        //Request Permission Location
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        showSignInOptions();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "You Must Accept Permission to use App", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    private void showSignInOptions() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
        .build(),MY_REQUEST_CODE);
    }

    //ctrl+o

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK)
            {
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                //chechk if user exist on db
                user_information.orderByKey()
                        .equalTo(firebaseUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null) //if user is not exist
                                {
                                    if (!dataSnapshot.child(firebaseUser.getUid()).exists()) //if key uid is not exist
                                    {
                                        Common.loggedUser = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                                        //Add to database
                                        user_information.child(Common.loggedUser.getUid())
                                                .setValue(Common.loggedUser);
                                    }
                                }
                                else  //if users is available
                                {
                                    Common.loggedUser = dataSnapshot.child(firebaseUser.getUid()).getValue(User.class);
                                }

                                //save UID to storage to update location from background
                                Paper.book().write(Common.USER_UID_SAVE_KEY, Common.loggedUser.getUid());
                                updateToken(firebaseUser);
                                setupUI();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }
    }

    private void setupUI() {
        //NavigateHome
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }

    private void updateToken(final FirebaseUser firebaseUser) {
        final DatabaseReference tokens = FirebaseDatabase.getInstance()
                .getReference(Common.TOKENS);

        //Get Token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        tokens.child(firebaseUser.getUid())
                                .setValue(instanceIdResult.getToken());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
