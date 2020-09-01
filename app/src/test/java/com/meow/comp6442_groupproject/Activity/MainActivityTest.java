package com.meow.comp6442_groupproject.Activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.meow.comp6442_groupproject.MainActivity;
import com.meow.comp6442_groupproject.R;
import com.meow.comp6442_groupproject.SignUpActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLog;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(AndroidJUnit4.class)
@Config(sdk = Build.VERSION_CODES.P)

public class MainActivityTest {

   @Rule
   public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

   private final String TAG = "test";

   private MainActivity mainActivity;
   private Button mLoginBtn;
   private Button mSignupBtn;
   private TextView mTitleTxt;
   private EditText mEmailEdt;
   private EditText mPasswordEdt;

   @Before
   public void setUp() {
      ShadowLog.stream = System.out;

      mainActivity = activityRule.getActivity();
      mLoginBtn = mainActivity.findViewById(R.id.login_main);
      mSignupBtn = mainActivity.findViewById(R.id.signUp_main);
      mTitleTxt = mainActivity.findViewById(R.id.appTitle);
      mEmailEdt = mainActivity.findViewById(R.id.email_login);
      mPasswordEdt = mainActivity.findViewById(R.id.password_login);
   }

   @Test
   public void useAppContext() {
      // Context of the app under test.
      Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
      assertEquals("com.meow.comp6442_groupproject", appContext.getPackageName());
   }

   @Test
   public void testMainActivity() {
      // test main activity not null
      assertNotNull(mainActivity);
      Log.d(TAG, "Test log output");
   }

   @Test
   public void testJumpToRegister() {
      assertEquals(mSignupBtn.getText().toString(), "Register a new account");

      mSignupBtn.performClick();
      // test the next activity is match with expected
      ShadowActivity shadowActivity = Shadows.shadowOf(mainActivity);
      Intent nextIntent = shadowActivity.getNextStartedActivity();
      assertEquals(nextIntent.getComponent().getClassName(), SignUpActivity.class.getName());
   }

   @Test
   public void testResources() {
      // test survey name is match with expected
      Application application = getApplicationContext();
      String appName = application.getString(R.string.app_name);
      assertEquals("SurveyMeow", appName);
   }
}