package info.galleria.view.album;

import info.galleria.view.PageUtilities;
import info.galleria.view.photo.*;
import info.galleria.view.user.HomePage;

import java.net.URI;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

public class ViewAlbumPage
{

	private WebDriver driver;
	private URI contextPath;

	public ViewAlbumPage(WebDriver driver, URI contextPath)
	{
		this.driver = driver;
		this.contextPath = contextPath;
		Wait<WebDriver> wait = new WebDriverWait(driver, 15);
		wait.until(PageUtilities.visibilityOfElementLocated(By.id("wrapper")));
		if (!driver.getTitle().equals("View an album"))
		{
			throw new IllegalStateException("This is not the View Album page.");
		}
	}

	public HomePage goToHomePage()
	{
		driver.findElement(By.id("viewAlbums")).click();
		return new HomePage(driver, contextPath);
	}

	public EditAlbumPage goToEditAlbumPage()
	{
		driver.findElement(By.id("editAlbumLink")).click();
		return new EditAlbumPage(driver, contextPath);
	}

	public HomePage deleteAlbum()
	{
		driver.findElement(By.id("deleteAlbumForm:deleteAlbumButton")).click();
		return new HomePage(driver, contextPath);
	}

	public UploadPhotoPage goToUploadPhoto()
	{
		driver.findElement(By.id("Upload")).click();
		return new UploadPhotoPage(driver, contextPath);
	}

	public ViewPhotoPage viewPhotoWith(String photoTitle, String photoDescription)
	{
		List<WebElement> photoWrappers = driver.findElements(By.xpath("//ul[@class='photoWrapper']"));
		for (WebElement element : photoWrappers)
		{
			WebElement name = element.findElement(By.className("name"));
			WebElement description = element.findElement(By.className("description"));
			if (name.getText().equals(photoTitle) && description.getText().equals(photoDescription))
			{
				element.findElement(By.tagName("a")).click();
				return new ViewPhotoPage(driver, contextPath);
			}
		}
		return null;
	}

	public String[] fetchSuccessMessages()
	{
		List<String> messages = new ArrayList<String>();
		for (WebElement element : driver.findElements(By.xpath("//ul[@id='messages']/li[@class='infoStyle']")))
		{
			messages.add(element.getText());
		}
		return messages.toArray(new String[0]);
	}

}
