 set define off;

insert into openiam.IDENTITY_QUEST_GRP(IDENTITY_QUEST_GRP_ID, NAME, STATUS, COMPANY_OWNER_ID, CREATE_DATE) VALUES ('GLOBAL','GLOBAL IDENTITY QUESTIONS', 'ACTIVE', 'GLOBAL', sysdate);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('200','GLOBAL','What are the last four digits of your social security number?',1);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('209','GLOBAL','What are the last four digits of your drivers license?',1);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('201','GLOBAL','What is your mothers maiden name?',1);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('202','GLOBAL','Where did you go to school?',1);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('203','GLOBAL','What is your pets name?',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('204','GLOBAL','What is your favorite food?',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('205','GLOBAL','What is your favorite color?',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('206','GLOBAL','Which city were you born in?',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('207','GLOBAL','What is your favorite sport?',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('210','GLOBAL','What is the name of your favorite school?',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('211','GLOBAL','What is the name of your first pet',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('212','GLOBAL','What is the name of your favorite movie, play or song?',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('213','GLOBAL','What is the title of your favorite book?',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('214','GLOBAL','What is the name of your first boy or girl friend?',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('215','GLOBAL','What is the name of your favorite school teacher?',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('216','GLOBAL','What is the year and nick name of your first car (Year-Name)?',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('217','GLOBAL','Where did you meet you significant other?',0);
insert into openiam.IDENTITY_QUESTION(IDENTITY_QUESTION_ID, IDENTITY_QUEST_GRP_ID, QUESTION_TEXT, REQUIRED) VALUES('218','GLOBAL','What is the type and name of your first pet (Type-Name)?',0);

commit;