package info.galleria.domain;

import static name.reynolds.vineet.verifier.registration.context.RelationType.*;

import java.util.Collection;

import name.reynolds.vineet.verifier.factory.ITypeFactory;
import name.reynolds.vineet.verifier.registration.*;
import name.reynolds.vineet.verifier.registration.context.TestContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.*;

@RunWith(Parameterized.class)
public class UserMutualRegistrationVerifier
{
	private static final TestContext[] contexts = new TestContext[] {
			new TestContext(User.class, "albums", Album.class, "user", ONE_TO_MANY),
			new TestContext(User.class, "groups", Group.class, "users", MANY_TO_MANY) };

	static final Logger logger = LoggerFactory.getLogger(UserMutualRegistrationVerifier.class);

	private ITypeFactory typeFactory = new DomainTypeFactory();

	private TestContext testContext;

	@Parameters
	public static Collection<Object[]> data()
	{
		return VerifierHelper.fetchPopulatedContexts(contexts);
	}

	public UserMutualRegistrationVerifier(TestContext testContext)
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
