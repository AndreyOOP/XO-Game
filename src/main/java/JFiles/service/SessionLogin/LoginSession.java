package JFiles.service.SessionLogin;

import JFiles.model.UserEntity;
import JFiles.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**Pool of authorized users*/
@Service
public class LoginSession {

    @Autowired
    @Qualifier(value = "UserServiceBean")
    private UserService userService;

    private HashMap<Integer, Session> loggedUsers = new HashMap<Integer, Session>();

    /**Create login session for user<br>
     * Add user to <i>logged user</i> list*/
    public void addUser(int authKey, String userName){

        UserEntity user = userService.getUserByName(userName);

        Session session = new Session(user);

        loggedUsers.put(authKey, session);

    }

    public Boolean isUserAlreadyLoggedIn(String userName){

        for(Session s: loggedUsers.values()){
            if( s.getUserName().contentEquals( userName)){
                return true;
            }
        }

        return false;
    }

    public Session getSession(int authKey){

        return loggedUsers.get(authKey);
    }

    public void removeSession(int authKey){

        loggedUsers.remove(authKey);
    }

    /**Authorization key is random integer value<br>
     * It is generated during login or registration process, it is needed for access to any program menu<br>*/
    public int generateAuthorizationKey(){

        int authKey;

        do{
            authKey = (int)(Integer.MAX_VALUE * Math.random());

        }while ( loggedUsers.containsKey(authKey));

        return authKey;
    }

}
