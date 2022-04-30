drop table Tags CASCADE CONSTRAINTS;
drop table Movie CASCADE CONSTRAINTS;
drop table TVShow CASCADE CONSTRAINTS;
drop table Video CASCADE CONSTRAINTS;
drop table CastMember CASCADE CONSTRAINTS;
drop table Acts CASCADE CONSTRAINTS;
drop table Account CASCADE CONSTRAINTS;
drop table Watches CASCADE CONSTRAINTS;
drop table Free CASCADE CONSTRAINTS;
drop table Premium CASCADE CONSTRAINTS;
drop table Friends CASCADE CONSTRAINTS;
drop table PlayList CASCADE CONSTRAINTS;



create table Video(
videoId char(8) primary key,
title varchar2(100) not null,
classfication varchar2(10) not null,
description varchar2(1000) not null,
constraint CHK_classification check (classfication in ('PG-13', 'TV-Y7-FV', 'TV-PG', 'TV-Y7', 'TV-14'))
);

create table Tags (
videoId char(8) references Video(videoId) on delete cascade,
tag varchar2(20),
primary key (videoId, tag)
);

create table Movie (
videoId char(8) primary key references Video(videoId) on delete cascade,
runtime number(5,2) not null,
dateOfRelease date not null,
sequel varchar2(100)
);

create table TVShow (
videoId char(8) primary key references Video(videoId) on delete cascade,
episodeNo int not null,
seasonNo int not null,
runtime number(6,2) not null,
dateOfRelease date not null
);

create table CastMember (
castId char(8) primary key,
castName varchar2(20) not null
);

create table Acts (
role  char(8) not null,
castId  char(8) references CastMember (castId) on delete cascade,
videoId  char(8) references video(videoId) on delete cascade,
primary key (role, castId, videoId)
);

create table Account (
username varchar2(20) primary key,
name varchar2(40) not null,
email varchar2(50) not null unique,
birthdate date  not null,
registrationDate date not null
);

create table Watches (
username varchar2(20) references Account(username) on delete cascade,
videoId char(8) references Video(videoId) on delete cascade,
timestamps number(6,2) not null,
primary key (username,videoId)
);

create table Free (
username varchar2(20) primary key references Account(username)
);
create table Premium (
username varchar2(20) primary key references Account(username)
);

create table Friends (
username1 varchar2(20) references Premium(username),
username2  varchar2(20) references Premium(username),
primary key (username1, username2)
);

create table PlayList (
username varchar2(20) references Premium(username) on delete cascade,
playListName varchar2(40) not null,
videoId char(8) references Video(videoId) on delete cascade,
primary key (username, playListName, videoId));
