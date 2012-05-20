/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.galleria.handlers;

import java.util.Iterator;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

/**
 * The Galleria Exception handler
 * @author atan
 */
public class GalleriaExceptionHandler extends ExceptionHandlerWrapper {

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
            if (t instanceof ViewExpiredException) {
                ViewExpiredException vee       = (ViewExpiredException) t;
                FacesContext fc                = FacesContext.getCurrentInstance();
                Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
                NavigationHandler nav          = fc.getApplication().getNavigationHandler();
                try {
                    // Push some stuff to the request scope for later use in the page
                    requestMap.put("currentViewId", vee.getViewId());

                    nav.handleNavigation(fc, null, "viewExpired");
                    fc.renderResponse();

                } finally {
                    i.remove();
                }
            }
        }
        // Let the parent handle all the remaining queued exception events.
        getWrapped().handle();
    }
}
