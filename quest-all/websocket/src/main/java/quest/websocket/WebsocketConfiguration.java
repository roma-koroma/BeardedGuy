package quest.websocket;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;


/**
 * @author Roman K.
 */


@Configuration
@Profile("dev")
@PropertySource(name = "test", value = "classpath:profiles/dev/websocket.properties")
public class WebsocketConfiguration
{
	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(WebsocketConfiguration.class);

	@Bean
	public Server server(@Value(value = "${jetty.port}") int port)
	{
		Server server = new Server(port);

		return server;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}
}


