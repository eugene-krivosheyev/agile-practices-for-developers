package db

//TODO move to integrationTest/resources and solve build profiles issue
databaseChangeLog() {
    include(file: 'db/liquibase-changelog.groovy') //production db schema

    changeSet(id: 'test-0001', author: 'Eugene Krivosheyev') {
        comment 'Authentication test data'
        tagDatabase '0.2'

        sql """
            insert into CLIENT(LOGIN, SECRET, SALT, USERNAME, LOCALE) 
                values('admin', 'c99ef573720e30031034d24e82721350dfa6af9957d267c2acc0be98813bb3e4', 'salt', 'admin@ya.ru', 'ru-RU'); --adminpassword
            insert into AUTHORITY(AUTHORITY, CLIENT_ID) values('ROLE_ADMIN', 1); 
                
            insert into CLIENT(LOGIN, SECRET, SALT, USERNAME, LOCALE) 
                values('account', '5aba80f0c9f7cfb0c7e8d5767aad85e8b384872e070c13a8fe6d11f58327934b', 'salt', 'account@ya.ru', 'en-US'); --accountpassword
            insert into AUTHORITY(AUTHORITY, CLIENT_ID) values('ROLE_USER', 2);
            
            insert into CLIENT(LOGIN, SECRET, SALT, USERNAME, LOCALE, ENABLED) 
                values('disabled', '7a2d0aa3d45a06277ee9e48623ae0dc1d9d5f83948a0b3e5cba3cae4fda533f7', 'salt', 'disabled@ya.ru', 'en-US', false); --disabledpassword
            insert into AUTHORITY(AUTHORITY, CLIENT_ID) values('ROLE_USER', 3);
                
            insert into AUTH_TOKEN(TOKEN, CLIENT_ID) values
                ('t1', 1), ('t2', 1), ('t3', 2), ('t4', 2);
                
            insert into CLIENT_EMAIL(EMAIL, CLIENT_ID) values('adminemail1@ya.ru', 1);
            insert into CLIENT_EMAIL(EMAIL, CLIENT_ID) values('adminemail2@ya.ru', 1);
            insert into CLIENT_EMAIL(EMAIL, CLIENT_ID) values('usermail1@ya.ru', 2);
        """

        rollback """
            delete from CLIENT_EMAIL where CLIENT_ID = 1;
            delete from CLIENT_EMAIL where CLIENT_ID = 2;

            delete from AUTH_TOKEN where CLIENT_ID = 1;
            delete from AUTH_TOKEN where CLIENT_ID = 2;

            delete from AUTHORITY where ID = 3;
            delete from CLIENT where ID = 3;
            delete from AUTHORITY where ID = 2;
            delete from CLIENT where ID = 2;
            delete from AUTHORITY where ID = 1;
            delete from CLIENT where ID = 1;
        """
    }
}