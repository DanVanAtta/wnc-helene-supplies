package com.vanatta.helene.supplies.database.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.vanatta.helene.supplies.database.TestConfiguration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CountyDaoTest {

  @BeforeAll
  static void setup() {
    TestConfiguration.setupDatabase();
  }

  @Test
  void fetchFullCountyListByState() {
    assertThat(CountyDao.fetchFullCountyListByState(TestConfiguration.jdbiTest, "NC"))
        .contains("dummy", "Watauga");
    assertThat(CountyDao.fetchFullCountyListByState(TestConfiguration.jdbiTest, "TN"))
        .doesNotContain("Watauga");

    assertThat(CountyDao.fetchFullCountyListByState(TestConfiguration.jdbiTest, "TN"))
        .contains("Sevier");
    assertThat(CountyDao.fetchFullCountyListByState(TestConfiguration.jdbiTest, "VA"))
        .contains("Halifax");
  }

  /**
   * Active counties are just the ones that have an active site. The list of active counties should
   * be shorter than the list of all counties.
   */
  @Test
  void fetchActiveCountyList() {
    int fullCountyCount =
        CountyDao.fetchFullCountyListByState(TestConfiguration.jdbiTest, "NC").size();
    List<String> activeCounties = CountyDao.fetchActiveCountyList(TestConfiguration.jdbiTest);

    assertThat(activeCounties.size()).isGreaterThan(0);
    assertThat(fullCountyCount).isGreaterThan(activeCounties.size());

    assertThat(activeCounties).contains("Watauga");
    assertThat(activeCounties).doesNotContain("dummy");
  }

  @Test
  void fetchFullCountyListing() {
    Map<String, List<String>> fetchFullCountyListing =
        CountyDao.fetchFullCountyListing(TestConfiguration.jdbiTest);
    assertThat(fetchFullCountyListing.size()).isGreaterThan(0);

    assertThat(fetchFullCountyListing.get("NC")).contains("Watauga");
    assertThat(fetchFullCountyListing.get("TN")).contains("Sevier");
    assertThat(fetchFullCountyListing.get("VA")).contains("Halifax");
  }
}
