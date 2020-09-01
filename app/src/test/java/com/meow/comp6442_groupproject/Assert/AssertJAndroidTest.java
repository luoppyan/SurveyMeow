package com.meow.comp6442_groupproject.Assert;

import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.meow.comp6442_groupproject.R;
import com.meow.comp6442_groupproject.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.core.app.ActivityScenario.launch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
@Config(sdk = Build.VERSION_CODES.P)

public class AssertJAndroidTest {

   private LinearLayout mRoot;
   private Button mLoginBtn;
   private Button mSignupBtn;
   private TextView mTitleTxt;
   private EditText mEmailEdt;
   private EditText mPasswordEdt;

   @Before
   public void setUp(){
      // get main activity through activity scenario
      ActivityScenario<MainActivity> scenario = launch(MainActivity.class);
      scenario.moveToState(Lifecycle.State.CREATED);
      // initialize widget variables
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
   public void testView() {
      // test visibility of widgets and text display
      assertThat(mLoginBtn.getVisibility()).isEqualTo(View.VISIBLE);
      assertEquals(mLoginBtn.getText().toString(), "Log in");
      assertThat(mSignupBtn.getVisibility()).isEqualTo(View.VISIBLE);
      assertEquals(mSignupBtn.getText().toString(), "Register a new account");

      // test layout orientation and child count
      assertThat(mRoot.getOrientation()).isEqualTo(LinearLayout.VERTICAL);
      assertThat(mRoot.getChildCount()).isEqualTo(2);

      assertThat(mTitleTxt.getVisibility()).isEqualTo(View.VISIBLE);
      assertThat(mEmailEdt.getVisibility()).isEqualTo(View.VISIBLE);
      assertThat(mPasswordEdt.getVisibility()).isEqualTo(View.VISIBLE);
   }
}