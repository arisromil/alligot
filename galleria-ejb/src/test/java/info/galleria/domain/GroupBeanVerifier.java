package info.galleria.domain;

import java.beans.PropertyDescriptor;
import java.util.Collection;

import name.reynolds.vineet.verifier.bean.*;
import name.reynolds.vineet.verifier.factory.ITypeFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.*;

@RunWith(Parameterized.class)
public class GroupBeanVerifier
{
	private static final Class<Group> TEST_TYPE = Group.class;

	private static final Logger logger = LoggerFactory.getLogger(GroupBeanVerifier.class);

	private ITypeFactory typeFactory = new DomainTypeFactory();
	private PropertyDescriptor descriptor;

	@Parameters
	public static Collection<Object[]> properties()
	{
		return VerifierHelper.getProperties(TEST_TYPE);
	}

	public GroupBeanVerifier(PropertyDescriptor descriptor)
	{
		this.descriptor = descriptor;
	}

	@Test
	public void testProperty() throws Exception
	{
		logger.info("Verifying property {} in bean {}", descriptor.getName(), TEST_TYPE);
		BeanVerifier<Group> verifier = BeanVerifier.forProperty(TEST_TYPE, descriptor, typeFactory);
		verifier.verify();
	}
}
