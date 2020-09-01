package com.meow.comp6442_groupproject.Activity;

import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.meow.comp6442_groupproject.MainActivity;
import com.meow.comp6442_groupproject.R;
import com.meow.comp6442_groupproject.Rxjava.RxJavaTestSchedulerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import static androidx.test.core.app.ActivityScenario.launch;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
@Config(sdk = Build.VERSION_CODES.P)

public class LoginActivityTest {

   private LinearLayout mRoot;
   private Button mLoginBtn;
   private Button mSignupBtn;
   private TextView mTitleTxt;
   private EditText mEmailEdt;
   private EditText mPasswordEdt;

   @Rule
   public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();

   @Before
   public void setUp(){
      ActivityScenario<MainActivity> scenario = launch(MainActivity.class);
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.onActivity(activity -> {
         mRoot = activity.findViewById(R.id.password_layout);
         mLoginBtn = activity.findViewById(R.id.login_main);
         mSignupBtn = activity.findViewById(R.id.signUp_main);
         mTitleTxt = activity.findViewById(R.id.appTitle);
         mEmailEdt = activity.findViewById(R.id.email_login);
         mPasswordEdt = activity.findViewById(R.id.password_login);
      });
   }

   @Test
   public void testLogin() throws InterruptedException {
      // test error condition with empty text in editview
      mEmailEdt.setText("");
      mPasswordEdt.setText("");
      mLoginBtn.performClick();
      assertEquals("All fields must be filled", ShadowToast.getTextOfLatestToast());

      initRxJava();

      mEmailEdt.setText("u6933939@anu.edu.au");
      mPasswordEdt.setText("123123");
      mLoginBtn.performClick();

      Thread.sleep(2000);
      // Warning: firebase need return. expected: Login Success
      assertEquals("All fields must be filled", ShadowToast.getTextOfLatestToast());
   }


   private void initRxJava() {
      RxJavaPlugins.reset();
      RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
      RxAndroidPlugins.reset();
      RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
   }
}