package com.meow.comp6442_groupproject.Assert;

import org.assertj.core.util.Lists;
import org.assertj.core.util.Maps;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.util.DateUtil.*;
import static org.assertj.core.util.Lists.newArrayList;


public class AssertJTest {

   @Test
   public void testString(){
      String str = null;
      // assert null or empty string
      assertThat(str).isNullOrEmpty();
      assertThat("").isEmpty();
      // assert correctness of string ignoring case
      assertThat("miaow").isEqualTo("miaow").isEqualToIgnoringCase("MiaoW");
      // assert the start and end char as well as its size
      assertThat("miaow").startsWith("m").endsWith("ow").hasSize(5);
      // assert string contains
      assertThat("miaow").contains("ao").doesNotContain("ae");
      // assert string contains only
      assertThat("miaow").containsOnlyOnce("mi");
      // assert regex match
      assertThat("miaow").matches(".i.o.").doesNotMatch(".*d");
   }

   // assert number correctness
   @Test
   public void testNumber() {
      Integer num = null;
      assertThat(num).isNull();
      assertThat(42).isEqualTo(42);
      assertThat(42).isGreaterThan(38).isGreaterThanOrEqualTo(38);
      assertThat(42).isLessThan(58).isLessThanOrEqualTo(58);
      assertThat(0).isZero();
      assertThat(1).isPositive().isNotNegative();
      assertThat(-1).isNegative().isNotPositive();
   }

   // assert date parse
   @Test
   public void testDate() {
      assertThat(parse("2020-05-15"))
              .isEqualTo("2020-05-15")
              .isNotEqualTo("2020-05-14")
              .isAfter("2020-04-01")
              .isBefore("2029-03-01");
      assertThat(now()).isBeforeYear(2030).isAfterYear(2017);
      assertThat(parse("2020-05-15"))
              .isBetween("2020-04-01", "2020-06-01")
              .isNotBetween("2019-01-01", "2019-12-31");

      Date d1 = new Date();
      Date d2 = new Date(d1.getTime() + 100);
      assertThat(d1).isCloseTo(d2, 100);

      Date date1 = parseDatetimeWithMs("2020-01-01T01:00:00.000");
      Date date2 = parseDatetimeWithMs("2020-01-01T01:00:00.555");
      Date date3 = parseDatetimeWithMs("2020-01-01T01:00:55.555");
      Date date4 = parseDatetimeWithMs("2020-01-01T01:55:55.555");
      Date date5 = parseDatetimeWithMs("2020-01-01T05:55:55.555");

      assertThat(date1).isEqualToIgnoringMillis(date2);
      assertThat(date1).isInSameSecondAs(date2);
      assertThat(date1).isEqualToIgnoringSeconds(date3);
      assertThat(date1).isInSameMinuteAs(date3);
      assertThat(date1).isEqualToIgnoringMinutes(date4);
      assertThat(date1).isInSameHourAs(date4);
      assertThat(date1).isEqualToIgnoringHours(date5);
      assertThat(date1).isInSameDayAs(date5);
   }

   // assert list
   @Test
   public void testList() {
      assertThat(newArrayList()).isEmpty();
      assertThat(newArrayList(1, 2, 3)).startsWith(1).endsWith(3);
      assertThat(newArrayList(1, 2, 3))
              .contains(1, atIndex(0))
              .contains(2, atIndex(1))
              .contains(3)
              .isSorted();
      assertThat(newArrayList(3, 1, 2)).isSubsetOf(newArrayList(1, 2, 3, 4));
      assertThat(Lists.newArrayList("a", "b", "c")).containsOnlyOnce("a");
   }

   // assert map
   @Test
   public void testMap() {
      Map<String, Integer> foo = Maps.newHashMap("A", 1);
      foo.put("B", 2);
      foo.put("C", 3);

      assertThat(foo).isNotEmpty().hasSize(3);
      assertThat(foo).contains(entry("A", 1), entry("B", 2));
      assertThat(foo).containsKeys("A", "B", "C");
      assertThat(foo).containsValue(3);
   }

}