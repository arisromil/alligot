package info.galleria.service.ejb;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import info.galleria.domain.*;
import info.galleria.service.jpa.AlbumRepository;
import info.galleria.service.jpa.PhotoRepository;
import info.galleria.service.jpa.UserRepository;
import static info.galleria.utilities.Settings.ALLIGOT_HASHTAG;
import static info.galleria.utilities.Settings.TIMELINE_URL;
import static info.galleria.utilities.Settings.ZERO_PRICE;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.*;
import javax.validation.Validation;
import javax.validation.Validator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@EJB(name = "java:global/galleria/galleria-ejb/PhotoService", beanInterface = PhotoService.class)
@RolesAllowed({ "RegisteredUsers" })
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PhotoServiceImpl implements PhotoService
{
	private static final Logger logger = LoggerFactory.getLogger(PhotoServiceImpl.class);

	@Resource
	private SessionContext context;

	@EJB
	private AlbumService albumService;

	@EJB
	private UserRepository userRepository;

	@EJB
	private AlbumRepository albumRepository;

	@EJB
	private PhotoRepository photoRepository;

        
	@Override
	public Photo fetchOnlinePhoto() throws PhotoException, MalformedURLException, IOException
	{
                Photo photo = null;
            
                Client client = Client.create();
                
                StringBuilder restCall = new StringBuilder(TIMELINE_URL);
                restCall.append(context.getCallerPrincipal().getName());
                    
                ClientResponse response = client.resource(restCall.toString()).get(ClientResponse.class);
        
                List<Status> statuses = response.getEntity(new GenericType<List<Status>>(){});   
                for (Status st :  statuses) {
                    for (TwitterEntity twitterEntity : st.getTwitterEntity()) {                        
                        for (HashTag hashtag : twitterEntity.getHashtags()) {
                                    if (ALLIGOT_HASHTAG.equalsIgnoreCase(hashtag.getTag().getText())) {
                                        URL url = new URL(twitterEntity.getMedia().getCreativeStart().getMediaUrl());
                                        File destination = new File(st.getId()+".jpg");
                                        FileUtils.copyURLToFile(url, destination);
                                        byte[] fileBytes = FileUtils.readFileToByteArray(destination);
                                        photo = new Photo(st.getId(), fileBytes, st.getText(),st.getText(), ZERO_PRICE);
                                    }
                        }                                                 
                    }
                }
            
                
                
            return photo;
        }
        
        
	@Override
	public Photo uploadPhoto(Photo photo, Album album) throws PhotoException
	{
		validatePhoto(photo);

		User user = findCurrentUser(UPLOAD_PHOTO_INTERNAL_ERROR_MESSAGE);
		Album albumInRepository = albumRepository.findById(album.getAlbumId());
		User albumOwner = albumInRepository.getUser();
		if (!user.equals(albumOwner))
		{
			throw new PhotoException(UPLOAD_PHOTO_INTERNAL_ERROR_MESSAGE);
		}
		else if (albumInRepository.getPhotos().contains(photo))
		{
			throw new PhotoException(DUPLICATE_PHOTO_ON_UPLOAD);
		}
		else
		{
			photo.setUploadTime(new Date());
			photo.modifyAlbum(albumInRepository);
			Photo createdPhoto = photoRepository.create(photo);
			return createdPhoto;
		}
	}

	@Override
	public Photo modifyPhoto(Photo photo) throws PhotoException
	{
		validatePhoto(photo);

		User user = findCurrentUser(MODIFY_PHOTO_INTERNAL_ERROR_MESSAGE);
		Photo foundPhoto = photoRepository.findById(photo.getPhotoId());
		User photoOwner = foundPhoto.getAlbum().getUser();
		if (user.equals(photoOwner))
		{
			foundPhoto.setTitle(photo.getTitle());
			foundPhoto.setDescription(photo.getDescription());
                        foundPhoto.setPrice(photo.getPrice());
			Photo modifiedPhoto = photoRepository.modify(foundPhoto);
			return modifiedPhoto;
		}
		else
		{
			throw new PhotoException(MODIFY_PHOTO_INTERNAL_ERROR_MESSAGE);
		}
	}

	@Override
	public void deletePhoto(Photo photo) throws PhotoException
	{
		User user = findCurrentUser(DELETE_PHOTO_INTERNAL_ERROR_MESSAGE);
		Photo foundPhoto = photoRepository.findById(photo.getPhotoId());
		User photoOwner = foundPhoto.getAlbum().getUser();
		if (user.equals(photoOwner))
		{
			logger.info("Flagging to delete photo {}", foundPhoto);
			photoRepository.delete(foundPhoto);
			return;
		}
		else
		{
			throw new PhotoException(DELETE_PHOTO_INTERNAL_ERROR_MESSAGE);
		}
	}

	@Override
	public Photo findPhotoById(long photoId, boolean withContents) throws PhotoException
	{
		User user = findCurrentUser(FIND_PHOTO_BY_ID_INTERNAL_ERROR_MESSAGE);
		Photo foundPhoto = photoRepository.findById(photoId);
		User photoOwner = foundPhoto.getAlbum().getUser();
		if (user.equals(photoOwner))
		{
			if (withContents)
			{
				int length = foundPhoto.getFile().length;
				logger.info("Returning photo {} with content of size {} matching Id {}", new Object[] { foundPhoto,
						length, photoId });
				return foundPhoto;
			}
			else
			{
				logger.info("Returning photo {} matching Id {}", new Object[] { foundPhoto, photoId });
				return foundPhoto;
			}
		}
		else
		{
			throw new PhotoException(FIND_PHOTO_BY_ID_INTERNAL_ERROR_MESSAGE);
		}
	}

	@Override
	public List<Photo> findPhotosByAlbum(Album album) throws PhotoException
	{
		try
		{
			Album foundAlbum = albumService.findAlbumById(album.getAlbumId());
			Set<Photo> photoSet = foundAlbum.getPhotos();
			List<Photo> photos = new ArrayList<Photo>(photoSet);
			return photos;
		}
		catch (AlbumException albumEx)
		{
			throw new PhotoException(albumEx);
		}
	}

	@Override
	public void setAlbumCover(Photo photo) throws PhotoException
	{
		validatePhoto(photo);

		User user = findCurrentUser(MODIFY_COVER_INTERNAL_ERROR_MESSAGE);
		Album album = albumRepository.findById(photo.getAlbum().getAlbumId());
		User albumOwner = album.getUser();
		if (!user.equals(albumOwner))
		{
			throw new PhotoException(MODIFY_COVER_INTERNAL_ERROR_MESSAGE);
		}
		else if (!album.getPhotos().contains(photo))
		{
			throw new PhotoException(MODIFY_COVER_INTERNAL_ERROR_MESSAGE);
		}
		else
		{
			album.modifyCoverPhoto(photo);
			albumRepository.modify(album);
		}
	}

	private void validatePhoto(Photo photo) throws PhotoException
	{
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set violations = validator.validate(photo);
		if (violations.size() > 0)
		{
			logger.warn("An invalid album entity was provided. Violations detected were {}", violations);
			throw new PhotoException(violations);
		}
	}

	/**
	 * @param contextExceptionMessage
	 *            The message to be contained in the AlbumException, if one were
	 *            to be thrown.
	 * @return
	 * @throws PhotoException
	 */
	private User findCurrentUser(String contextExceptionMessage) throws PhotoException
	{
		Principal caller = context.getCallerPrincipal();
		String userId = caller.getName();
		User user = userRepository.findById(userId);
		if (user == null)
		{
			logger.error("The principal for the caller was not found.");
			throw new PhotoException(contextExceptionMessage);
		}
		return user;
	}

}
