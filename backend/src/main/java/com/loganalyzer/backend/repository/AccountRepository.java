package com.loganalyzer.backend.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static test.generated.tables.Users.USERS;
import test.generated.tables.pojos.Users;

@Repository
public class AccountRepository {

    private final DSLContext dslContext;

    public AccountRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    /**
     * Change an existing users password
     * @param username - user to match
     * @param hashedPassword - the new password
     */
    public void changePassword(String username, String hashedPassword) throws IllegalArgumentException {
        int rowsAffected = dslContext.update(USERS)
                .set(USERS.PASSWORD, hashedPassword)
                .where(USERS.USERNAME.eq(username))
                .execute();

        if (rowsAffected == 0) {
            throw new IllegalArgumentException("User not found");
        }
    }

    /**
     * fetch user info excluding password
     * @param username - username to match on
     * @return a new {@link Users}
     */
//    public Users getUserInfo(String username){
//        return dslContext.select(USERS.USERNAME, USERS.EMAIL, USERS.ROLE, USERS.CREATED_AT)
//                .from(USERS)
//                .where(USERS.USERNAME.eq(username))
//                .fetchOneInto(Users.class);
//    }
}
