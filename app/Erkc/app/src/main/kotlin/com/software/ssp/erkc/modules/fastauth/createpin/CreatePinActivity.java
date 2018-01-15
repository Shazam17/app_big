package com.software.ssp.erkc.modules.fastauth.createpin;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.software.ssp.erkc.ErkcApplication;
import com.software.ssp.erkc.R;
import com.software.ssp.erkc.common.mvp.MvpActivity;
import com.software.ssp.erkc.common.views.pinLockView.IndicatorDots;
import com.software.ssp.erkc.common.views.pinLockView.PinLockListener;
import com.software.ssp.erkc.common.views.pinLockView.PinLockView;
import com.software.ssp.erkc.common.views.pinLockView.util.Utils;
import com.software.ssp.erkc.di.AppComponent;
import com.software.ssp.erkc.modules.drawer.DrawerActivity;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static com.software.ssp.erkc.modules.fastauth.EnterPinActivity.KEY_PIN;
import static com.software.ssp.erkc.modules.fastauth.EnterPinActivity.PREFERENCES;
import static com.software.ssp.erkc.modules.fastauth.EnterPinActivity.SHOULD_SUGGEST_SET_PIN;


public class CreatePinActivity extends MvpActivity implements ICreatePinView {

    @Inject public ICreatePinPresenter presenter;

    public static final String TAG = "CreatePinActivity";
    public static final String EXTRA_FONT_TEXT = "textFont";
    public static final String EXTRA_FONT_NUM = "numFont";

    private static final int PIN_LENGTH = 4;

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private TextView mTextTitle;
    private TextView mTextAttempts;
    private TextView mTextFingerText;
    private AppCompatImageView mImageViewFingerView;
    private String login = "";

    private String mFirstPin = "";

    public static Intent getIntent(Context context, boolean setPin) {
        return new Intent(context, CreatePinActivity.class);
    }

    public static Intent getIntent(Context context, String fontText, String fontNum) {
        Intent intent = new Intent(context, CreatePinActivity.class);

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

        mImageViewFingerView.setVisibility(View.GONE);
        mTextFingerText.setVisibility(View.GONE);
        mTextAttempts.setVisibility(View.GONE);
        mTextTitle.setText(getString(R.string.pinlock_set_title));

        final PinLockListener pinLockListener = new PinLockListener() {

            @Override
            public void onComplete(String pin) {
                setPin(pin);
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

        presenter.onViewAttached();
    }

    @Override
    public void resolveDependencies(@NotNull AppComponent appComponent) {
        DaggerCreatePinComponent.builder()
                .appComponent(appComponent)
                .createPinModule(new CreatePinModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void beforeDestroy() {
        presenter.dropView();
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

    private void writePinToSharedPreferences(String pin) {
        SharedPreferences prefs = this.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PIN + login, Utils.sha256(pin)).apply();
        prefs.edit().putBoolean(SHOULD_SUGGEST_SET_PIN + login, false).apply();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public void navigateToDrawerScreen() {
        Intent intent = new Intent(this, DrawerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void setLogin(@NotNull String login) {
        this.login = login;
        ErkcApplication.login = login;
    }

    private void setPin(String pin) {
        if (mFirstPin.equals("")) {
            mFirstPin = pin;
            mTextTitle.setText(getString(R.string.pinlock_secondPin));
            mPinLockView.resetPinLockView();
        } else {
            if (pin.equals(mFirstPin)) {
                presenter.saveAccessToken();
                writePinToSharedPreferences(pin);
                Intent intent = new Intent(this, DrawerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                shake();
                mTextTitle.setText(getString(R.string.pinlock_tryagain));
                mPinLockView.resetPinLockView();
                mFirstPin = "";
            }
        }
    }

    private void shake() {
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(mPinLockView, "translationX",
                0, 25, -25, 25, -25, 15, -15, 6, -6, 0).setDuration(1000);
        objectAnimator.start();
    }

}
