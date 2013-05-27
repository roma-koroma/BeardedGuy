package quest.websocket;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Roman K.
 */
public class WebsocketLauncher
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(WebsocketLauncher.class);

	public static void main(String[] args)
	{
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.getEnvironment().setActiveProfiles("dev");
		context.scan("quest.websocket");
		context.refresh();

		Server server = context.getBean("server", Server.class);
	}
}
