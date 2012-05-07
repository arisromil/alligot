package info.galleria.service.ejb;

import info.galleria.domain.Group;

import javax.ejb.Local;

@Local
public interface GroupService
{
	public Group getOrCreateRegisteredUsersGroup();
}
