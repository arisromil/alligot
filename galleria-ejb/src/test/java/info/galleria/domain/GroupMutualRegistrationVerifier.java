package info.galleria.domain;

import name.verifier.registration.VerifierHelper;
import name.verifier.registration.MutualRegistrationVerifier;
import static name.verifier.registration.context.RelationType.MANY_TO_MANY;

import java.util.Collection;

import name.verifier.factory.ITypeFactory;
import name.verifier.registration.context.TestContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.*;

@RunWith(Parameterized.class)
public class GroupMutualRegistrationVerifier
{
	private static final TestContext[] contexts = new TestContext[] { new TestContext(Group.class, "users", User.class,
			"groups", MANY_TO_MANY) };

	static final Logger logger = LoggerFactory.getLogger(GroupMutualRegistrationVerifier.class);

	private ITypeFactory typeFactory = new DomainTypeFactory();

	private TestContext testContext;

	@Parameters
	public static Collection<Object[]> data()
	{
		return VerifierHelper.fetchPopulatedContexts(contexts);
	}

	public GroupMutualRegistrationVerifier(TestContext testContext)
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
