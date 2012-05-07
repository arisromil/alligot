package info.galleria.service.jpa;

import info.galleria.domain.Group;
import info.galleria.service.GenericRepository;

import java.util.List;

import javax.ejb.*;
import javax.persistence.*;

@Stateless
@LocalBean
public class GroupRepository implements GenericRepository<Group, String>
{

	@PersistenceContext
	private EntityManager em;

	public GroupRepository()
	{
	}

	GroupRepository(EntityManager em)
	{
		this.em = em;
	}

	@Override
	public Group create(Group group)
	{
		em.persist(group);
		return group;
	}

	@Override
	public Group modify(Group group)
	{
		em.find(Group.class, group.getGroupId());
		Group mergedGroup = em.merge(group);
		return mergedGroup;
	}

	@Override
	public void delete(Group group)
	{
		Group mergedGroup = em.merge(group);
		em.remove(mergedGroup);
	}

	@Override
	public Group findById(String groupId)
	{
		Group group = em.find(Group.class, groupId);
		return group;
	}

	@Override
	public List<Group> findAll()
	{
		TypedQuery<Group> allGroupsQuery = em.createNamedQuery("info.galleria.domain.Group.findAllGroups", Group.class);
		List<Group> result = allGroupsQuery.getResultList();
		return result;
	}

}
