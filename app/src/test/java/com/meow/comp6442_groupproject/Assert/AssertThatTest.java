package com.meow.comp6442_groupproject.Assert;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AssertThatTest {

   @Rule
   public MyRule rule = new MyRule();

   @Test
   public void testAssertThat1() throws Exception {
      assertThat(6, is(6));
   }

   @Test
   public void testAssertThat2() throws Exception {
      assertThat(null, nullValue());
   }

   @Test
   public void testAssertThat3() throws Exception {
      assertThat("Survey Meow", both(startsWith("Survey")).and(endsWith("Meow")));
   }

}