package name.verifier.factory;

public class NullTypeFactory implements ITypeFactory
{

	@Override
	public Object create(Class<?> propertyType)
	{
		return null;
	}
	
}