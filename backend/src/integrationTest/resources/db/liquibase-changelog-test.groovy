package db

databaseChangeLog() {
    include(file: 'classpath:/db/liquibase-changelog.groovy') //production db schema

    changeSet(id: 'test-0001', author: 'Eugene Krivosheyev') {
        comment 'Authentication test data'
        tagDatabase '0.2'

        sql """
            insert into CLIENT(LOGIN, SECRET, SALT)
                values('admin', 'c99ef573720e30031034d24e82721350dfa6af9957d267c2acc0be98813bb3e4', 'salt');
                
            insert into CLIENT(LOGIN, SECRET, SALT) 
                values('account', '5aba80f0c9f7cfb0c7e8d5767aad85e8b384872e070c13a8fe6d11f58327934b', 'salt');
            
            insert into CLIENT(LOGIN, SECRET, SALT, ENABLED) 
                values('disabled', '7a2d0aa3d45a06277ee9e48623ae0dc1d9d5f83948a0b3e5cba3cae4fda533f7', 'salt', false);
        """

        rollback """
            delete from CLIENT where LOGIN = 'disabled';
            delete from CLIENT where LOGIN = 'account';
            delete from CLIENT where LOGIN = 'admin';
        """
    }
}