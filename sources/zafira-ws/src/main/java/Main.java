import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{
	public static void main(String [] args) throws MalformedURLException
	{

		String jobUrl = "http://demo.qaprosoft.com/jenkins/job/qaprosoft/job/carina-demo/job/Launcher/";
//		jobUrl = jobUrl.replaceAll("/$", "").;
		String jobName = jobUrl.replaceAll("/$", "").substring(jobUrl.lastIndexOf("/") + 1);
//		String folderUrl = jobUrl.substring(0, jobUrl.lastIndexOf("/job/"));
//		String folderName = folderUrl.substring(folderUrl.lastIndexOf("/") + 1);

		System.out.println(jobName);
//		System.out.println(folderUrl);
//		System.out.println(folderName);
	}
}
