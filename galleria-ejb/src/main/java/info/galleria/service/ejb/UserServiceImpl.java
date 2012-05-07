package info.galleria.service.ejb;

import info.galleria.domain.*;
import info.galleria.service.jpa.UserRepository;
import info.galleria.utilities.PasswordUtility;

import java.util.*;

import javax.annotation.Resource;
import javax.annotation.security.*;
import javax.ejb.*;
import javax.persistence.EntityExistsException;
import javax.validation.*;

import org.slf4j.*;

@Stateless
@EJB(name = "java:global/galleria/galleria-ejb/UserService", beanInterface = UserService.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserServiceImpl implements UserService
{
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@EJB
	private UserRepository userRepository;

	@EJB
	private GroupService groupService;

	@Resource
	private SessionContext context;

	@Override
	@PermitAll
	public User signupUser(User user) throws UserException
	{
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set violations = validator.validate(user);
		if (violations.size() > 0)
		{
			throw new UserException(violations);
		}

		User existingUser = userRepository.findById(user.getUserId());
		if (existingUser != null)
		{
			logger.error("Attempted to create a duplicate user.");
			throw new UserException(DUPLICATE_USER);
		}

		char[] digest = PasswordUtility.getDigest(user.getPassword(), "SHA-512");
		user.setPassword(digest);

		Group group = groupService.getOrCreateRegisteredUsersGroup();
		user.addToGroups(group);
		try
		{
			user = userRepository.create(user);
		}
		catch (EntityExistsException entityExistsEx)
		{
			logger.error("Attempted to create a duplicate user.");
			throw new UserException(DUPLICATE_USER, entityExistsEx);
		}
		return user;
	}

	@Override
	@RolesAllowed({ "RegisteredUsers" })
	public void deleteUserAccount() throws UserException
	{
		String userId = context.getCallerPrincipal().getName();
		User user = userRepository.findById(userId);
		if (user == null)
		{
			logger.error("The principal for the invoker was not found in the database.");
			throw new UserException(INTERNAL_ERROR_ON_DELETE);
		}
		else
		{
			userRepository.delete(user);
		}
	}

	@Override
	@RolesAllowed({ "RegisteredUsers" })
	public void modifyPassword(ModifyPasswordRequest request) throws ModifyPasswordException
	{
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set violations = validator.validate(request);
		if (violations.size() > 0)
		{
			throw new ModifyPasswordException(violations);
		}
		if (Arrays.equals(request.getOldPassword(), request.getNewPassword()))
		{
			throw new ModifyPasswordException(SAME_OLD_AND_NEW_PASSWORDS);
		}
		if (!Arrays.equals(request.getNewPassword(), request.getConfirmNewPassword()))
		{
			throw new ModifyPasswordException(MISMATCH_NEW_AND_CONFIRMED_PASSWORDS);
		}

		String userId = context.getCallerPrincipal().getName();
		User user = userRepository.findById(userId);
		if (user == null)
		{
			throw new ModifyPasswordException(INTERNAL_ERROR_ON_PASSWORD_CHANGE);
		}

		char[] oldDigest = PasswordUtility.getDigest(request.getOldPassword(), "SHA-512");
		char[] currentDigest = user.getPassword();
		if (!Arrays.equals(oldDigest, currentDigest))
		{
			throw new ModifyPasswordException(MISMATCH_PROVIDED_AND_CURRENT_PASSWORDS);
		}
		char[] digest = PasswordUtility.getDigest(request.getNewPassword(), "SHA-512");
		user.setPassword(digest);
		userRepository.modify(user);
	}

}
