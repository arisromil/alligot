package info.galleria.domain;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ UserBeanVerifier.class, UserEqualsAndConstructorVerifier.class, UserMutualRegistrationVerifier.class })
public class UserSuite
{

}
