package info.galleria.domain;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AlbumBeanVerifier.class, AlbumOtherTests.class, AlbumMutualRegistrationVerifier.class })
public class AlbumSuite
{

}
