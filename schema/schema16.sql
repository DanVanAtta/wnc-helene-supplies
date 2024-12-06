alter table county drop constraint county_name_key;
alter table county add constraint county_unique_name unique(name, state);

insert into county(name, state) values('Accomack', 'VA');
insert into county(name, state) values('Albemarle', 'VA');
insert into county(name, state) values('Alexandria', 'VA');
insert into county(name, state) values('Alleghany', 'VA');
insert into county(name, state) values('Amelia', 'VA');
insert into county(name, state) values('Amherst', 'VA');
insert into county(name, state) values('Appomattox', 'VA');
insert into county(name, state) values('Arlington', 'VA');
insert into county(name, state) values('Augusta', 'VA');
insert into county(name, state) values('Bath', 'VA');
insert into county(name, state) values('Bedford', 'VA');
insert into county(name, state) values('Bland', 'VA');
insert into county(name, state) values('Botetourt', 'VA');
insert into county(name, state) values('Bristol', 'VA');
insert into county(name, state) values('Brunswick', 'VA');
insert into county(name, state) values('Buchanan', 'VA');
insert into county(name, state) values('Buckingham', 'VA');
insert into county(name, state) values('Buena Vista', 'VA');
insert into county(name, state) values('Campbell', 'VA');
insert into county(name, state) values('Caroline', 'VA');
insert into county(name, state) values('Carroll', 'VA');
insert into county(name, state) values('Charles City', 'VA');
insert into county(name, state) values('Charlotte', 'VA');
insert into county(name, state) values('Charlottesville', 'VA');
insert into county(name, state) values('Chesapeake', 'VA');
insert into county(name, state) values('Chesterfield', 'VA');
insert into county(name, state) values('Clarke', 'VA');
insert into county(name, state) values('Colonial Heights', 'VA');
insert into county(name, state) values('Covington', 'VA');
insert into county(name, state) values('Craig', 'VA');
insert into county(name, state) values('Culpeper', 'VA');
insert into county(name, state) values('Cumberland', 'VA');
insert into county(name, state) values('Danville', 'VA');
insert into county(name, state) values('Dickenson', 'VA');
insert into county(name, state) values('Dinwiddie', 'VA');
insert into county(name, state) values('Emporia', 'VA');
insert into county(name, state) values('Essex', 'VA');
insert into county(name, state) values('Fairfax', 'VA');
insert into county(name, state) values('Fairfax City', 'VA');
insert into county(name, state) values('Falls Church', 'VA');
insert into county(name, state) values('Fauquier', 'VA');
insert into county(name, state) values('Floyd', 'VA');
insert into county(name, state) values('Fluvanna', 'VA');
insert into county(name, state) values('Franklin', 'VA');
insert into county(name, state) values('Franklin-City', 'VA');
insert into county(name, state) values('Frederick', 'VA');
insert into county(name, state) values('Fredericksburg', 'VA');
insert into county(name, state) values('Galax', 'VA');
insert into county(name, state) values('Giles', 'VA');
insert into county(name, state) values('Gloucester', 'VA');
insert into county(name, state) values('Goochland', 'VA');
insert into county(name, state) values('Grayson', 'VA');
insert into county(name, state) values('Greene', 'VA');
insert into county(name, state) values('Greensville', 'VA');
insert into county(name, state) values('Halifax', 'VA');
insert into county(name, state) values('Hampton', 'VA');
insert into county(name, state) values('Hanover', 'VA');
insert into county(name, state) values('Harrisonburg', 'VA');
insert into county(name, state) values('Henrico', 'VA');
insert into county(name, state) values('Henry', 'VA');
insert into county(name, state) values('Highland', 'VA');
insert into county(name, state) values('Hopewell', 'VA');
insert into county(name, state) values('Isle Of Wight', 'VA');
insert into county(name, state) values('James City', 'VA');
insert into county(name, state) values('King And Queen', 'VA');
insert into county(name, state) values('King George', 'VA');
insert into county(name, state) values('King William', 'VA');
insert into county(name, state) values('Lancaster', 'VA');
insert into county(name, state) values('Lee', 'VA');
insert into county(name, state) values('Lexington', 'VA');
insert into county(name, state) values('Loudoun', 'VA');
insert into county(name, state) values('Louisa', 'VA');
insert into county(name, state) values('Lunenburg', 'VA');
insert into county(name, state) values('Lynchburg', 'VA');
insert into county(name, state) values('Madison', 'VA');
insert into county(name, state) values('Manassas', 'VA');
insert into county(name, state) values('Manassas Park', 'VA');
insert into county(name, state) values('Martinsville', 'VA');
insert into county(name, state) values('Mathews', 'VA');
insert into county(name, state) values('Mecklenburg', 'VA');
insert into county(name, state) values('Middlesex', 'VA');
insert into county(name, state) values('Montgomery', 'VA');
insert into county(name, state) values('Nelson', 'VA');
insert into county(name, state) values('New Kent', 'VA');
insert into county(name, state) values('Newport News', 'VA');
insert into county(name, state) values('Norfolk', 'VA');
insert into county(name, state) values('Northampton', 'VA');
insert into county(name, state) values('Northumberland', 'VA');
insert into county(name, state) values('Norton', 'VA');
insert into county(name, state) values('Nottoway', 'VA');
insert into county(name, state) values('Orange', 'VA');
insert into county(name, state) values('Page', 'VA');
insert into county(name, state) values('Patrick', 'VA');
insert into county(name, state) values('Petersburg', 'VA');
insert into county(name, state) values('Pittsylvania', 'VA');
insert into county(name, state) values('Poquoson', 'VA');
insert into county(name, state) values('Portsmouth', 'VA');
insert into county(name, state) values('Powhatan', 'VA');
insert into county(name, state) values('Prince Edward', 'VA');
insert into county(name, state) values('Prince George', 'VA');
insert into county(name, state) values('Prince William', 'VA');
insert into county(name, state) values('Pulaski', 'VA');
insert into county(name, state) values('Radford', 'VA');
insert into county(name, state) values('Rappahannock', 'VA');
insert into county(name, state) values('Richmond', 'VA');
insert into county(name, state) values('Richmond-City', 'VA');
insert into county(name, state) values('Roanoke', 'VA');
insert into county(name, state) values('Roanoke-City', 'VA');
insert into county(name, state) values('Rockbridge', 'VA');
insert into county(name, state) values('Rockingham', 'VA');
insert into county(name, state) values('Russell', 'VA');
insert into county(name, state) values('Salem', 'VA');
insert into county(name, state) values('Scott', 'VA');
insert into county(name, state) values('Shenandoah', 'VA');
insert into county(name, state) values('Smyth', 'VA');
insert into county(name, state) values('Southampton', 'VA');
insert into county(name, state) values('Spotsylvania', 'VA');
insert into county(name, state) values('Stafford', 'VA');
insert into county(name, state) values('Staunton', 'VA');
insert into county(name, state) values('Suffolk', 'VA');
insert into county(name, state) values('Surry', 'VA');
insert into county(name, state) values('Sussex', 'VA');
insert into county(name, state) values('Tazewell', 'VA');
insert into county(name, state) values('Virginia Beach', 'VA');
insert into county(name, state) values('Warren', 'VA');
insert into county(name, state) values('Washington', 'VA');
insert into county(name, state) values('Waynesboro', 'VA');
insert into county(name, state) values('Westmoreland', 'VA');
insert into county(name, state) values('Williamsburg', 'VA');
insert into county(name, state) values('Winchester', 'VA');
insert into county(name, state) values('Wise', 'VA');
insert into county(name, state) values('Wythe', 'VA');
insert into county(name, state) values('York', 'VA');
insert into county(name, state) values('Anderson', 'TN');
insert into county(name, state) values('Bedford', 'TN');
insert into county(name, state) values('Benton', 'TN');
insert into county(name, state) values('Bledsoe', 'TN');
insert into county(name, state) values('Blount', 'TN');
insert into county(name, state) values('Bradley', 'TN');
insert into county(name, state) values('Campbell', 'TN');
insert into county(name, state) values('Cannon', 'TN');
insert into county(name, state) values('Carroll', 'TN');
insert into county(name, state) values('Carter', 'TN');
insert into county(name, state) values('Cheatham', 'TN');
insert into county(name, state) values('Chester', 'TN');
insert into county(name, state) values('Claiborne', 'TN');
insert into county(name, state) values('Clay', 'TN');
insert into county(name, state) values('Cocke', 'TN');
insert into county(name, state) values('Coffee', 'TN');
insert into county(name, state) values('Crockett', 'TN');
insert into county(name, state) values('Cumberland', 'TN');
insert into county(name, state) values('Davidson', 'TN');
insert into county(name, state) values('Decatur', 'TN');
insert into county(name, state) values('DeKalb', 'TN');
insert into county(name, state) values('Dickson', 'TN');
insert into county(name, state) values('Dyer', 'TN');
insert into county(name, state) values('Fayette', 'TN');
insert into county(name, state) values('Fentress', 'TN');
insert into county(name, state) values('Franklin', 'TN');
insert into county(name, state) values('Gibson', 'TN');
insert into county(name, state) values('Giles', 'TN');
insert into county(name, state) values('Grainger', 'TN');
insert into county(name, state) values('Greene', 'TN');
insert into county(name, state) values('Grundy', 'TN');
insert into county(name, state) values('Hamblen', 'TN');
insert into county(name, state) values('Hamilton', 'TN');
insert into county(name, state) values('Hancock', 'TN');
insert into county(name, state) values('Hardeman', 'TN');
insert into county(name, state) values('Hardin', 'TN');
insert into county(name, state) values('Hawkins', 'TN');
insert into county(name, state) values('Haywood', 'TN');
insert into county(name, state) values('Henderson', 'TN');
insert into county(name, state) values('Henry', 'TN');
insert into county(name, state) values('Hickman', 'TN');
insert into county(name, state) values('Houston', 'TN');
insert into county(name, state) values('Humphreys', 'TN');
insert into county(name, state) values('Jackson', 'TN');
insert into county(name, state) values('Jefferson', 'TN');
insert into county(name, state) values('Johnson', 'TN');
insert into county(name, state) values('Knox', 'TN');
insert into county(name, state) values('Lake', 'TN');
insert into county(name, state) values('Lauderdale', 'TN');
insert into county(name, state) values('Lawrence', 'TN');
insert into county(name, state) values('Lewis', 'TN');
insert into county(name, state) values('Lincoln', 'TN');
insert into county(name, state) values('Loudon', 'TN');
insert into county(name, state) values('McMinn', 'TN');
insert into county(name, state) values('McNairy', 'TN');
insert into county(name, state) values('Macon', 'TN');
insert into county(name, state) values('Madison', 'TN');
insert into county(name, state) values('Marion', 'TN');
insert into county(name, state) values('Marshall', 'TN');
insert into county(name, state) values('Maury', 'TN');
insert into county(name, state) values('Meigs', 'TN');
insert into county(name, state) values('Monroe', 'TN');
insert into county(name, state) values('Montgomery', 'TN');
insert into county(name, state) values('Moore', 'TN');
insert into county(name, state) values('Morgan', 'TN');
insert into county(name, state) values('Obion', 'TN');
insert into county(name, state) values('Overton', 'TN');
insert into county(name, state) values('Perry', 'TN');
insert into county(name, state) values('Pickett', 'TN');
insert into county(name, state) values('Polk', 'TN');
insert into county(name, state) values('Putnam', 'TN');
insert into county(name, state) values('Rhea', 'TN');
insert into county(name, state) values('Roane', 'TN');
insert into county(name, state) values('Robertson', 'TN');
insert into county(name, state) values('Rutherford', 'TN');
insert into county(name, state) values('Scott', 'TN');
insert into county(name, state) values('Sequatchie', 'TN');
insert into county(name, state) values('Sevier', 'TN');
insert into county(name, state) values('Shelby', 'TN');
insert into county(name, state) values('Smith', 'TN');
insert into county(name, state) values('Stewart', 'TN');
insert into county(name, state) values('Sullivan', 'TN');
insert into county(name, state) values('Sumner', 'TN');
insert into county(name, state) values('Tipton', 'TN');
insert into county(name, state) values('Trousdale', 'TN');
insert into county(name, state) values('Unicoi', 'TN');
insert into county(name, state) values('Union', 'TN');
insert into county(name, state) values('Van Buren', 'TN');
insert into county(name, state) values('Warren', 'TN');
insert into county(name, state) values('Washington', 'TN');
insert into county(name, state) values('Wayne', 'TN');
insert into county(name, state) values('Weakley', 'TN');
insert into county(name, state) values('White', 'TN');
insert into county(name, state) values('Williamson', 'TN');
insert into county(name, state) values('Wilson', 'TN');
