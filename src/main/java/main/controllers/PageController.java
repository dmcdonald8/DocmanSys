package main.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/* MVC controller servicing URL requests and returning the appropriate page/view */
@Controller
public class PageController {

    /**************** Admin Pages **************/

    @RequestMapping(value = "/adminDashboard", method= RequestMethod.GET)
    public ModelAndView adminDashboard() {
        ModelAndView mv = new ModelAndView("adminDashboard");
        return mv;
    }

    @RequestMapping(value = "/createUser", method=RequestMethod.GET)
    public ModelAndView createUser() {
        ModelAndView mv = new ModelAndView("createUser");
        return mv;
    }

    @RequestMapping(value = "/viewUser", method=RequestMethod.GET)
    public ModelAndView viewUser() {
        ModelAndView mv = new ModelAndView("viewUser");
        return mv;
    }

    @RequestMapping(value = "/editUser", method=RequestMethod.GET)
    public ModelAndView editUser() {
        ModelAndView mv = new ModelAndView("editUser");
        return mv;
    }

    /**************** User Pages **************/

    @RequestMapping(value = "/userDashboard", method=RequestMethod.GET)
    public ModelAndView userDashboard() {
        ModelAndView mv = new ModelAndView("userDashboard");
        return mv;
    }

    @RequestMapping(value = "/shareDocument/{docId}", method = RequestMethod.GET)
    public ModelAndView shareDocument(){
        ModelAndView mv = new ModelAndView("shareDocument");
        return mv;
    }
    /* For test page only */
    @RequestMapping(value = "/testSharing")
    public String testSharing() { return "testSharing"; }

}
