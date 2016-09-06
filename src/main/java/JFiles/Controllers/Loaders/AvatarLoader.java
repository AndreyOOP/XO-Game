package JFiles.Controllers.Loaders;

import JFiles.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**Load avatart picture related to user stored in User database*/
@org.springframework.stereotype.Controller
public class AvatarLoader {

    @Autowired
    @Qualifier(value = "UserServiceBean")
    private UserService userService;

    @RequestMapping("/avatar/{userName}")
    public ResponseEntity<byte[]> onPhoto(@PathVariable("userName") String userName) {

        byte[] avatarPicBytes = userService.getUserByName(userName).getAvatarPic();

        if ( avatarPicBytes == null || avatarPicBytes.length < 1){
            avatarPicBytes = userService.getUserByName("default_picture").getAvatarPic();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<byte[]>(avatarPicBytes, headers, HttpStatus.OK);
    }
}
