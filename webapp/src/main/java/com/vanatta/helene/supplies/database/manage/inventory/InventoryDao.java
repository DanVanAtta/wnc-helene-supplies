package com.vanatta.helene.supplies.database.manage.inventory;

import com.vanatta.helene.supplies.database.data.ItemStatus;
import com.vanatta.helene.supplies.database.manage.ManageSiteDao;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;

@Slf4j
public class InventoryDao {

  // TODO: handle null case (can be possible if a person is using multiple browser windows
  static ItemStatus fetchItemStatus(Jdbi jdbi, long siteId, String itemName) {
    String query =
        """
        select ist.name
        from item_status ist
        join site_item si on si.item_status_id = ist.id
        join item i on i.id = si.item_id
        where i.name = :itemName and si.site_id = :siteId
        """;
    String status =
        jdbi.withHandle(
            handle ->
                handle
                    .createQuery(query)
                    .bind("siteId", siteId)
                    .bind("itemName", itemName)
                    .mapTo(String.class)
                    .one());
    return ItemStatus.fromTextValue(status);
  }

  public static void updateSiteItemActive(
      Jdbi jdbi, long siteId, String itemName, String itemStatus) {
    String insert =
        """
          insert into site_item(site_id, item_id, item_status_id) values
             (
                :siteId,
                (select id from item where name = :itemName),
                (select id from item_status where name = :itemStatus)
             )
          """;
    try {
      jdbi.withHandle(
          handle ->
              handle
                  .createUpdate(insert)
                  .bind("siteId", siteId)
                  .bind("itemName", itemName)
                  .bind("itemStatus", itemStatus)
                  .execute());
      updateSiteItemAudit(jdbi, siteId, itemName, "inactive", "active");
    } catch (Exception e) {
      if (e.getMessage().contains("already exists.")
          || (e.getCause() != null && e.getCause().getMessage().contains("duplicate key value"))) {
        log.warn(
            "Duplicate key insert attempted, siteId: {}, itemName: {}, itemStatus: {}",
            siteId,
            itemName,
            itemStatus);
      } else {
        throw e;
      }
    }
    ManageSiteDao.updateSiteInventoryLastUpdatedAndOnboardedFlag(jdbi, siteId);
  }

  /** Adds a record to the site item change table. */
  // @VisibleForTesting
  static void updateSiteItemAudit(
      Jdbi jdbi, long siteId, String itemName, String oldValue, String newValue) {

    String insertIntoAudit =
        """
        insert into site_item_audit(
          site_id,
          item_id,
          old_value,
          new_value
        ) values (
          :siteId,
          (select id from item where name = :itemName),
          :oldValue,
          :newValue
        );
        """;
    jdbi.withHandle(
        handle ->
            handle
                .createUpdate(insertIntoAudit)
                .bind("siteId", siteId)
                .bind("itemName", itemName)
                .bind("oldValue", oldValue)
                .bind("newValue", newValue)
                .execute());
  }

  static void updateSiteItemInactive(Jdbi jdbi, long siteId, String itemName) {
    String delete =
        """
            delete from site_item
            where site_id = :siteId
              and item_id = (select id from item where name = :itemName)
            """;
    int deleteCount =
        jdbi.withHandle(
            handle ->
                handle
                    .createUpdate(delete)
                    .bind("siteId", siteId)
                    .bind("itemName", itemName)
                    .execute());
    if (deleteCount > 0) {
      updateSiteItemAudit(jdbi, siteId, itemName, "active", "inactive");
    }
    ManageSiteDao.updateSiteInventoryLastUpdatedAndOnboardedFlag(jdbi, siteId);
  }

  public static void updateItemStatus(Jdbi jdbi, long siteId, String itemName, String itemStatus) {
    if (!ItemStatus.allItemStatus().contains(itemStatus)) {
      throw new IllegalArgumentException("Invalid item status: " + itemStatus);
    }
    String oldStatusQuery =
        """
        select its.name
        from site_item si
        join item_status its on its.id = si.item_status_id
        where si.site_id = :siteId
          and si.item_id = (select id from item where name = :itemName)
        """;
    String oldStatus =
        jdbi.withHandle(
            handle ->
                handle
                    .createQuery(oldStatusQuery)
                    .bind("siteId", siteId)
                    .bind("itemName", itemName)
                    .mapTo(String.class)
                    .one());

    String update =
        """
      update site_item
      set item_status_id = (select id from item_status where name = :itemStatus),
         last_updated = now()
      where site_id = :siteId
         and item_id = (select id from item where name = :itemName)
      """;
    int updateCount =
        jdbi.withHandle(
            handle ->
                handle
                    .createUpdate(update)
                    .bind("siteId", siteId)
                    .bind("itemName", itemName)
                    .bind("itemStatus", itemStatus)
                    .execute());

    if (updateCount != 1) {
      throw new IllegalArgumentException(String.format("Invalid item name: %s", itemName));
    }
    updateSiteItemAudit(jdbi, siteId, itemName, oldStatus, itemStatus);
    ManageSiteDao.updateSiteInventoryLastUpdatedAndOnboardedFlag(jdbi, siteId);
  }

  /**
   * Adds a brand new item to database, inserts into item table. No-op if the item already exists.
   * Item will exist when this method is done.
   *
   * @return True if the item was added, false if the item already exists.
   */
  public static boolean addNewItem(Jdbi jdbi, String itemName) {
    // we explicitly select for an item rather than "insert & let it fail"
    // the latter is more performant, but we'll get error messages in the logs if we do that.
    // The "check & insert" method does suffer from a time-of-check vs time-of-execution error
    String select = "select id from item where name = :itemName";

    Long result =
        jdbi.withHandle(
                handle ->
                    handle
                        .createQuery(select)
                        .bind("itemName", itemName)
                        .mapTo(Long.class)
                        .findOne())
            .orElse(null);
    if (result != null) {
      return false;
    } else {
      try {
        String insert = "insert into item(name) values(:itemName)";
        jdbi.withHandle(handle -> handle.createUpdate(insert).bind("itemName", itemName).execute());
      } catch (Exception e) {
        if (e.getMessage().contains("duplicate key")) {
          return false;
        } else {
          throw e;
        }
      }
      return true;
    }
  }
}
