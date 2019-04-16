package db

databaseChangeLog() {
    changeSet(id: 'prod-0001', author: 'Eugene Krivosheyev') {
        comment 'Core DDL'
        tagDatabase '0.1'

        sql """
            create table CLIENT (
              ID bigint not null generated always as identity primary key,
              LOGIN varchar(50) not null,
              SECRET varchar(100) not null,
              SALT varchar(12) not null,
              CREATED timestamp not null default CURRENT_TIMESTAMP,
              ENABLED boolean not null default TRUE
            );
            create unique index CLIENT_LOGIN_INDEX on CLIENT(LOGIN);
            
            create table ACCOUNT (
              ID bigint not null generated always as identity primary key,
              AMOUNT decimal(31, 16) not null,
              CREATE_STAMP timestamp not null default CURRENT_TIMESTAMP,
              CLIENT_ID bigint not null constraint ACCOUNT_CLIENT_FK references CLIENT
                on update restrict on delete restrict
            );
        """

        rollback """
            drop table ACCOUNT;
            drop index CLIENT_LOGIN_INDEX;
            drop table CLIENT;
        """
    }

    changeSet(id: 'prod-0002', author: 'Eugene Krivosheyev') {
        comment 'Core DML'
        tagDatabase '0.2'

        sql """
            insert into CLIENT(LOGIN, SECRET, SALT) values 
                ('root@acme.com', '04979571c0019e117b922f6aae339fb614afe7f2a5685477c33c86a5a05af3e2', '1q2w3e4r5t6y');
        """

        rollback """
            delete from CLIENT where LOGIN = 'root@acme.com';
        """
    }
}