package com.vanatta.helene.supplies.database.manage;

import com.vanatta.helene.supplies.database.data.CountyDao;
import com.vanatta.helene.supplies.database.data.ItemStatus;
import com.vanatta.helene.supplies.database.data.SiteType;
import com.vanatta.helene.supplies.database.data.export.NewItemUpdate;
import com.vanatta.helene.supplies.database.data.export.SendInventoryUpdate;
import com.vanatta.helene.supplies.database.data.export.SendSiteUpdate;
import com.vanatta.helene.supplies.database.manage.add.site.AddSiteDao;
import com.vanatta.helene.supplies.database.manage.add.site.AddSiteData;
import com.vanatta.helene.supplies.database.site.details.SiteDetailDao;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@Slf4j
public class ManageSiteController {

  private final Jdbi jdbi;
  private final SendSiteUpdate sendSiteUpdate;
  private final NewItemUpdate newItemUpdate;
  private final SendInventoryUpdate sendInventoryUpdate;

  @Builder
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class SiteSelection {
    Long id;
    String name;
  }

  /** User will be shown a page to select the site they want to manage. */
  @GetMapping("/manage/select-site")
  ModelAndView selectSite() {
    Map<String, Object> pageParams = new HashMap<>();
    pageParams.put("sites", ManageSiteDao.fetchSiteList(jdbi));
    return new ModelAndView("manage/select-site", pageParams);
  }

  /**
   * After a site is selected, user selects which aspect they want to manage (eg: inventory, status)
   */
  @GetMapping("/manage/site-selected")
  ModelAndView siteSelected(
      //      @CookieValue(value = "auth") String auth,
      @RequestParam String siteId) {

    String siteName = fetchSiteName(siteId);
    if (siteName == null) {
      return selectSite();
    }

    Map<String, String> pageParams = new HashMap<>();
    pageParams.put("siteName", siteName);
    pageParams.put("siteId", siteId);
    return new ModelAndView("manage/site-selected", pageParams);
  }

  /** Returns null if ID is not valid or DNE. */
  String fetchSiteName(String siteId) {
    if (siteId == null || siteId.isBlank()) {
      return null;
    }

    try {
      long id = Long.parseLong(siteId);
      return ManageSiteDao.fetchSiteName(jdbi, id);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /** Fetches data for the manage site page */
  @GetMapping("/manage/contact")
  ModelAndView manageContact(String siteId) {
    Map<String, Object> pageParams = new HashMap<>();

    String siteName = fetchSiteName(siteId);
    if (siteName == null) {
      return selectSite();
    }

    SiteDetailDao.SiteDetailData data = SiteDetailDao.lookupSiteById(jdbi, Long.parseLong(siteId));
    pageParams.put("siteId", siteId);
    pageParams.put("siteName", data.getSiteName());
    pageParams.put("address", data.getAddress());
    pageParams.put("siteContact", Optional.ofNullable(data.getContactNumber()).orElse(""));
    pageParams.put("website", Optional.ofNullable(data.getWebsite()).orElse(""));
    pageParams.put("city", Optional.ofNullable(data.getCity()).orElse(""));
    pageParams.put("state", Optional.ofNullable(data.getState()).orElse(""));

    // fetch all counties, mark the county of this site as "selected"
    var countyListing =
        CountyDao.fetchFullCountyList(jdbi).stream()
            .map(
                county ->
                    CountyListing.builder()
                        .name(county)
                        .selected(county.equals(data.getCounty()) ? "selected" : "")
                        .build())
            .sorted(Comparator.comparing(CountyListing::getName))
            .toList();
    pageParams.put("countyList", countyListing);
    return new ModelAndView("manage/contact", pageParams);
  }

  @Builder
  @Data
  public static class CountyListing {
    String name;

    /** Should either be blank, or "selected" */
    String selected;
  }

  /**
   * Info update for a site, eg: site-rename, site contact info changed.
   */
  @PostMapping("/manage/update-site")
  @ResponseBody
  ResponseEntity<?> updateSiteData(@RequestBody Map<String, String> params) {

    String siteId = params.get("siteId");
    String field = params.get("field");
    String newValue = params.get("newValue");

    if (fetchSiteName(siteId) == null) {
      log.warn("invalid site id: {}, request: {}", siteId, params);
      return ResponseEntity.badRequest().body("Invalid site id");
    }

    var siteField = ManageSiteDao.SiteField.lookupField(field);

    if (siteField == ManageSiteDao.SiteField.SITE_NAME) {
      String oldName = SiteDetailDao.lookupSiteById(jdbi, Long.parseLong(siteId)).getSiteName();
      ManageSiteDao.updateSiteField(jdbi, Long.parseLong(siteId), siteField, newValue);
      log.info("Site updating (with name change), old name: {}, new data: {}", oldName, params);
      sendSiteUpdate.sendWithNameUpdate(Long.parseLong(siteId), oldName);
    } else {
      ManageSiteDao.updateSiteField(jdbi, Long.parseLong(siteId), siteField, newValue);
      log.info("Site updated: {}", params);
      sendSiteUpdate.send(Long.parseLong(siteId));
    }

    return ResponseEntity.ok().body("Updated");
  }

  /** Displays the 'manage-status' page. */
  @GetMapping("/manage/status")
  ModelAndView showManageStatusPage(String siteId) {

    String siteName = fetchSiteName(siteId);
    if (siteName == null) {
      return selectSite();
    }

    Map<String, String> pageParams = new HashMap<>();
    pageParams.put("siteName", siteName);
    pageParams.put("siteId", siteId);

    ManageSiteDao.SiteStatus siteStatus =
        ManageSiteDao.fetchSiteStatus(jdbi, Long.parseLong(siteId));
    pageParams.put("siteActive", siteStatus.isActive() ? "checked" : "");
    pageParams.put("siteNotActive", siteStatus.isActive() ? "" : "checked");

    pageParams.put("siteAcceptingDonations", siteStatus.isAcceptingDonations() ? "checked" : "");
    pageParams.put("siteNotAcceptingDonations", siteStatus.isAcceptingDonations() ? "" : "checked");

    pageParams.put(
        "distributionSiteChecked",
        siteStatus.getSiteTypeEnum() == ManageSiteDao.SiteType.DISTRIBUTION_SITE ? "checked" : "");
    pageParams.put(
        "supplyHubChecked",
        siteStatus.getSiteTypeEnum() == ManageSiteDao.SiteType.SUPPLY_HUB ? "checked" : "");

    return new ModelAndView("manage/status", pageParams);
  }

  /** REST endpoint to toggle the status of sites (active/accepting donations). */
  @PostMapping("/manage/update-status")
  @ResponseBody
  ResponseEntity<?> updateStatus(@RequestBody Map<String, String> params) {
    String siteId = params.get("siteId");
    String statusFlag = params.get("statusFlag");
    String newValue = params.get("newValue");

    String siteName = fetchSiteName(siteId);
    if (siteName == null) {
      return ResponseEntity.badRequest().body("Invalid site id: " + siteId);
    }
    if (statusFlag == null
        || !(statusFlag.equals("active")
            || statusFlag.equals("acceptingDonations")
            || statusFlag.equals("distSite"))) {
      return ResponseEntity.badRequest().body("Invalid status flag: " + statusFlag);
    }

    if (newValue == null
        || !(newValue.equalsIgnoreCase("true") || newValue.equalsIgnoreCase("false"))) {
      return ResponseEntity.badRequest().body("Invalid new value: " + newValue);
    }

    if (statusFlag.equalsIgnoreCase("active")) {
      log.info("Updating site: {}, active = {}", siteName, newValue);
      ManageSiteDao.updateSiteActiveFlag(
          jdbi, Long.parseLong(siteId), Boolean.parseBoolean(newValue));
    } else if (statusFlag.equalsIgnoreCase("acceptingDonations")) {
      log.info("Updating site: {}, accepting donations = {}", siteName, newValue);
      ManageSiteDao.updateSiteAcceptingDonationsFlag(
          jdbi, Long.parseLong(siteId), Boolean.parseBoolean(newValue));
    } else {
      var siteType =
          Boolean.parseBoolean(newValue)
              ? ManageSiteDao.SiteType.DISTRIBUTION_SITE
              : ManageSiteDao.SiteType.SUPPLY_HUB;
      log.info("Updating site: {}, site type: {}", siteName, siteType);
      ManageSiteDao.updateSiteType(jdbi, Long.parseLong(siteId), siteType);
    }
    sendSiteUpdate.send(Long.parseLong(siteId));
    return ResponseEntity.ok().body("Updated");
  }

  /** Display inventory listing for a site. */
  @GetMapping("/manage/inventory")
  ModelAndView displaySiteInventory(String siteId) {

    String siteName = fetchSiteName(siteId);
    if (siteName == null) {
      return selectSite();
    }

    Map<String, Object> pageParams = new HashMap<>();
    pageParams.put("siteName", siteName);
    pageParams.put("siteId", siteId);

    List<ItemInventoryDisplay> inventoryList =
        ManageSiteDao.fetchSiteInventory(jdbi, Long.parseLong(siteId)).stream()
            .map(ItemInventoryDisplay::new)
            .sorted(
                Comparator.comparing(
                    d -> d.getItemName().toUpperCase())) // ItemInventoryDisplay::getItemName))
            .toList();

    pageParams.put("inventoryList", inventoryList);

    return new ModelAndView("manage/inventory", pageParams);
  }

  @Data
  @Builder
  @AllArgsConstructor
  static class ItemInventoryDisplay {
    String itemName;

    /** Should either be blank or "checked" */
    @Builder.Default String itemChecked = "";

    @Builder.Default String urgentChecked = "";
    @Builder.Default String neededChecked = "";
    @Builder.Default String availableChecked = "";
    @Builder.Default String oversupplyChecked = "";

    ItemInventoryDisplay(ManageSiteDao.SiteInventory siteInventory) {
      itemName = siteInventory.getItemName();
      itemChecked = siteInventory.isActive() ? "checked" : "";

      urgentChecked =
          ItemStatus.URGENTLY_NEEDED.getText().equalsIgnoreCase(siteInventory.getItemStatus())
              ? "checked"
              : "";
      neededChecked =
          ItemStatus.NEEDED.getText().equalsIgnoreCase(siteInventory.getItemStatus())
              ? "checked"
              : "";
      oversupplyChecked =
          ItemStatus.OVERSUPPLY.getText().equalsIgnoreCase(siteInventory.getItemStatus())
              ? "checked"
              : "";

      // if none of the statuses are checked, then check 'available' by default.
      availableChecked =
          (urgentChecked.isEmpty() && neededChecked.isEmpty() && oversupplyChecked.isEmpty())
              ? "checked"
              : "";
    }

    @SuppressWarnings("unused")
    public String getItemLabelClass() {
      if (urgentChecked != null && !urgentChecked.isEmpty()) {
        return ItemStatus.URGENTLY_NEEDED.getCssClass();
      } else if (neededChecked != null && !neededChecked.isEmpty()) {
        return ItemStatus.NEEDED.getCssClass();
      } else if (availableChecked != null && !availableChecked.isEmpty()) {
        return ItemStatus.AVAILABLE.getCssClass();
      } else if (oversupplyChecked != null && !oversupplyChecked.isEmpty()) {
        return ItemStatus.OVERSUPPLY.getCssClass();
      } else {
        return ItemStatus.AVAILABLE.getCssClass();
      }
    }

    @SuppressWarnings("unused")
    public String getItemStatusDisabled() {
      if (itemChecked == null || itemChecked.isEmpty()) {
        return "disabled";
      } else {
        return "";
      }
    }
  }

  /** Adds an item to a site */
  @PostMapping("/manage/activate-site-item")
  @ResponseBody
  ResponseEntity<String> updateSiteItemActive(@RequestBody Map<String, String> params) {
    String siteId = params.get("siteId");
    String itemName = params.get("itemName");
    String itemStatus = params.get("itemStatus");
    log.info("Activating item: {}, siteId: {}, status: {}", itemName, siteId, itemStatus);
    if (siteId == null) {
      log.warn("Failed to activate item. No site id. Params: {}", params);
      return ResponseEntity.badRequest().body("Invalid site id, none specified.");
    }
    if (itemName == null) {
      log.warn("Failed to activate item. No item name. Params: {}", params);
      return ResponseEntity.badRequest().body("Invalid item name, none specified.");
    }
    if (itemStatus == null) {
      log.warn("Failed to activate item. No item name. No item status: {}", params);
      return ResponseEntity.badRequest().body("Invalid item status, none specified.");
    }

    String siteName = fetchSiteName(siteId);
    if (siteName == null) {
      log.warn("Failed to activate item. Invalid site id: {}, params: {}", siteId, params);
      return ResponseEntity.badRequest().body("Invalid site id");
    }

    if (!ItemStatus.allItemStatus().contains(itemStatus)) {
      log.warn("Failed to activate item. Invalid item status: {}, params: {}", itemStatus, params);
      return ResponseEntity.badRequest().body("Invalid item status: " + itemStatus);
    }

    ManageSiteDao.updateSiteItemActive(jdbi, Long.parseLong(siteId), itemName, itemStatus);
    sendInventoryUpdate.send(Long.parseLong(siteId));
    return ResponseEntity.ok("Updated");
  }

  /** Removes an item from a site */
  @PostMapping("/manage/deactivate-site-item")
  @ResponseBody
  ResponseEntity<String> updateSiteItemInactive(@RequestBody Map<String, String> params) {
    String siteId = params.get("siteId");
    String itemName = params.get("itemName");
    if (siteId == null) {
      log.warn("Failed to deactivate item, no site id. Params: {}", params);
      throw new IllegalArgumentException("Invalid site id, none specified.");
    }
    if (itemName == null) {
      log.warn("Failed to deactivate item, no item name. Params: {}", params);
      throw new IllegalArgumentException("Invalid item name, none specified.");
    }

    log.info("Deactivating item: {}, siteId: {}", itemName, siteId);
    String siteName = fetchSiteName(siteId);
    if (siteName == null) {
      log.warn("Invalid site id: {}", siteId);
      return ResponseEntity.badRequest().body("Invalid site id");
    }

    ManageSiteDao.updateSiteItemInactive(jdbi, Long.parseLong(siteId), itemName);
    sendInventoryUpdate.send(Long.parseLong(siteId));
    return ResponseEntity.ok("Updated");
  }

  /** Changes the status of an item within a site */
  @PostMapping("/manage/update-site-item-status")
  @ResponseBody
  ResponseEntity<String> updateSiteItemStatus(@RequestBody Map<String, String> params) {
    String siteId = params.get("siteId");
    String itemName = params.get("itemName");
    String newStatus = params.get("newStatus");

    log.info(
        "Updating item status, site id: {}, item name: {}, status: {}",
        siteId,
        itemName,
        newStatus);
    if (fetchSiteName(siteId) == null) {
      log.warn("Failed to update item status. Invalid site id: {}, params: {}", siteId, params);
      return ResponseEntity.badRequest().body("Invalid site id");
    }

    ManageSiteDao.updateItemStatus(jdbi, Long.parseLong(siteId), itemName, newStatus);
    sendInventoryUpdate.send(Long.parseLong(siteId));
    return ResponseEntity.ok("Updated");
  }

  /** Creates a brand new item, and adds that item to a given site. */
  @PostMapping("/manage/add-site-item")
  @ResponseBody
  ResponseEntity<String> addNewSiteItem(@RequestBody Map<String, String> params) {
    String siteId = params.get("siteId");
    String itemName = params.get("itemName");
    String itemStatus = params.get("itemStatus");

    log.info("Adding item: {}, to site: {}, status: {}", itemName, siteId, itemStatus);
    if (fetchSiteName(siteId) == null) {
      log.warn("Failed to add item. Invalid site id: {}, params: {}", siteId, params);
      return ResponseEntity.badRequest().body("Invalid site id");
    }

    if (!ItemStatus.allItemStatus().contains(itemStatus)) {
      log.warn("Failed to add item. invalid item status: {}, params: {}", itemStatus, params);
      return ResponseEntity.badRequest().body("Invalid item status: " + itemStatus);
    }

    boolean itemAdded = ManageSiteDao.addNewItem(jdbi, itemName);
    if (!itemAdded) {
      log.warn("Failed to add item, already exists. Params: {}", params);
      return ResponseEntity.badRequest().body("Item not added, already exists");
    }
    newItemUpdate.sendNewItem(itemName);

    ManageSiteDao.updateSiteItemActive(jdbi, Long.parseLong(siteId), itemName, itemStatus);
    sendInventoryUpdate.send(Long.parseLong(siteId));
    return ResponseEntity.ok("Added");
  }

  /** Shows the form for adding a brand new site */
  @GetMapping("/manage/new-site/add-site")
  ModelAndView showNewSiteForm() {
    log.info("new site");
    Map<String, Object> model = new HashMap<>();
    model.put("countyList", CountyDao.fetchFullCountyList(jdbi));
    return new ModelAndView("manage/new-site/add-site", model);
  }

  /** REST endpoint to create a new site */
  @PostMapping("/manage/add-site")
  @ResponseBody
  ResponseEntity<?> postNewSite(@RequestBody Map<String, String> params) {
    var addSiteData =
        AddSiteData.builder()
            .contactNumber(params.get("contactNumber"))
            .website(params.get("website"))
            .siteType(SiteType.parseSiteType(params.get("siteType")))
            .siteName(params.get("siteName"))
            .streetAddress(params.get("streetAddress"))
            .city(params.get("city"))
            .county(params.get("county"))
            .state(params.get("state"))
            .build();
    try {
      long newSiteId = AddSiteDao.addSite(jdbi, addSiteData);
      sendSiteUpdate.send(newSiteId);
      return ResponseEntity.ok(
          "{\"result\": \"success\", \"editSiteInventoryUrl\": \"/manage/inventory?siteId="
              + newSiteId
              + "\"}");
    } catch (AddSiteDao.DuplicateSiteException e) {
      return ResponseEntity.badRequest()
          .body("{\"result\": \"fail\", \"error\": \"site name already exists\"}");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(String.format("{\"result\": \"fail\", \"error\": \"%s\"}", e.getMessage()));
    }
  }
}
