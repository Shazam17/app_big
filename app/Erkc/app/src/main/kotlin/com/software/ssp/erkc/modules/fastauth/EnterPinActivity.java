package com.software.ssp.erkc.modules.fastauth;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.software.ssp.erkc.ErkcApplication;
import com.software.ssp.erkc.R;
import com.software.ssp.erkc.common.mvp.MvpActivity;
import com.software.ssp.erkc.common.views.pinLockView.IndicatorDots;
import com.software.ssp.erkc.common.views.pinLockView.PinLockListener;
import com.software.ssp.erkc.common.views.pinLockView.PinLockView;
import com.software.ssp.erkc.common.views.pinLockView.fingerprint.FingerPrintListener;
import com.software.ssp.erkc.common.views.pinLockView.fingerprint.FingerprintHandler;
import com.software.ssp.erkc.common.views.pinLockView.util.Animate;
import com.software.ssp.erkc.common.views.pinLockView.util.Utils;
import com.software.ssp.erkc.di.AppComponent;
import com.software.ssp.erkc.modules.drawer.DrawerActivity;
import com.software.ssp.erkc.modules.fastauth.createpin.CreatePinActivity;
import com.software.ssp.erkc.modules.signin.SignInActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.inject.Inject;

/**
 * Created by nixus on 02.01.2018.
 */

public class EnterPinActivity extends MvpActivity implements IEnterPinView {

    @Inject public IEnterPinPresenter presenter;

    public static final String TAG = "EnterPinActivity";
    public static final String EXTRA_FONT_TEXT = "textFont";
    public static final String EXTRA_FONT_NUM = "numFont";

    private boolean initialAuth = false;

    private static final int PIN_LENGTH = 4;
    private static final String FINGER_PRINT_KEY = "FingerPrintKey";

    public static final String PREFERENCES = "erkc.pinlock";
    public static final String KEY_PIN = "pin";
    public static final String SHOULD_SUGGEST_SET_PIN = "should_suggest_set_pin";

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private TextView mTextTitle;
    private TextView mTextAttempts;
    private TextView mTextFingerText;
    private AppCompatImageView mImageViewFingerView;

    private Cipher mCipher;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintManager mFingerprintManager;
    private KeyguardManager mKeyguardManager;
    private int mTryCount = 0;
    private String login = "";

    private AnimatedVectorDrawable showFingerprint;
    private AnimatedVectorDrawable fingerprintToTick;
    private AnimatedVectorDrawable fingerprintToCross;

    public static Intent getIntent(Context context, boolean setPin) {
        return new Intent(context, EnterPinActivity.class);
    }

    public static Intent getIntent(Context context, String fontText, String fontNum) {
        Intent intent = new Intent(context, EnterPinActivity.class);

        intent.putExtra(EXTRA_FONT_TEXT, fontText);
        intent.putExtra(EXTRA_FONT_NUM, fontNum);

        return intent;
    }

    public static Intent getIntent(Context context, boolean setPin, String fontText, String fontNum) {
        return getIntent(context, fontText, fontNum);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_enterpin);

        mTextAttempts = (TextView) findViewById(R.id.attempts);
        mTextTitle = (TextView) findViewById(R.id.title);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mImageViewFingerView = (AppCompatImageView) findViewById(R.id.fingerView);
        mTextFingerText = (TextView) findViewById(R.id.fingerText);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showFingerprint = (AnimatedVectorDrawable) ContextCompat.getDrawable(this, R.drawable.show_fingerprint);
            fingerprintToTick = (AnimatedVectorDrawable) ContextCompat.getDrawable(this, R.drawable.fingerprint_to_tick);
            fingerprintToCross = (AnimatedVectorDrawable) ContextCompat.getDrawable(this, R.drawable.fingerprint_to_cross);
        }

        Bundle b = getIntent().getExtras();
        if(b!=null) {
            initialAuth = b.getBoolean("initialAuth");
            getIntent().getExtras().remove("initialAuth");
        }

        mTextTitle.setText(R.string.pinlock_title);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0f);
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        }

        presenter.onViewAttached();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fast_auth_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            presenter.onArrowClosePressed();
            return true;
        } else if (item.getItemId() == R.id.change_user) {
            presenter.onArrowClosePressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void resolveDependencies(@NotNull AppComponent appComponent) {
        DaggerEnterPinComponent.builder()
                .appComponent(appComponent)
                .enterPinModule(new EnterPinModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void beforeDestroy() {
        presenter.dropView();
    }

    @Override
    public void setLogin(@NotNull String login) {
        this.login = login;
        ErkcApplication.login = login;

        String pin = getPinFromSharedPreferences();
        if (pin.equals("")) {
            /*Intent intent = new Intent(this, CreatePinActivity.class);
            startActivity(intent);*/
            this.finish();
        } else {
            checkForFingerPrint();
        }

        final PinLockListener pinLockListener = new PinLockListener() {

            @Override
            public void onComplete(String pin) {
                checkPin(pin);
            }

            @Override
            public void onEmpty() {
                Log.d(TAG, "Pin empty");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }

        };

        mPinLockView = (PinLockView) findViewById(R.id.pinlockView);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(pinLockListener);

        mPinLockView.setPinLength(PIN_LENGTH);

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        checkForFont();
    }

    private void checkForFont() {
        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_FONT_TEXT)) {

            String font = intent.getStringExtra(EXTRA_FONT_TEXT);
            setTextFont(font);
        }
        if (intent.hasExtra(EXTRA_FONT_NUM)) {
            String font = intent.getStringExtra(EXTRA_FONT_NUM);
            setNumFont(font);
        }
    }

    private void setTextFont(String font) {
        try {
            Typeface typeface = Typeface.createFromAsset(getAssets(), font);

            mTextTitle.setTypeface(typeface);
            mTextAttempts.setTypeface(typeface);
            mTextFingerText.setTypeface(typeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNumFont(String font) {
        try {
            Typeface typeface = Typeface.createFromAsset(getAssets(), font);

            mPinLockView.setTypeFace(typeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//
    private void generateKey() throws EnterPinActivity.FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            mKeyStore.load(null);

            //Initialize the KeyGenerator//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mKeyGenerator.init(new

                        //Specify the operation(s) this key can be used for//
                        KeyGenParameterSpec.Builder(FINGER_PRINT_KEY,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                        //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
            }

            //Generate the key//
            mKeyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            throw new EnterPinActivity.FingerprintException(exc);
        }
    }

    //Create a new method that we’ll use to initialize our mCipher//
    public boolean initCipher() {
        try {
            //Obtain a mCipher instance and configure it with the properties required for fingerprint authentication//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCipher = Cipher.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES + "/"
                                + KeyProperties.BLOCK_MODE_CBC + "/"
                                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            }
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(FINGER_PRINT_KEY,
                    null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the mCipher has been initialized successfully//
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private String getPinFromSharedPreferences() {
        SharedPreferences prefs = this.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PIN + login, "");
    }

    private void checkPin(String pin) {
        if (Utils.sha256(pin).equals(getPinFromSharedPreferences())) {
            presenter.saveAccessToken();
            setResult(RESULT_OK);
            if(initialAuth) {
                Intent intent = new Intent(this, DrawerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                this.finish();
            } else {
                finish();
            }
        } else {
            shake();

            mTryCount++;
            mTextAttempts.setText(getString(R.string.pinlock_wrongpin));
            mPinLockView.resetPinLockView();
            if (mTryCount > 2) {
                new AlertDialog.Builder(this)
                    .setMessage(R.string.fast_auth_pin_attemp_error_text)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences prefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                            prefs.edit().remove(KEY_PIN + login).apply();
                            prefs.edit().remove(SHOULD_SUGGEST_SET_PIN + login).apply();
                            presenter.onAttemptsFailed();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
            }
        }
    }

    private void shake() {
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(mPinLockView, "translationX",
                0, 25, -25, 25, -25, 15, -15, 6, -6, 0).setDuration(1000);
        objectAnimator.start();
    }

    private void checkForFingerPrint() {

        final FingerPrintListener fingerPrintListener = new FingerPrintListener() {

            @Override
            public void onSuccess() {
                if(fingerprintToTick!=null)
                    Animate.animate(mImageViewFingerView, fingerprintToTick);
                presenter.saveAccessToken();
                setResult(RESULT_OK);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(initialAuth) {
                            Intent intent = new Intent(EnterPinActivity.this, DrawerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            finish();
                        }

                    }
                }, 750);
            }

            @Override
            public void onFailed() {
                if(fingerprintToCross!=null)
                    Animate.animate(mImageViewFingerView, fingerprintToCross);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(showFingerprint!=null)
                            Animate.animate(mImageViewFingerView, showFingerprint);
                    }
                }, 750);
            }

            @Override
            public void onError(CharSequence errorString) {
                Toast.makeText(EnterPinActivity.this, errorString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHelp(CharSequence helpString) {
                Toast.makeText(EnterPinActivity.this, helpString, Toast.LENGTH_SHORT).show();
            }

        };

        // If you’ve set your app’s minSdkVersion to anything lower than 23, then you’ll need to verify that the device is running Marshmallow
        // or higher before executing any fingerprint-related code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Get an instance of KeyguardManager and FingerprintManager//
            mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            mFingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            //Check whether the device has a fingerprint sensor//
            if (mFingerprintManager == null){
                mImageViewFingerView.setVisibility(View.GONE);
                mTextFingerText.setVisibility(View.GONE);
            } else if (!mFingerprintManager.isHardwareDetected()) {
                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
//                textView.setText("Your device doesn't support fingerprint authentication");
                mImageViewFingerView.setVisibility(View.GONE);
                mTextFingerText.setVisibility(View.GONE);
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) { //Check whether the user has granted your app the USE_FINGERPRINT permission//
                // If your app doesn't have this permission, then display the following text//
//                Toast.makeText(CreatePinActivity.this, "Please enable the fingerprint permission", Toast.LENGTH_LONG).show();
                mImageViewFingerView.setVisibility(View.GONE);
                mTextFingerText.setVisibility(View.GONE);
            } else if (!mFingerprintManager.hasEnrolledFingerprints()) { //Check that the user has registered at least one fingerprint//
                // If the user hasn’t configured any fingerprints, then display the following message//
//                Toast.makeText(CreatePinActivity.this,
//                        "No fingerprint configured. Please register at least one fingerprint in your device's Settings",
//                        Toast.LENGTH_LONG).show();
                mImageViewFingerView.setVisibility(View.GONE);
                mTextFingerText.setVisibility(View.GONE);
            } else if (!mKeyguardManager.isKeyguardSecure()) {  //Check that the lockscreen is secured//
                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
//                Toast.makeText(CreatePinActivity.this, "Please enable lockscreen security in your device's Settings", Toast.LENGTH_LONG).show();
                mImageViewFingerView.setVisibility(View.GONE);
                mTextFingerText.setVisibility(View.GONE);
            } else {
                try {
                    generateKey();
                } catch (EnterPinActivity.FingerprintException e) {
                    Log.wtf(TAG, "Failed to generate key for fingerprint.", e);
                }

                if (initCipher()) {
                    //If the mCipher is initialized successfully, then create a CryptoObject instance//
                    mCryptoObject = new FingerprintManager.CryptoObject(mCipher);

                    // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                    // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuth(mFingerprintManager, mCryptoObject);
                    helper.setFingerPrintListener(fingerPrintListener);
                }
            }
        } else {
            mImageViewFingerView.setVisibility(View.GONE);
            mTextFingerText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public void navigateToMainScreen() {
        Intent intent = new Intent(this, DrawerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("nonAuthImitation", true);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void navigateToLoginScreen() {
        Intent intent = new Intent(this, DrawerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("nonAuthImitation", true);
        intent.putExtra("navigateToLogin", true);
        startActivity(intent);
        this.finish();
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }

}
