package JFiles.Controllers;

import JFiles.Constants.*;
import JFiles.Constants.PageService.Check;
import JFiles.Constants.PageService.Message;
import JFiles.Constants.PageService.Tag;
import JFiles.service.SessionLogin.LoginSession;
import JFiles.service.SessionLogin.Session;
import JFiles.model.UserEntity;
import JFiles.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**Controller for work with menu <i>Admin->Users Table</i><br>
 * Responsible for display User table content, creating new records and edit existing records*/
@org.springframework.stereotype.Controller
public class AdminUserTable {

    //region Services declaration
    @Autowired
    private TableUtil         tableUtil;

    @Autowired
    private PageService page;

    @Autowired
    @Qualifier(value = "UserServiceBean")
    private UserService       userService;

    @Autowired
    private HTMLMail          htmlMail;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private LoginSession loginSession;
    //endregion

    /**Load User table based on tableCurrentPage parameter<br>
     * tableCurrentPage - page of User table to display<br>
     * Display table parameters are defined into Table interface and calculated via TableUtil service */
    @RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    public String adminUsers(Model model,
                             @RequestParam int authKey,
                             @RequestParam int tableCurrentPage){

        Session session = loginSession.getSession(authKey);

        if( !(session.getUserRole() == Role.ADMIN || session.getUserRole() == Role.SUPER_ADMIN))
            return Page.ERROR;

        tableUtil.setParam( tableCurrentPage, userService.getAllUsers().size());

        page.setModel(model)

            .add( Tag.MAIN_MENU_AUTH_KEY           , authKey)
            .add( Tag.MAIN_MENU_USER_NAME          , session.getUserName())
            .add( Tag.MAIN_MENU_USER_ROLE          , session.getUserRole())
            .add( Tag.MAIN_MENU_IS_ADMIN_USER_PAGE , true)

            .add( Tag.ADMIN_USER_CURRENT_PAGE , tableCurrentPage)
            .add( Tag.ADMIN_USER_LIST         , tableUtil.getUserRecords(tableCurrentPage))
            .add( Tag.TABLE_FROM_PAGE         , tableUtil.getFromPage())
            .add( Tag.TABLE_TO_PAGE           , tableUtil.getToPage())
            .add( Tag.TABLE_PREVIOUS          , tableUtil.getPrev())
            .add( Tag.TABLE_NEXT              , tableUtil.getNext());

        return Page.MAIN_MENU;
    }

    /**Delete record based on deleteUserName parameter<br>*/
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String removeUser(RedirectAttributes redirectAttributes,
                             @RequestParam int authKey,
                             @RequestParam String deleteUser,
                             @RequestParam int tableCurrentPage){


        Session session = loginSession.getSession(authKey);

        if( session.getUserRole() != Role.SUPER_ADMIN)
            return Page.ERROR;

        if( !deleteUser.contentEquals( session.getUserName())){ //to avoid yourself deletion

            userService.remove( deleteUser);
        }else{
            return Page.ERROR;
        }

        page.setRedirectAttributes(redirectAttributes)
                .addRedirect( Tag.MAIN_MENU_AUTH_KEY      , authKey)
                .addRedirect( Tag.ADMIN_USER_CURRENT_PAGE , tableCurrentPage);

        return "redirect:" + Page.ADMIN_USER_MENU;
    }

    /**Reset password based on user name<br>
     * Generate new password via <i>PasswordGenerator</i> service<br>
     * Generate and send email with new password via <i>htmlEmail</i> service */
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public String resetPassword(RedirectAttributes redirectAttributes,
                                @RequestParam int authKey,
                                @RequestParam String resetUser,
                                @RequestParam int tableCurrentPage){


        int role = loginSession.getSession(authKey).getUserRole();

        if( !(role == Role.ADMIN || role == Role.SUPER_ADMIN))
            return Page.ERROR;

        String newPassword = passwordGenerator.generate();
        String email       = userService.getUserByName(resetUser).getEmail();

        userService.updateUserInDatabase(resetUser, newPassword);

        htmlMail.sendEmail( resetUser, newPassword, email, Email.PASSWORD_RESET); //todo separate thread, approx 10 second to send email

        page.setRedirectAttributes(redirectAttributes)
                .addRedirect( Tag.MAIN_MENU_AUTH_KEY      , authKey)
                .addRedirect( Tag.ADMIN_USER_CURRENT_PAGE , tableCurrentPage);

        return "redirect:" + Page.ADMIN_USER_MENU;
    }

    /**Proceed POST request of add form of <i>User</i> table<br>
     * Make checks of entered information<br>
     * Depend on check add record to database or show error message    */
    @RequestMapping(value = "/addnewuser", method = RequestMethod.POST)
    public String addNewUser(RedirectAttributes redirectAttributes, Model model,
                             @RequestParam int authKey,
                             @RequestParam String formuserName,
                             @RequestParam String userPassword,
                             @RequestParam String userEmail,
                             @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                             @RequestParam String userRole,
                             @RequestParam int tableCurrentPage){


        Session session = loginSession.getSession(authKey);

        if( session.getUserRole() != Role.SUPER_ADMIN)
            return Page.ERROR;

        tableUtil.setParam(tableCurrentPage, userService.getAllUsers().size());

        page.setModel(model)
            .setRedirectAttributes(redirectAttributes)
            .setFormUserName(formuserName)
            .setFormUserPassword(userPassword)
            .setFormUserEmail(userEmail)
            .setFormUserRole(userRole)

            .add( Tag.MAIN_MENU_AUTH_KEY           , authKey)
            .add( Tag.MAIN_MENU_USER_ROLE          , session.getUserRole())
            .add( Tag.MAIN_MENU_IS_ADMIN_USER_PAGE , true)

            .add( Tag.TABLE_FROM_PAGE              , tableUtil.getFromPage())
            .add( Tag.TABLE_TO_PAGE                , tableUtil.getToPage())
            .add( Tag.TABLE_PREVIOUS               , tableUtil.getPrev())
            .add( Tag.TABLE_NEXT                   , tableUtil.getNext())

            .add( Tag.ADMIN_USER_LIST            , tableUtil.getUserRecords( tableCurrentPage))
            .add( Tag.ADMIN_USER_SHOW_ADD_MENU   , true)
            .add( Tag.ADMIN_USER_CURRENT_PAGE    , tableCurrentPage)
            .add( Tag.ADMIN_USER_SAVED_NAME      , formuserName)
            .add( Tag.ADMIN_USER_SAVED_PASSWORD  , userPassword)
            .add( Tag.ADMIN_USER_SAVED_USER_ROLE , userRole)
            .add( Tag.ADMIN_USER_SAVED_EMAIL     , userEmail);


        //region Checks
        if( page.makeCheck( Check.USER_NAME_BLANK)){

            page.add( Tag.ADMIN_USER_ERR_USER_NAME, Message.USER_NAME_BLANK);
            return Page.MAIN_MENU;
        }

        if( page.makeCheck( Check.USER_ALREADY_REGISTERED)){

            page.add( Tag.ADMIN_USER_ERR_USER_NAME, Message.USER_ALREADY_REGISTERED);
            return Page.MAIN_MENU;
        }

        if( page.makeCheck( Check.PASSWORD_BLANK)){

            page.add( Tag.ADMIN_USER_ERR_USER_PASSWORD, Message.PASSWORD_BLANK);
            return Page.MAIN_MENU;
        }

        if( page.makeCheck( Check.USER_ROLE)) {

            page.add( Tag.ADMIN_USER_ERR_USER_ROLE, Message.USER_ROLE_NOT_FOUND);
            return Page.MAIN_MENU;
        }

        if( page.makeCheck( Check.EMAIL_IN_DATABASE)) {

            page.add( Tag.ADMIN_USER_ERR_EMAIL, Message.EMAIL_ALREADY_REGISTERED);
            return Page.MAIN_MENU;
        }
        //endregion

        userService.addUser(formuserName, userPassword, getRoleId( userRole), userEmail, avatarFile);

        page.addRedirect( Tag.MAIN_MENU_AUTH_KEY      , authKey)
            .addRedirect( Tag.ADMIN_USER_CURRENT_PAGE , tableCurrentPage);

        return "redirect:" + Page.ADMIN_USER_MENU;
    }

    /**Method loads edit form based on userName to edit<br>
     * as well add to Model parameters required for <i>Admin->User Table</i> menu display*/
    @RequestMapping(value = "/edituser", method = RequestMethod.GET)
    public String editUser(Model model,
                           @RequestParam int authKey,
                           @RequestParam String editUser,
                           @RequestParam int tableCurrentPage){


        Session session = loginSession.getSession(authKey);

        if(session.getUserRole() != Role.SUPER_ADMIN){
            return Page.ERROR;
        }

        tableUtil.setParam(tableCurrentPage, userService.getAllUsers().size());

        UserEntity editUserEnt = userService.getUserByName(editUser);

        page.setModel(model)

                .add( Tag.MAIN_MENU_AUTH_KEY           , authKey)
                .add( Tag.MAIN_MENU_IS_ADMIN_USER_PAGE , true)
                .add( Tag.MAIN_MENU_USER_ROLE          , session.getUserRole())


                .add( Tag.TABLE_FROM_PAGE , tableUtil.getFromPage())
                .add( Tag.TABLE_TO_PAGE   , tableUtil.getToPage())
                .add( Tag.TABLE_PREVIOUS  , tableUtil.getPrev())
                .add( Tag.TABLE_NEXT      , tableUtil.getNext())

                .add( Tag.ADMIN_USER_LIST            , tableUtil.getUserRecords( tableCurrentPage))
                .add( Tag.ADMIN_USER_SHOW_EDIT_MENU  , true)
                .add( Tag.ADMIN_USER_CURRENT_PAGE    , tableCurrentPage)

                .add( Tag.ADMIN_USER_SAVED_NAME      , editUser)
                .add( Tag.ADMIN_USER_SAVED_PASSWORD  , editUserEnt.getPassword())
                .add( Tag.ADMIN_USER_SAVED_USER_ROLE , getRoleName(editUserEnt.getRole()))
                .add( Tag.ADMIN_USER_SAVED_EMAIL     , editUserEnt.getEmail());

        return Page.MAIN_MENU;
    }

    /**Proceed POST request of edit form of <i>User</i> table<br>
     * Make checks are email and password fields updated, if updated check them<br>
     * Based on check update record in database or show error message    */
    @RequestMapping(value = "/edituser", method = RequestMethod.POST)
    public String addNewUser(Model model, RedirectAttributes redirectAttributes,
                             @RequestParam int authKey,
                             @RequestParam int tableCurrentPage,
                             @RequestParam String editName,
                             @RequestParam String userPassword,
                             @RequestParam String userEmail,
                             @RequestParam String userRole,
                             @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile){

        Session session = loginSession.getSession(authKey);

        if( session.getUserRole() != Role.SUPER_ADMIN){
            return Page.ERROR;
        }

        tableUtil.setParam(tableCurrentPage, userService.getAllUsers().size());

        page.setModel(model)
                .setUserName(editName)
                .setFormUserPassword(userPassword)
                .setFormUserEmail(userEmail)
                .setRedirectAttributes(redirectAttributes)

                .add( Tag.MAIN_MENU_AUTH_KEY           , authKey)
                .add( Tag.MAIN_MENU_IS_ADMIN_USER_PAGE , true)
                .add( Tag.MAIN_MENU_USER_ROLE          , session.getUserRole())

                .add( Tag.TABLE_FROM_PAGE , tableUtil.getFromPage())
                .add( Tag.TABLE_TO_PAGE   , tableUtil.getToPage())
                .add( Tag.TABLE_PREVIOUS  , tableUtil.getPrev())
                .add( Tag.TABLE_NEXT      , tableUtil.getNext())

                .add( Tag.ADMIN_USER_LIST            , tableUtil.getUserRecords( tableCurrentPage))
                .add( Tag.ADMIN_USER_SHOW_EDIT_MENU  , true)
                .add( Tag.ADMIN_USER_CURRENT_PAGE    , tableCurrentPage)

                .add( Tag.ADMIN_USER_SAVED_NAME      , editName)
                .add( Tag.ADMIN_USER_SAVED_PASSWORD  , userPassword)
                .add( Tag.ADMIN_USER_SAVED_USER_ROLE , userRole)
                .add( Tag.ADMIN_USER_SAVED_EMAIL     , userEmail);


        if ( page.isFieldUpdated( Check.NEW_PASSWORD)) {

            if( page.makeCheck( Check.PASSWORD_BLANK)){

                page.add( Tag.ADMIN_USER_ERR_USER_PASSWORD, Message.PASSWORD_BLANK);
                return Page.MAIN_MENU;
            }
        }

        if( page.isFieldUpdated(Check.NEW_EMAIL)){

            if( page.makeCheck( Check.EMAIL_IN_DATABASE)) {

                page.add( Tag.ADMIN_USER_ERR_EMAIL, Message.EMAIL_ALREADY_REGISTERED);
                return Page.MAIN_MENU;
            }
        }

        userService.updateUserInDatabase(editName, userPassword, userEmail, avatarFile, getRoleId(userRole));

        page.addRedirect( Tag.MAIN_MENU_AUTH_KEY      , authKey)
            .addRedirect( Tag.ADMIN_USER_CURRENT_PAGE , tableCurrentPage);

        return  "redirect:" + Page.ADMIN_USER_MENU;
    }

    /**Load adminuser.jsp used into mainmenu.jsp*/
    @RequestMapping(value = "/superadminmenupagecontent")
    public String prepareMyProfileContent(){

        return Page.ADMIN_USER;
    }

    /**Load adduser.jsp used into adminuser.jsp*/
    @RequestMapping(value = "/addnewuserpagecontent")
    public String prepareAddNewUserForm(){

        return Page.ADMIN_USER_ADD;
    }

    /**Load edituser.jsp used into adminuser.jsp*/
    @RequestMapping(value = "/edituserpagecontent")
    public String prepareEditUserForm(){

        return Page.ADMIN_USER_EDIT;
    }

    private int getRoleId(String roleName){

        if( roleName.contentEquals( Role.USER_NAME))
            return Role.USER;

        if( roleName.contentEquals( Role.ADMIN_NAME))
            return Role.ADMIN;

        if( roleName.contentEquals( Role.SUPER_ADMIN_NAME))
            return Role.SUPER_ADMIN;

        return Role.USER;
    }

    private String getRoleName(int roleId){

        if( roleId == Role.USER)
            return Role.USER_NAME;

        if( roleId == Role.ADMIN)
            return Role.ADMIN_NAME;

        if( roleId == Role.SUPER_ADMIN)
            return Role.SUPER_ADMIN_NAME;

        return Role.USER_NAME;
    }

}
