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
public class AlbumMutualRegistrationVerifier
{
	private static final TestContext[] contexts = new TestContext[] {
			new TestContext(Album.class, "user", User.class, "albums", MANY_TO_ONE),
			new TestContext(Album.class, "photos", Photo.class, "album", ONE_TO_MANY) };

	static final Logger logger = LoggerFactory.getLogger(AlbumMutualRegistrationVerifier.class);

	private ITypeFactory typeFactory = new DomainTypeFactory();

	private TestContext testContext;

	@Parameters
	public static Collection<Object[]> data()
	{
		return VerifierHelper.fetchPopulatedContexts(contexts);
	}

	public AlbumMutualRegistrationVerifier(TestContext testContext)
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
