package name.reynolds.vineet.verifier.registration;

import static name.reynolds.vineet.verifier.registration.context.RelationType.MANY_TO_MANY;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Collection;

import name.reynolds.vineet.verifier.factory.ITypeFactory;
import name.reynolds.vineet.verifier.registration.context.TestContext;

import org.slf4j.*;

class CollectionAddDuplicateVerifier extends MutualRegistrationVerifier
{
	private static final Logger logger = LoggerFactory.getLogger(CollectionAddDuplicateVerifier.class);

	CollectionAddDuplicateVerifier(TestContext context, ITypeFactory typeFactory)
	{
		super(context, typeFactory);
	}

	@Override
	public void verify()
	{
		Class<?> clazz = context.getClazz();
		Object system = typeFactory.create(clazz);
		Method addToCollection = getContextMethod(Operation.COLLECTION_ADD);
		Object collectionElement = typeFactory.create(context.getInverseClazz());
		Object[] arguments = new Object[] { collectionElement };
		try
		{
			addToCollection.invoke(system, arguments);
			addToCollection.invoke(system, arguments);

			Method readMethod = getPropertyReadMethod();
			Object property = readMethod.invoke(system, (Object[]) null);
			Collection<?> collection = (Collection<?>) property;
			assertTrue("The collection must contain one element", collection.size() == 1);
			assertTrue("The collection must contain the added element.", collection.contains(collectionElement));

			Method inverseReadMethod = getInversePropertyReadMethod();
			Object inverseProperty = inverseReadMethod.invoke(collectionElement, (Object[]) null);
			if (context.getRelationType().equals(MANY_TO_MANY))
			{
				Collection<?> inverseCollection = (Collection<?>) inverseProperty;
				assertTrue("The inverse collection must contain one element", inverseCollection.size() == 1);
				assertTrue("The inverse collection must contain the system.", inverseCollection.contains(system));
			}
			else
			{
				// Relation type can only be One-To-Many here for this test.
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
