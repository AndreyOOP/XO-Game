package JFiles.dao;

import JFiles.model.UserEntity;

import java.util.List;

public interface UserDAO {

    public UserEntity getUserByName(String name);
    public void addUser(UserEntity user);
    public void remove(String user);
    public void update(UserEntity user);
    public List<UserEntity> getAllUsers();
    public List<String> getRecordsWithEmail(String email);
}
