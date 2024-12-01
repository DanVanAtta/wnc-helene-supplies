package com.vanatta.helene.supplies.database.incoming.webhook.need.request;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SiteDataImportController {

/*
  // Example input:
   {airtableId=3, siteName=Affordable Senior Housing Foundation, siteCategory=[POD, POC],
   publicVisibility=true, status=Accepting Donations, hours=null, streetAddress=....,
   city=Hudson, state=NC, county=Caldwell, pointOfContact=null, email=null, phone=....,
   website=null, facebook=null}
*/

  @PostMapping("/import/update/site-data")
  ResponseEntity<String> updateSiteData(@RequestBody Map<String, Object> body) {
    log.info("Received import data: {}", body);


    return ResponseEntity.ok().build();
  }


}
