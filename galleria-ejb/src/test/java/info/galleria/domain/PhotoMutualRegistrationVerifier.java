package info.galleria.domain;

import name.verifier.registration.VerifierHelper;
import name.verifier.registration.MutualRegistrationVerifier;
import static name.verifier.registration.context.RelationType.MANY_TO_ONE;

import java.util.Collection;

import name.verifier.factory.ITypeFactory;
import name.verifier.registration.context.TestContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.*;

@RunWith(Parameterized.class)
public class PhotoMutualRegistrationVerifier
{
	private static final TestContext[] contexts = new TestContext[] { new TestContext(Photo.class, "album",
			Album.class, "photos", MANY_TO_ONE) };

	static final Logger logger = LoggerFactory.getLogger(PhotoMutualRegistrationVerifier.class);

	private ITypeFactory typeFactory = new DomainTypeFactory();

	private TestContext testContext;

	@Parameters
	public static Collection<Object[]> data()
	{
		return VerifierHelper.fetchPopulatedContexts(contexts);
	}

	public PhotoMutualRegistrationVerifier(TestContext testContext)
	{
		this.testContext = testContext;
	}

	@Test
	public void testProperty() throws Exception
	{
		logger.info("Executing Test Context {}", testContext);
		MutualRegistrationVerifier verifier = MutualRegistrationVerifier.forContext(testContext, typeFactory);
		verifier.verify();
	}
}
