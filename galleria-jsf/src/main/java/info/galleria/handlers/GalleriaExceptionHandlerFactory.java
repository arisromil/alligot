package info.galleria.handlers;

import info.galleria.handlers.GalleriaExceptionHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 *
 * @author 
 */
public class GalleriaExceptionHandlerFactory extends ExceptionHandlerFactory {

    private ExceptionHandlerFactory parent;

    /**
     * Public Constructor
     * @param parent
     */
    public GalleriaExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    /**
     * {@inheritDoc }
     * @return
     */
    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler result = parent.getExceptionHandler();
        result = new GalleriaExceptionHandler(result);
        return result;
    }
}