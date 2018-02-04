package br.com.siecola.gae_exemplo1.repository;

import br.com.siecola.gae_exemplo1.model.User;
import com.google.appengine.api.datastore.*;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import java.util.logging.Logger;

@Repository
public class UserRepository {
    private static final Logger log = Logger.getLogger("UserRepository");

    private static final String USER_KIND = "Users";
    private static final String USER_KEY = "userKey";

    private static final String PROPERTY_ID = "UserId";
    private static final String PROPERTY_EMAIL = "email";
    private static final String PROPERTY_PASSWORD = "password";
    private static final String PROPERTY_GCM_REG_ID = "gcmRegId";
    private static final String PROPERTY_LAST_LOGIN = "lastLogin";
    private static final String PROPERTY_LAST_GCM_REGISTER = "lastGCMRegister";
    private static final String PROPERTY_ROLE = "role";
    private static final String PROPERTY_ENABLED = "enabled";


    private void userToEntity (User user, Entity userEntity) {
        userEntity.setProperty(PROPERTY_ID, user.getId());
        userEntity.setProperty(PROPERTY_EMAIL, user.getEmail());
        userEntity.setProperty(PROPERTY_PASSWORD, user.getPassword());
        userEntity.setProperty(PROPERTY_GCM_REG_ID, user.getGcmRegId());
        userEntity.setProperty(PROPERTY_LAST_LOGIN, user.getLastLogin());
        userEntity.setProperty(PROPERTY_LAST_GCM_REGISTER,
                user.getLastGCMRegister());
        userEntity.setProperty(PROPERTY_ROLE, user.getRole());
        userEntity.setProperty(PROPERTY_ENABLED, user.isEnabled());
    }

    private User entityToUser (Entity userEntity) {
        User user = new User();
        user.setId(userEntity.getKey().getId());
        user.setEmail((String) userEntity.getProperty(PROPERTY_EMAIL));
        user.setPassword((String) userEntity.getProperty(PROPERTY_PASSWORD));
        user.setGcmRegId((String) userEntity.getProperty(PROPERTY_GCM_REG_ID));
        user.setLastLogin((Date) userEntity.getProperty(PROPERTY_LAST_LOGIN));
        user.setLastGCMRegister((Date) userEntity.
                getProperty(PROPERTY_LAST_GCM_REGISTER));
        user.setRole((String) userEntity.getProperty(PROPERTY_ROLE));
        user.setEnabled((Boolean) userEntity.getProperty(PROPERTY_ENABLED));
        return user;
    }

    private boolean checkIfEmailExist (User user) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter filter = new Query.FilterPredicate(PROPERTY_EMAIL,
                Query.FilterOperator.EQUAL, user.getEmail());

        Query query = new Query(USER_KIND).setFilter(filter);
        Entity userEntity = datastore.prepare(query).asSingleEntity();

        if (userEntity == null) {
            return false;
        } else {
            if (user.getId() == null) {
                return true;
            } else {
                return userEntity.getKey().getId() != user.getId();
            }
        }
    }
}
