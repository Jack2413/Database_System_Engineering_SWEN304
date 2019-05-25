--Question1

CREATE TABLE Banks(
BankName varchar (50) not null,
City varchar (50) not null,
NoAccounts int constraint NoAccck check (NoAccounts >= 0),
Security varchar (50),
constraint banks_pk primary key (BankName,City)
);

CREATE TABLE Robberies(
BankName varchar(50) not null,
City varchar(50) Not Null ,
Dor Date Not Null,
Amount NUMERIC constraint Amountck check (Amount > 0),
constraint robberies_bankName_fk foreign key (BankName,City) references Banks(BankName,City)
);

CREATE TABLE Plans(
BankName varchar(50) Not Null, 
City varchar(50) Not Null,
NoRobbers int constraint NoRobck check (NoRobbers > 0),
PlannedDate Date Not Null,
constraint plans_bank_fk foreign key (BankName,City) references Banks(BankName,City)
);

CREATE TABLE Robbers(
RobberID serial,
Nickname varchar(50) Not Null,
Age int constraint Ageck check (Age > 0),
NoYears int constraint Noyeack check (NoYears < Age),
constraint rbpk primary key (RobberID)
);

CREATE TABLE Skills(
SkillID serial,
Description varchar(50) not null,
constraint skpk primary key (SkillID)
);

CREATE TABLE HasSkills(
RobberID int Not Null, 
SkillID int Not Null,
Preference int,
Grade varchar(50),
constraint hasSkills_Robbers_fk foreign key (RobberID) references Robbers(RobberID),
constraint hasSkills_SkillID_fk foreign key (SkillID) references Skills(SkillID)
);

CREATE TABLE HasAccounts(
RobberID int Not Null,
BankName varchar(50) Not Null, 
City varchar(50) Not Null,
constraint HasAccounts_Robbers_fk foreign key (RobberID) references Robbers(RobberID),
constraint HasAccounts_Banks_fk foreign key (BankName,City) references Banks(BankName,City)
);

CREATE TABLE Accompiecs(
RobberID int Not Null,
BankName varchar(50) Not Null,
City varchar(50) Not NUll,
RibberyDate Date ,
Share NUMERIC Not NUll constraint shack check (Share >= 0),
constraint Accompiecs_RobberID_fk foreign key (RobberID) references Robbers(RobberID),
constraint Accompiecs_BankName_fk foreign key (BankName,City) references Banks(BankName,City)
);