package JFiles.Controllers;

import JFiles.Constants.Page;
import JFiles.Constants.PageService.Tag;
import JFiles.service.SessionLogin.LoginSession;
import JFiles.service.SessionLogin.Session;
import JFiles.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**Controller is responsible for welcome menu display*/
@org.springframework.stereotype.Controller
public class WelcomePage {

    //region Services declaration
    @Autowired
    private PageService pageService;

    @Autowired
    private LoginSession loginSession;
    //endregion

    /**Load welcome page*/
    @RequestMapping(value = "/welcome/{authKey}", method = RequestMethod.GET)
    public String welcome(Model model, @PathVariable int authKey){

        Session session = loginSession.getSession(authKey);

        if(session == null)
            return Page.ERROR;

        pageService.setModel(model)

                .add( Tag.MAIN_MENU_USER_NAME    , session.getUserName())
                .add( Tag.MAIN_MENU_USER_ROLE    , session.getUserRole())
                .add( Tag.MAIN_MENU_WELCOME_PAGE , true);

        return Page.MAIN_MENU;
    }

    /**Load welcome.jsp used into mainmenu.jsp*/
    @RequestMapping(value = "/welcomepagecontent")
    public String loadWelcomePageContent(){

        return Page.WELCOME;
    }

}
