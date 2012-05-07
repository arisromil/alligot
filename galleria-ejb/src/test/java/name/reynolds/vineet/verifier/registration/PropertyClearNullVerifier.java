package name.reynolds.vineet.verifier.registration;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import name.reynolds.vineet.verifier.factory.ITypeFactory;
import name.reynolds.vineet.verifier.registration.context.TestContext;

import org.slf4j.*;

class PropertyClearNullVerifier extends MutualRegistrationVerifier
{
	private static final Logger logger = LoggerFactory.getLogger(PropertyClearNullVerifier.class);

	PropertyClearNullVerifier(TestContext context, ITypeFactory typeFactory)
	{
		super(context, typeFactory);
	}

	@Override
	public void verify()
	{
		Class<?> clazz = context.getClazz();
		Object system = typeFactory.create(clazz);
		Method clearProperty = getContextMethod(Operation.PROPERTY_CLEAR);

		Object[] arguments = new Object[] {};
		try
		{
			clearProperty.invoke(system, arguments);

			Method readMethod = getPropertyReadMethod();
			Object actualProperty = readMethod.invoke(system, (Object[]) null);

			assertNull(actualProperty);

			// Cannot verify anything on the inverse-side as there is no inverse
			// object whose state is to be verified.
		}
		catch (Exception ex)
		{
			logger.error("Failed to invoke the method under test in the SUT.", ex);
			fail("Failed to invoke the method under test in the SUT.");
		}
	}

}
