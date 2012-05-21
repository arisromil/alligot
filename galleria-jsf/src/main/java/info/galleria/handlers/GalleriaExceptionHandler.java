/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.galleria.handlers;



import info.galleria.i18n.Messages;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Galleria Exception handler
 * @author atan
 */
public class GalleriaExceptionHandler extends ExceptionHandlerWrapper {
    
    private static final Logger logger = LoggerFactory.getLogger(GalleriaExceptionHandler.class);

    private ExceptionHandler wrapped;

    /**
     * Constructor for setting the wrapped handler
     * @param wrapped
     */
    public GalleriaExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Getting the wrapped parent
     * @return
     */
    @Override
    public ExceptionHandler getWrapped() {
        return this.wrapped;
    }

    /**
     * {@inheritDoc }
     * @throws FacesException
     */
    @Override
    public void handle() throws FacesException {
        for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
            Throwable t = context.getException();
            
            FacesContext fc                = FacesContext.getCurrentInstance();
            Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
            NavigationHandler nav          = fc.getApplication().getNavigationHandler();
            
            if (t instanceof ViewExpiredException) {
                ViewExpiredException vee       = (ViewExpiredException) t;
                
                try {
                    // Push some stuff to the request scope for later use in the page
                    requestMap.put("currentViewId", vee.getViewId());

                    nav.handleNavigation(fc, null, "viewExpired");
                    fc.renderResponse();

                } finally {
                    i.remove();
                }
            }else {
               String forwardView = "generalError";

               Locale locale = fc.getViewRoot().getLocale();
               String key = "Excepetion.GeneralError";
               logger.error(Messages.getLoggerString(key), t);
               
               String errorCode = String.valueOf(Math.abs(new Date().hashCode()));
               
               logger.error(Messages.getLoggerString(key), t);
               requestMap.put("errorCode", errorCode);
               
               String message = Messages.getString(key, locale);
               FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
               fc.addMessage(null, facesMessage);
           }
        }
        // Let the parent handle all the remaining queued exception events.
        getWrapped().handle();
    }
}
