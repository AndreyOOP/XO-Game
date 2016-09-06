import JFiles.Constants.Role;
import JFiles.model.UserEntity;
import JFiles.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:dispatcher-servletForTesting.xml")
public class UserServiceImplTest {

    @Autowired
    @Qualifier(value = "UserServiceBean")
    private UserService userService;

    @Test
    public void getUserEntityByName(){

        UserEntity user = userService.getUserByName("a");

        Assert.assertNotNull(user);
        Assert.assertEquals("a"   , user.getName());
        Assert.assertEquals("1"   , user.getPassword());
        Assert.assertEquals("a@a" , user.getEmail());
    }

    @Test
    public void addUser(){

        UserEntity newUser = new UserEntity();

        if( userService.getUserByName("test") != null){
            userService.remove("test");
        }

        newUser.setName("test");
        newUser.setPassword("pass1");
        newUser.setAvatarPic("x".getBytes());
        newUser.setRole(Role.ADMIN);

        userService.addUser( newUser);

        Assert.assertEquals("test"     , newUser.getName());
        Assert.assertEquals("pass1"    , newUser.getPassword());
        Assert.assertEquals("x"        , new String( newUser.getAvatarPic()));
        Assert.assertEquals(Role.ADMIN , newUser.getRole());
    }

    @Test
    public void remove(){

        if( userService.getUserByName("testRemove") == null){

            UserEntity removeUser = new UserEntity();

            removeUser.setName("testRemove");
            removeUser.setPassword("x");
            removeUser.setRole(Role.USER);

            userService.addUser( removeUser);
            Assert.assertEquals("testRemove", userService.getUserByName("testRemove").getName());
        }

        userService.remove("testRemove");

        Assert.assertNull( userService.getUserByName("testRemove"));
    }

    @Test
    public void update(){

        UserEntity updatedUser = userService.getUserByName("c");
        updatedUser.setPassword("updatedPW");

        userService.update(updatedUser);

        Assert.assertEquals("updatedPW", userService.getUserByName("c").getPassword());
    }

    @Test
    public void getAllUsers(){

        List<UserEntity> userEntityList = userService.getAllUsers();

        Assert.assertTrue( userEntityList.size() > 1);

        for (UserEntity user: userEntityList){
            System.out.println(user);
        }
    }

    @Test
    public void isEmailInDatabase(){

        Assert.assertTrue ( userService.isEmailInDatabase("a@a"));
        Assert.assertFalse( userService.isEmailInDatabase("not_existing_email@ttt.ua"));
    }

}