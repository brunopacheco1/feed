package arquivoviseurobot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Main {
	public static void main(String[] args) throws Exception {
		WebDriver driver = new FirefoxDriver();

		driver.get("http://digitarq.advis.arquivos.pt/viewer?id=1200621");

		while (true) {
			Long time = System.currentTimeMillis();
			
			WebElement downloadButton = driver.findElement(By.id("ViewerControl1_HyperLinkDownload"));
			
			CloseableHttpClient httpclient = HttpClients.createDefault();

			String link = downloadButton.getAttribute("href"); 
			
			WebElement nextButton = null;
			try {
				nextButton = driver.findElement(By.id("ViewerControl1_ImageButtonThumbnailNext"));
			} catch (NoSuchElementException e) {
				nextButton = null;
			}

			if (nextButton == null) {
				break;
			}

			nextButton.click();
			
			HttpGet httpget = new HttpGet(link);

			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			
			String fileName = response.getFirstHeader("Content-Disposition").getValue().split("filename=")[1].trim();

			File file = new File("./arquivos/" + fileName);
			
			InputStream is = entity.getContent();
			
			FileOutputStream fos = new FileOutputStream(file);
			
			IOUtils.copy(is, fos);

			is.close();
			fos.close();

			Thread.sleep(5000);
			
			time = System.currentTimeMillis() - time;
			
			System.out.println(String.format("%s persisted! %s ms", fileName, time));
		}

		driver.quit();
	}
}
