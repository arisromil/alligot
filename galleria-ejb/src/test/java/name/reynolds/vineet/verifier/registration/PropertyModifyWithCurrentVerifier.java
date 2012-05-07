package name.reynolds.vineet.verifier.registration;

import static name.reynolds.vineet.verifier.registration.context.RelationType.MANY_TO_ONE;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Collection;

import name.reynolds.vineet.verifier.factory.ITypeFactory;
import name.reynolds.vineet.verifier.registration.context.TestContext;

import org.slf4j.*;

class PropertyModifyWithCurrentVerifier extends MutualRegistrationVerifier
{
	private static final Logger logger = LoggerFactory.getLogger(PropertyModifyWithCurrentVerifier.class);

	PropertyModifyWithCurrentVerifier(TestContext context, ITypeFactory typeFactory)
	{
		super(context, typeFactory);
	}

	@Override
	public void verify()
	{
		Class<?> clazz = context.getClazz();
		Object system = typeFactory.create(clazz);
		Method modifyProperty = getContextMethod(Operation.PROPERTY_MODIFY);

		Object property = typeFactory.create(context.getInverseClazz());
		Object[] arguments = new Object[] { property };
		try
		{
			modifyProperty.invoke(system, arguments);

			modifyProperty.invoke(system, arguments);

			Method readMethod = getPropertyReadMethod();
			Object actualProperty = readMethod.invoke(system, (Object[]) null);

			assertNotNull(actualProperty);
			assertEquals(property, actualProperty);

			Method inverseReadMethod = getInversePropertyReadMethod();
			Object inverseProperty = inverseReadMethod.invoke(property, (Object[]) null);
			if (context.getRelationType().equals(MANY_TO_ONE))
			{
				Collection<?> inverseCollection = (Collection<?>) inverseProperty;
				assertTrue("The inverse collection must contain one element", inverseCollection.size() == 1);
				assertTrue("The inverse collection must contain the system.", inverseCollection.contains(system));
			}
			else
			{
				assertEquals(system, inverseProperty);
			}
		}
		catch (Exception ex)
		{
			logger.error("Failed to invoke the method under test in the SUT.", ex);
			fail("Failed to invoke the method under test in the SUT.");
		}
	}

}
