/**
 * Fires when inventory checkbox is checked or unchecked.
 */
async function toggleInventory(siteId, itemName) {
  const checked = document.getElementById(`${itemName}Checkbox`).checked;

  document.getElementById(`${itemName}Requested`).disabled = !checked;
  document.getElementById(`${itemName}Urgent`).disabled = !checked;
  document.getElementById(`${itemName}Oversupply`).disabled = !checked;

  const requestedChecked = document.getElementById(`${itemName}Requested`)
      .checked;
  const urgentChecked = document.getElementById(`${itemName}Urgent`)
      .checked;
  const oversupplyChecked = document.getElementById(`${itemName}Oversupply`)
      .checked;

  if (checked) {
    let labelClass = "requested";
    let itemStatus = "Requested";
    if (urgentChecked) {
      labelClass = "urgent";
      itemStatus = "Urgent Need";
    } else if(oversupplyChecked) {
      labelClass = "oversupply";
      itemStatus = "Oversupply";
    }
    await sendActivateItem(siteId, itemName, itemStatus);

    document.getElementById(`${itemName}Label`).classList.value = "larger " + labelClass;

    document.getElementById(`${itemName}RequestedLabel`)
    .classList.remove("disabled");
    document.getElementById(`${itemName}UrgentLabel`)
    .classList.remove("disabled");
    document.getElementById(`${itemName}OversupplyLabel`)
    .classList.remove("disabled");
  } else {
    await sendDeactivateItem(siteId, itemName);

    document.getElementById(`${itemName}Label`).classList.value = "larger disabled";

    document.getElementById(`${itemName}RequestedLabel`)
    .classList.add("disabled");
    document.getElementById(`${itemName}UrgentLabel`)
    .classList.add("disabled");
    document.getElementById(`${itemName}OversupplyLabel`)
    .classList.add("disabled");
  }
  showUpdateConfirmation(itemName);
}

async function sendActivateItem(siteId, itemName, itemStatus) {
  const url = "/manage/activate-site-item";

  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      siteId: siteId,
      itemName: itemName,
      itemStatus: itemStatus
    })
  });

  if (!response.ok) {
    throw new Error(`Response status: ${response.status}, ${response.body}`);
  }
  return await response.text();
}

async function sendDeactivateItem(siteId, itemName) {
  const url = "/manage/deactivate-site-item";

  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      siteId: siteId,
      itemName: itemName
    })
  });

  if (!response.ok) {
    throw new Error(`Response status: ${response.status}, ${response.body}`);
  }
  return await response.text();
}


/**
 * Fires when the item status radio buttons are toggled.
 */
function changeItemStatus(itemName) {
  const requestedChecked = document.getElementById(`${itemName}Requested`)
      .checked ? "checked" : "";
  const urgentChecked = document.getElementById(`${itemName}Urgent`)
      .checked ? "checked" : "";
  const oversupplyChecked = document.getElementById(`${itemName}Oversupply`)
      .checked ? "checked" : "";

  document.getElementById(`${itemName}Label`)
  .classList.remove("requested", "urgent", "oversupply");

  if (requestedChecked) {
    document.getElementById(`${itemName}Label`)
    .classList.add("requested");
  } else if (urgentChecked) {
    document.getElementById(`${itemName}Label`)
    .classList.add("urgent");
  } else {
    document.getElementById(`${itemName}Label`)
    .classList.add("oversupply");
  }
  showUpdateConfirmation(itemName);
}

function addItem() {
  const itemName = document.getElementById("newItemText").value;
  const itemNameEncoded = htmlEncode(itemName);

  if (itemName === "") {
    return;
  }

  const requestedChecked = document.getElementById("requestedRadioNew")
      .checked ? "checked" : "";
  const urgentChecked = document.getElementById("urgentlyNeededRadioNew")
      .checked ? "checked" : "";
  const oversupplyChecked = document.getElementById("oversupplyRadioNew")
      .checked ? "checked" : "";

  let labelStyle = "";
  if (requestedChecked) {
    labelStyle = "requested";
  } else if (urgentChecked) {
    labelStyle = "urgent";
  } else if(oversupplyChecked) {
    labelStyle = "oversupply";
  }

  const newItemRowHtml = `
        <tr>
          <!-- Item Checkbox -->
          <td><input type="checkbox" id="${itemNameEncoded}Checkbox"
                     onclick="toggleInventory('${itemNameEncoded}')" checked /></td>

           <!-- Item Label -->
          <td>
            <div>
              <label for="${itemNameEncoded}Checkbox" class="larger ${labelStyle}" id="${itemNameEncoded}Label">${itemNameEncoded}</label>
            </div>
            <div class="horizontal update-confirm-div" id="${itemNameEncoded}UpdateConfirm">
              <span class="green-check" id="update-confirm">&#10003;</span>
              <span>Updated</span>
            </div>
          </td>
          <!-- Item Status Radio buttons -->
          <td>
            <div class="horizontal">
            
              <fieldset class="vertical">
                <div class="horizontal">
                  <input 
                      type="radio" 
                      id="${itemNameEncoded}Requested" 
                      name="${itemNameEncoded}Status" 
                      onclick="changeItemStatus('${itemNameEncoded}')"
                      ${requestedChecked}/>
                  <label 
                      for="${itemNameEncoded}Requested" 
                      class="requested" 
                      id="${itemNameEncoded}RequestedLabel">
                    Requested or Available</label>
                </div>
                <div class="horizontal">
                  <input 
                      type="radio" 
                      id="${itemNameEncoded}Urgent" 
                      name="${itemNameEncoded}Status" 
                      onclick="changeItemStatus('${itemNameEncoded}')"
                      ${urgentChecked}/>
                  <label 
                      for="${itemNameEncoded}Urgent" 
                      class="urgent" 
                      id="${itemNameEncoded}UrgentLabel">
                    Urgently Needed</label>
                </div>
                <div class="horizontal">
                  <input 
                      type="radio" 
                      id="${itemNameEncoded}Oversupply" 
                      name="${itemNameEncoded}Status" 
                      onclick="changeItemStatus('${itemNameEncoded}')"
                      ${oversupplyChecked}/>
                  <label 
                      for="${itemNameEncoded}Oversupply" 
                      class="oversupply" 
                      id="${itemNameEncoded}OversupplyLabel">
                    Oversupply (too much)</label>
                </div>
              </fieldset>
            </div>

          </td>
        </tr>
  `;

  document.getElementById("inventoryTableBody").innerHTML +=
      newItemRowHtml;

  document.getElementById("newItemText").value = "";

  showUpdateConfirmation(itemNameEncoded);
}

const timeouts = [];


/**
 * Shows 'update confirm' checkmark. If the indicator is already visible, then we hide it briefly
 * before showing it again. If there was a previous timer that was going to hide it, we cancel
 * that so we can renew the hide timer.
 */
function showUpdateConfirmation(itemName) {

  // remove any preceding timeouts which have yet to fire.
  clearTimeout(timeouts[itemName]);

  // if update confirmation is visible, hide it very briefly and then show it again.
  if (document.getElementById(`${itemName}UpdateConfirm`).style.display === "block") {
    // hide the element for 200ms
    document.getElementById(`${itemName}UpdateConfirm`).style.display = "none";
    timeouts[itemName] = setTimeout(function () {
          document.getElementById(`${itemName}UpdateConfirm`).style.display = 'block';
        },
        100);

  } else {
    // if update confirm is not visible, then make it visible.
    document.getElementById(`${itemName}UpdateConfirm`).style.display = "block";
  }
  // document.getElementById(`${itemName}UpdateConfirm`).style.display = "block";
  timeouts[itemName] = setTimeout(function () {
        document.getElementById(`${itemName}UpdateConfirm`).style.display = 'none';
      },
      1000);
}

function htmlEncode(input) {
  input = input.replace(/&/g, '&amp;');
  input = input.replace(/</g, '&lt;');
  input = input.replace(/>/g, '&gt;');
  return input;
}
