package info.galleria.service.ejb;

import java.util.Set;

import javax.validation.ConstraintViolation;

public class AlbumException extends ApplicationException
{

	public AlbumException(String message)
	{
		super(message);
	}

	public AlbumException(Throwable cause)
	{
		super(cause);
	}

	public AlbumException(Set<ConstraintViolation<?>> violations)
	{
		super(violations);
	}

}
