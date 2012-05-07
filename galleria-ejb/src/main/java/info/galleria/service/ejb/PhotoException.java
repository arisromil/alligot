package info.galleria.service.ejb;

import java.util.Set;

import javax.validation.ConstraintViolation;

public class PhotoException extends ApplicationException
{

	public PhotoException(String message)
	{
		super(message);
	}

	public PhotoException(Throwable cause)
	{
		super(cause);
	}

	public PhotoException(Set<ConstraintViolation<?>> violations)
	{
		super(violations);
	}

}
