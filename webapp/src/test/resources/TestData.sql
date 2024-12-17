delete from delivery_item;
delete from delivery;
delete from site_item_audit;
delete from site_item;
delete from item;
delete from site_audit_trail;
delete from site;
delete from county;

insert into county(name, state) values('Ashe', 'NC');
insert into county(name, state) values('Buncombe', 'NC');

insert into county(name, state) values('Watauga', 'NC');
insert into county(name, state) values('Sevier', 'TN');
insert into county(name, state) values('Halifax', 'VA');
insert into county(name) values('dummy') on conflict do nothing;

insert into max_supply_load(id, sort_order, name, default_selection)
values( -100, 25, 'test-value', false) on conflict do nothing;

insert into site(
  name,
  contact_name,
  contact_number,
  contact_email,
  address,
  city,
  county_id,
  website,
  site_type_id,
  accepting_donations,
  active,
  airtable_id,
  hours,
  facebook,
  wss_id,
  publicly_visible,
  distributing_supplies,
  has_forklift,
  has_indoor_storage,
  has_loading_dock,
  max_supply_load_id,
  inactive_reason)
values (
        'site1',
        'contact me',
        '111',
        'email glorious',
        'address1',
        'city1',
        (select id from county where name = 'Watauga' and state = 'NC'),
        'site1website',
        (select id from site_type where name = 'Distribution Center'),
        true,
        true ,
        -200,
        'our hours',
        'fb url',
        -10,
        true,
        true,
        true,
        true,
        true,
        (select id from max_supply_load where name = 'Car'),
        'inactive reason text'
       );


-- site2, in Buncombe county, not accepting donations
insert into site(name, address, city, county_id, accepting_donations, site_type_id, wss_id, max_supply_load_id) values(
'site2', 'address2', 'city2', (select id from county where name = 'Buncombe'), false,
(select id from site_type where name = 'Distribution Center'), -20, -100
);


-- site3, in Buncombe county, not active
insert into site(name, address, city, county_id, active, site_type_id, max_supply_load_id) values(
'site3', 'address3', 'city2', (select id from county where name = 'Buncombe'), false,
(select id from site_type where name = 'Distribution Center'), -100
);

-- create a delivery from site2 to site3
insert into delivery(
  airtable_id, from_site_id, to_site_id,
  delivery_status, target_delivery_date, dispatcher_name,
  dispatcher_number, driver_name, driver_number,
  driver_license_plates)
values(
        -1,
        (select id from site where name = 'site2'),
        (select id from site where name = 'site3'),
        'pending',
        to_date('2024-12-13', 'YYYY-MM-DD'),
        'dispatcher1',
        '555',
        'driver1',
        'call me anytime',
        'traveler'
      );



-- site4, in Buncombe county, no items (but active), supply hub
insert into site(name, address, city, county_id, site_type_id, max_supply_load_id) values(
   'site4', 'address3', 'city2', (select id from county where name = 'Buncombe'),
   (select id from site_type where name = 'Supply Hub'), -100
);
-- create a delivery from site3 to site4
insert into delivery(
  airtable_id, from_site_id, to_site_id,
  delivery_status, target_delivery_date, dispatcher_name,
  dispatcher_number, driver_name, driver_number,
  driver_license_plates)
values(
        -2,
        (select id from site where name = 'site3'),
        (select id from site where name = 'site4'),
        'pending',
        to_date('2024-12-13', 'YYYY-MM-DD'),
        'dispatcher1',
        '555',
        'driver1',
        'call me anytime',
        'traveler'
      );


-- create a delivery from site4 to site3
-- this is a  minimal 'data', everything that can be null should be null
insert into delivery(
  airtable_id, from_site_id, to_site_id)
values(
        -3,
        (select id from site where name = 'site3'),
        (select id from site where name = 'site4')
      );

-- site5, (no items & not active), name, address & details may be modified by tests,
-- data will not be stable.
insert into site(name, address, city, county_id, site_type_id, max_supply_load_id) values(
   'site5', 'address5', 'city5', (select id from county where name = 'Buncombe'),
   (select id from site_type where name = 'Distribution Center'), -100
);

insert into site(name, address, city, county_id, website, site_type_id, max_supply_load_id) values(
   'site6', 'address6', 'city6', (select id from county where name = 'Watauga'), 'site6website',
   (select id from site_type where name = 'Distribution Center'), -100
);


insert into item(name, wss_id) values('water', -40);
insert into item(name) values('soap');
insert into item(name, wss_id) values('gloves', -50);
insert into item(name) values('used clothes');
insert into item(name) values('new clothes');
insert into item(name) values('random stuff');
insert into item(name) values('heater');
insert into item(name) values('batteries');

insert into site_item(site_id, item_id, item_status_id) values(
    (select id from site where name = 'site1'),
    (select id from item where name = 'water'),
    (select id from item_status where name = 'Available')
   );
insert into site_item(site_id, item_id, item_status_id, wss_id) values(
    (select id from site where name = 'site1'),
    (select id from item where name = 'new clothes'),
    (select id from item_status where name = 'Urgently Needed'),
    -10
   );
insert into site_item(site_id, item_id, item_status_id) values(
    (select id from site where name = 'site1'),
    (select id from item where name = 'used clothes'),
    (select id from item_status where name = 'Oversupply')
   );
 insert into site_item(site_id, item_id, item_status_id) values(
    (select id from site where name = 'site2'),
    (select id from item where name = 'used clothes'),
    (select id from item_status where name = 'Oversupply')
   );
insert into site_item(site_id, item_id, item_status_id) values(
    (select id from site where name = 'site2'),
    (select id from item where name = 'new clothes'),
    (select id from item_status where name = 'Oversupply')
  );
 insert into site_item(site_id, item_id, item_status_id) values(
    (select id from site where name = 'site2'),
    (select id from item where name = 'water'),
    (select id from item_status where name = 'Oversupply')
   );

-- insert item for inactive 'site3'
 insert into site_item(site_id, item_id, item_status_id) values(
    (select id from site where name = 'site3'),
    (select id from item where name = 'water'),
    (select id from item_status where name = 'Needed')
   );
-- insert a "dummy" county, where no site is in that county (this county is considered
-- 'not active')


