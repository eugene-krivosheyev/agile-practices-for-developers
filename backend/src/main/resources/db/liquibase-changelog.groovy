package db

databaseChangeLog() {
    changeSet(id: 'prod-0001', author: 'Eugene Krivosheyev') {
        comment 'Core DDL'
        tagDatabase '0.1'

        sql """
            create table CLIENT_DATA_TYPE (
              ID int not null generated always as identity primary key,
              DESCRIPTION varchar(120) not null unique
            );
            
            create table CLIENT_STATUS (
              ID integer not null generated always as identity primary key,
              DESCRIPTION varchar(255) not null unique
            );
            
            create table CLIENT (
              ID bigint not null generated always as identity primary key,
              LOGIN varchar(50) not null,
              SECRET varchar(100) not null,
              SALT varchar(12) not null,
              CREATED timestamp not null default CURRENT_TIMESTAMP,
              USERNAME varchar(50),
              LOCALE varchar(50),
              ENABLED boolean not null default TRUE
            );
            create unique index CLIENT_LOGIN_INDEX on CLIENT(LOGIN);
            create unique index CLIENT_LOGIN_USERNAME_INDEX on CLIENT(LOGIN, USERNAME);
            
            create table AUTHORITY (
              ID int not null generated always as identity primary key,
              AUTHORITY varchar(50) not null,
              CLIENT_ID bigint not null constraint AUTHORITY_CLIENT_ID_FK references CLIENT
                on delete cascade on update restrict
            );
            
            create table CLIENT_DATA (
              ID bigint not null generated always as identity primary key,
              VALUE varchar(255) not null,
              POST_STAMP timestamp not null default CURRENT_TIMESTAMP,
              EXPIRE_STAMP timestamp,
              CLIENT_ID bigint not null constraint CLIENT_DATA_CLIENT_FK references CLIENT
                on delete restrict on update restrict,
              TYPE_ID integer not null constraint CLIENT_DATA_TYPE_FK references CLIENT_DATA_TYPE
                on delete restrict on update restrict,
            
              constraint CLIENT_DATA_UNIQUE unique (CLIENT_ID, TYPE_ID)
            );
            
            create table CLIENT_EMAIL (
              ID bigint not null generated always as identity primary key,
              EMAIL varchar(60) not null,
              TIME_CREATED timestamp not null default CURRENT_TIMESTAMP,
              CLIENT_ID bigint not null constraint EMAIL_CLIENT_ID_FK references CLIENT
            );
            
            
            create table AUTH_TOKEN (
              TOKEN varchar(36) not null,
              CREATED timestamp not null default CURRENT_TIMESTAMP,
              EXPIRED timestamp not null default CURRENT_TIMESTAMP,
              LAST_ACCESS_STAMP timestamp,
              ENABLED boolean not null default TRUE,
              CLIENT_ID bigint not null constraint AUTH_TOKEN_CLIENT_ID_FK references CLIENT
                on delete cascade on update restrict
            );
            create unique index AUTH_TOKEN_INDEX on AUTH_TOKEN(TOKEN);
            
            
            create table SETTINGS (
              PARAM varchar(255) not null primary key,
              VALUE varchar(255) not null
            );
            
            
            create table BALANCE_TRANSACTION_STATUS (
              ID integer not null generated always as identity primary key,
              DESCRIPTION varchar(255) not null unique
            );
            
            CREATE TABLE BALANCE_TRANSACTION_TYPE (
              ID integer not null generated always as identity primary key,
              DESCRIPTION varchar(255) not null unique
            );
            
            create table CURRENCY_TYPE (
              ID integer not null generated always as identity primary key,
              DESCRIPTION varchar(255) not null unique
            );
            
            create table PAY_TRANSACTION_TYPE (
              ID integer not null generated always as identity primary key,
              DESCRIPTION varchar(255) not null unique
            );
            
            create table PAY_TRANSACTION_STATUS (
              ID integer not null generated always as identity primary key,
              DESCRIPTION varchar(255) not null unique
            );
            
            
            create table PLATFORM (
              ID integer not null generated always as identity primary key,
              NAME varchar(255) not null unique,
              ALIAS varchar(16) not null unique
            );
            
            CREATE TABLE CURRENCY (
              ID integer not null generated always as identity primary key,
              NAME varchar(64) not null unique,
              ALIAS varchar(16) not null unique,
              DECIMAL_PRECISION integer not null,
              PLATFORM_ID integer not null constraint CURRENCY_PLATFORM_FK references PLATFORM
                on update restrict on delete restrict,
              TYPE_ID integer not null constraint CURRENCY_TYPE_FK references CURRENCY_TYPE
                on update restrict on delete restrict
            );
            
            
            create table ACCOUNT (
              ID bigint not null generated always as identity primary key,
              AMOUNT decimal(31, 16) not null,
              CREATE_STAMP timestamp not null default CURRENT_TIMESTAMP,
              CLIENT_ID bigint not null constraint ACCOUNT_CLIENT_FK references CLIENT
                on update restrict on delete restrict,
              CURRENCY_ID integer not null constraint ACCOUNT_CURRENCY_FK references CURRENCY
                on update restrict on delete restrict,
            
              constraint ACCOUNT_UNIQUE unique (client_id, currency_id)
            );
            
            create table ADDRESS (
              ID bigint not null generated always as identity primary key,
              CREATE_STAMP timestamp not null default CURRENT_TIMESTAMP,
              HASH varchar(128) not null,
              CLIENT_ID bigint not null constraint ADDRESS_CLIENT_FK references CLIENT
                on update restrict on delete restrict,
              PLATFORM_ID integer not null constraint ADDRESS_PLATFORM_FK references PLATFORM
                on update restrict on delete restrict,
            
              constraint ADDRESS_UNIQUE unique (PLATFORM_ID, HASH)
            );
            
            create table PAY_TRANSACTION (
              ID bigint not null generated always as identity primary key,
              "ORDER" char(36) not null unique,
              AMOUNT decimal(31, 16) not null,
              POST_STAMP timestamp not null default CURRENT_TIMESTAMP,
              UPDATE_STAMP timestamp not null default CURRENT_TIMESTAMP,
              ERROR_MSG varchar(255),
              EXTERNAL_ID varchar(128),
              ADDRESS_ID bigint not null constraint PAY_TRANSACTION_ADDRESS_FK references ADDRESS
                on update restrict on delete restrict,
              TYPE_ID integer not null constraint PAY_TRANSACTION_TYPE_FK references PAY_TRANSACTION_TYPE
                on update restrict on delete restrict,
              STATUS_ID integer not null constraint PAY_TRANSACTION_STATUS_FK references PAY_TRANSACTION_STATUS
                on update restrict on delete restrict,
              ACCOUNT_ID bigint not null constraint PAY_TRANSACTION_ACCOUNT_FK references ACCOUNT
                on update restrict on delete restrict
            );
            
            create table BALANCE_TRANSACTION (
              ID bigint not null generated always as identity primary key,
              POST_STAMP timestamp not null default CURRENT_TIMESTAMP,
              FROM_AMOUNT decimal(31, 16),
              FROM_HASH varchar(128),
              TO_AMOUNT decimal(31, 16),
              TO_HASH character varying(128),
              RATE decimal(31, 16) not null,
              UPDATE_STAMP integer not null,
              PAY_TRANSACTION_ID bigint constraint BALANCE_TRANSACTION_PAY_FK references PAY_TRANSACTION
                on update restrict on delete restrict,
              TYPE_ID integer not null constraint BALANCE_TRANSACTION_TYPE_FK references BALANCE_TRANSACTION_TYPE
                on update restrict on delete restrict,
              FROM_ACCOUNT_ID bigint not null constraint BALANCE_TRANSACTION_FROM_ACCOUNT_FK references ACCOUNT
                on update restrict on delete restrict,
              FROM_CURRENCY_ID integer not null constraint BALANCE_TRANSACTION_FROM_CURRENCY_FK references CURRENCY
                on update restrict on delete restrict,
              TO_ACCOUNT_ID bigint not null constraint BALANCE_TRANSACTION_TO_ACCOUNT_FK references ACCOUNT
                on update restrict on delete restrict,
              TO_CURRENCY_ID integer not null constraint BALANCE_TRANSACTION_TO_CURRENCY_FK references CURRENCY
                on update restrict on delete restrict,
              STATUS_ID integer not null constraint BALANCE_TRANSACTION_STATUS_FK references BALANCE_TRANSACTION_STATUS
                on update restrict on delete restrict
            );
        """

        rollback """
            drop table BALANCE_TRANSACTION;
            drop table PAY_TRANSACTION;
            drop table ADDRESS;
            drop table ACCOUNT;
            
            drop table CURRENCY;
            drop table PLATFORM;

            drop table PAY_TRANSACTION_STATUS;
            drop table PAY_TRANSACTION_TYPE;
            drop table CURRENCY_TYPE;
            drop table BALANCE_TRANSACTION_TYPE;
            drop table BALANCE_TRANSACTION_STATUS;
            
            drop table SETTINGS;
            
            drop index AUTH_TOKEN_INDEX;
            drop table AUTH_TOKEN;

            drop table CLIENT_EMAIL;
            drop table CLIENT_DATA;
            drop table AUTHORITY;
            drop index CLIENT_LOGIN_USERNAME_INDEX;
            drop index CLIENT_LOGIN_INDEX;
            drop table CLIENT;
            drop table CLIENT_STATUS;
            drop table CLIENT_DATA_TYPE;
        """
    }

    changeSet(id: 'prod-0002', author: 'Eugene Krivosheyev') {
        comment 'Core DML'
        tagDatabase '0.2'

        sql """
            insert into CLIENT_DATA_TYPE(DESCRIPTION) values 
                ('created'), ('confirmed'), ('authorized'), ('blocked'), ('change email'), ('recovery'), ('reset password');  
            insert into CLIENT(LOGIN, SECRET, SALT, USERNAME, LOCALE) values 
                ('demo@acme.one', '04979571c0019e117b922f6aae339fb614afe7f2a5685477c33c86a5a05af3e2', '1q2w3e4r5t6y', 'demo', 'en');
        """

        rollback """
            delete from CLIENT_DATA_TYPE;
        """
    }
}