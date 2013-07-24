package org.societies.webapp;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.display.IDisplayPortalServer;
import org.societies.webapp.dao.ScreenDAO;
import org.societies.webapp.model.ScreenDataModel;
import org.societies.webapp.model.Screens;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import java.io.Serializable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean(name = "screenData", eager = true)
public class ScreenData implements Serializable {


    private Logger log = LoggerFactory.getLogger(ScreenData.class);

    private ScreenDataModel screenDataModel;
    private ScreenDAO screenDAO;
    private List<Screens> screenList;

    @ManagedProperty(value = "#{displayPortalServer}")
    private IDisplayPortalServer displayPortalServer;

    private Pattern pattern;
    private Matcher matcher;

    private Screens[] selectedScreens;

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";




    public ScreenData() {
    }

    @PostConstruct
    public void init() {
        this.screenDAO = new ScreenDAO();
        refreshScreens();
    }

    //CHECKS IP IS VALID IP
    public boolean checkIp(String sip) {
        log.debug(sip);
        pattern = Pattern.compile(IPADDRESS_PATTERN);
        matcher = pattern.matcher(sip);
        return matcher.matches();
    }


    //CHECKS USER INPUT THEN ADDS SCREEN TO DB
    public void addScreen(Screens screen) {
        log.debug("IN VALIDATION");
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean screenAdded = false;

        String screenID = screen.getScreenID();
        String locationID = screen.getLocationID();
        String ipAddress = screen.getIpAddress();

         if(!screenID.isEmpty()  && !locationID.isEmpty()  && !ipAddress.isEmpty()) {
            if(checkIp(ipAddress))
            {
                screenAdded = true;
                screenDAO.save(screen);
                refreshScreens();
                this.displayPortalServer.setScreens();
                log.debug("Screen: " + screenID + " added to DB.");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Screen successfully added", "Screen: " + screenID + " has successfully been " +
                        "added to the database.");
            }
            else
            {
                log.debug("Screen: " + screenID + " does NOT have valid IP - NOT added to DB.");
                screenAdded = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid IP", "Please give a valid IP address for: " + screenID +".");
            }
        }
        else {
            log.debug("User left all fields empty.");
            screenAdded = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid fields!", "Please ensure all fields are filled in.");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("screenAdded", screenAdded);
    }

    //RETRIEVES SCREEN FROM DB TO BE DISPLAYED
    public void refreshScreens()
    {
        screenList = (List<Screens>) screenDAO.getAllScreens();
        this.screenDataModel = new ScreenDataModel(screenList);
        log.debug("Current Screen List: " + screenList.toString());
    }


    public void setSelectedScreens(Screens[] selectedScreens) {
        this.selectedScreens = selectedScreens;
    }

    public Screens[] getSelectedScreens() {
        return selectedScreens;
    }

    public ScreenDataModel getScreenDataModel() {
        return screenDataModel;
    }

    //DELETES USER SELECTED SCREENS FROM DB
    public void delete() {
        int count = 0;
        FacesMessage msg = null;
        for(Screens screens : selectedScreens)
        {
            log.debug("Deleting screen with ID: " + screens.getScreenID());
            screenDAO.deleteScreens(screens);
            count++;
        }
        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Delete Screens", String.valueOf(count) + " screens have been deleted!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        refreshScreens();
        selectedScreens = null;
    }


    /**
     * @return the displayPortalServer
     */
    public IDisplayPortalServer getDisplayPortalServer() {
        return displayPortalServer;
    }

    /**
     * @param displayPortalServer the displayPortalServer to set
     */
    public void setDisplayPortalServer(IDisplayPortalServer displayPortalServer) {
        this.displayPortalServer = displayPortalServer;
    }


}