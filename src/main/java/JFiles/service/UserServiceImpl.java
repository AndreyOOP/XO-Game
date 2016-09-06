package JFiles.service;

import JFiles.dao.UserDAO;
import JFiles.model.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**Service for work with <i>User</i> table. (Add, Update, Select records etc)<br>
 * Methods are self-explanatory*/
@Service
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Transactional
    public UserEntity getUserByName(String name) {
        return userDAO.getUserByName(name);
    }

    @Transactional
    public List<UserEntity> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Transactional
    public void addUser(UserEntity user) {
        userDAO.addUser(user);
    }

    @Transactional
    public void addUser(String userName, String userPassword, int userRole, String userEmail, MultipartFile avatarFile) {

        UserEntity newUser = new UserEntity();

        newUser.setName     ( userName);
        newUser.setPassword ( userPassword);
        newUser.setAvatarPic( toBytes(avatarFile));
        newUser.setRole     ( userRole);
        newUser.setEmail    ( userEmail);

        addUser( newUser);
    }

    @Transactional
    public void remove(String user) {
        userDAO.remove(user);
    }

    @Transactional
    public void update(UserEntity user) {
        userDAO.update(user);
    }

    @Transactional
    public void updateUserInDatabase(String userName, String userPassword) {

        UserEntity updateUser = getUserByName( userName);

        updateUser.setPassword ( userPassword);

        update( updateUser);
    }

    @Transactional
    public void updateUserInDatabase(String userName, String userPassword, String userEmail, MultipartFile avatarFile) {

        UserEntity updateUser = getUserByName( userName);

        updateUser.setPassword ( userPassword);

        updateUser.setEmail    ( userEmail);

        updateUser.setAvatarPic( toBytes(avatarFile));

        update( updateUser);
    }

    @Transactional
    public void updateUserInDatabase(String userName, String userPassword, String userEmail, MultipartFile avatarFile, int userRole) {

        UserEntity updateUser = getUserByName( userName);

        updateUser.setPassword ( userPassword);

        updateUser.setEmail    ( userEmail);

        updateUser.setAvatarPic( toBytes(avatarFile));

        updateUser.setRole     ( userRole);

        update( updateUser);
    }

    @Transactional
    public Boolean isEmailInDatabase(String email) {

        return userDAO.getRecordsWithEmail(email).size() > 0;
    }

    /**Converts Multipart File to bytes for database storage*/
    private byte[] toBytes(MultipartFile avatarFile){

        try {
            return avatarFile.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
