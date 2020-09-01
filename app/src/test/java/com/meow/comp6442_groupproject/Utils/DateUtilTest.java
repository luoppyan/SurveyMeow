package com.meow.comp6442_groupproject.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;


@RunWith(Parameterized.class)
public class DateUtilTest {

   private String time;
   private Date date;



   public DateUtilTest(String time, Date date) {
      this.time = time;
      this.date = date;
   }



   // testing parameters
   @Parameterized.Parameters
   public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] { { "2017-10-15", new Date(1508054402001L) },
              { "2017/10/15 16/00/02", new Date(1508054402000L) }});
   }



   // test method dataToStamp
   @Test(expected = ParseException.class)
   public void dateToStampTest() throws Exception{
      DateUtil.dateToStamp(time);
   }



   // test method stampToDate
   @Test
   public void stampToDateTest(){
      DateUtil.stampToDate(date);
   }

}