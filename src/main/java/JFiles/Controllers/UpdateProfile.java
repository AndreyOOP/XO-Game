package JFiles.Controllers;

import JFiles.Constants.*;
import JFiles.Constants.PageService.Check;
import JFiles.Constants.PageService.Message;
import JFiles.Constants.PageService.Tag;
import JFiles.service.SessionLogin.LoginSession;
import JFiles.service.SessionLogin.Session;
import JFiles.service.PageService;
import JFiles.service.HTMLMail;
import JFiles.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**Controller is responsible for update personal information page*/
@org.springframework.stereotype.Controller
public class UpdateProfile {

    //region Services declaration
    @Autowired
    private LoginSession loginSession;

    @Autowired
    private PageService pageService;

    @Autowired
    @Qualifier(value = "UserServiceBean")
    private UserService userService;

    @Autowired
    private HTMLMail    htmlMail;
    //endregion

    /**Method responsible for displaying form with current personal information*/
    @RequestMapping(value = "/myprofile/{authKey}", method = RequestMethod.GET)
    public String openMyProfile2(Model model, @PathVariable int authKey){

        Session session = loginSession.getSession(authKey);

        pageService.setModel(model)

                   .add( Tag.MAIN_MENU_USER_NAME           , session.getUserName())
                   .add( Tag.MAIN_MENU_USER_ROLE           , session.getUserRole())
                   .add( Tag.MAIN_MENU_IS_PROFILE_PAGE     , true)

                   .add( Tag.MYPROFILE_SAVED_USER_NAME     , session.getUserName())
                   .add( Tag.MYPROFILE_SAVED_USER_PASSWORD , session.getUserPassword())
                   .add( Tag.MYPROFILE_SAVED_EMAIL         , session.getUserEmail());

        return Page.MAIN_MENU;
    }

    /**Method responsible for personal information update<br>
     * It makes checks if any field has been updated, in case of update make appropriate checks for updated field<br>
     * If check successful update database record, send notification email about update*/
    @RequestMapping(value = "/myprofile/{authKey}", method = RequestMethod.POST)
    public String updateMyProfile(Model model,
                                  @PathVariable int authKey,
                                  @RequestParam String userName,
                                  @RequestParam String userPassword,
                                  @RequestParam String userEmail,
                                  @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) {

        Boolean updatePassword = false;
        Boolean updateEmail    = false;
        Boolean updateAvatar   = false;

        pageService.setModel(model)
                   .setUserName(userName)
                   .setFormUserName(userName)
                   .setFormUserPassword(userPassword)
                   .setFormUserEmail(userEmail)
                   .setAvatarFile(avatarFile)

                   .add( Tag.MAIN_MENU_USER_NAME           , userName)
                   .add( Tag.MAIN_MENU_USER_ROLE           , userService.getUserByName(userName).getRole())
                   .add( Tag.MAIN_MENU_IS_PROFILE_PAGE     , true)
                   .add( Tag.MAIN_MENU_AUTH_KEY, authKey)

                   .add( Tag.MYPROFILE_SAVED_USER_NAME     , userName)
                   .add( Tag.MYPROFILE_SAVED_USER_PASSWORD , userPassword)
                   .add( Tag.MYPROFILE_SAVED_EMAIL         , userEmail);


        //region Form Input Check
        if( pageService.isFieldUpdated( Check.NEW_PASSWORD)){

            if( pageService.makeCheck( Check.PASSWORD_BLANK)){

                pageService.add( Tag.MYPROFILE_ERR_USER_PASSWORD, Message.PASSWORD_BLANK);
                return Page.MAIN_MENU;
            }

            if( pageService.makeCheck( Check.PASSWORD_LENGTH)){

                pageService.add( Tag.MYPROFILE_ERR_USER_PASSWORD, Message.PASSWORD_LENGTH);
                return Page.MAIN_MENU;
            }

            if( pageService.makeCheck( Check.PASSWORD_SYNTAX)){

                pageService.add( Tag.MYPROFILE_ERR_USER_PASSWORD, Message.PASSWORD_SYNTAX);
                return Page.MAIN_MENU;
            }

            updatePassword = true;
        }


        if( pageService.isFieldUpdated( Check.NEW_EMAIL)){

            if( pageService.makeCheck( Check.EMAIL_IN_DATABASE)){

                pageService.add( Tag.MYPROFILE_ERR_EMAIL, Message.EMAIL_ALREADY_REGISTERED);
                return Page.MAIN_MENU;
            }

            updateEmail = true;
        }


        if( pageService.isFieldUpdated( Check.NEW_AVATAR)){

            updateAvatar = true;
        }


        if( updatePassword || updateEmail || updateAvatar){

            userService.updateUserInDatabase( userName, userPassword, userEmail, avatarFile);

            htmlMail.sendEmail( userName, userPassword, userEmail, Email.UPDATE);

            if( updatePassword)
                pageService.add( Tag.MYPROFILE_ERR_USER_PASSWORD, Message.UPDATED);

            if( updateEmail)
                pageService.add( Tag.MYPROFILE_ERR_EMAIL, Message.UPDATED);

            if( updateAvatar)
                pageService.add( Tag.MYPROFILE_ERR_AVATAR, Message.UPDATED);
        }
        else
            pageService.add( Tag.MYPROFILE_ERR_AVATAR, Message.NOTHING_UPDATE);
        //endregion

        return Page.MAIN_MENU;
    }

    /**Load myprofile.jsp used into mainmenu.jsp*/
    @RequestMapping(value = "/myprofilepagecontent")
    public String prepareMyProfileContent(){

        return Page.MYPROFILE;
    }

}
