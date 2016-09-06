package JFiles.service;

import JFiles.model.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserEntity getUserByName(String name);
    void addUser(UserEntity user);
    void addUser(String userName, String userPassword, int userRole, String userEmail, MultipartFile avatarFile);
    void remove(String name);
    void update(UserEntity user);
    List<UserEntity> getAllUsers();
    Boolean isEmailInDatabase(String email);
    void updateUserInDatabase(String userName, String userPassword, String userEmail, MultipartFile avatarFile);
    void updateUserInDatabase(String userName, String userPassword, String userEmail, MultipartFile avatarFile, int userRole);
    void updateUserInDatabase(String userName, String userPassword);
}
