package ch.sailcom.server;

import java.io.File;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.jboss.weld.environment.se.StartMain;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.servlet.Listener;

/**
 * Run the main method and that's it. For how Weld recommends starting
 * applications outside a JEE container, see {@link StartMain}.
 *
 * @see StartMain
 */
public class App {

	public static String[] PARAMETERS;

	public static String[] getParameters() {
		final String[] copy = new String[App.PARAMETERS.length];
		System.arraycopy(App.PARAMETERS, 0, copy, 0, App.PARAMETERS.length);
		return copy;
	}

	/**
	 * The main method called from the command line.
	 *
	 * @param args
	 *          the command line arguments
	 */
	public static void main(final String[] args) {
		try {
			new App(args).startWeld();
		} catch (ServletException | LifecycleException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Can be useful for some cloud deployments where the port is set as an
	 * environment variable.
	 */
	static void setListeningPort(final Tomcat tomcat) {
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "8889";
		}
		tomcat.setPort(Integer.valueOf(webPort));
	}

	public App(final String[] commandLineArgs) throws ServletException, LifecycleException {

		App.PARAMETERS = commandLineArgs;

		final Tomcat tomcat = new Tomcat();
		App.setListeningPort(tomcat);

		final StandardContext ctx = (StandardContext) tomcat.addWebapp("/", new File("src/main/webapp/").getAbsolutePath());

		// Declare an alternative location for your "WEB-INF/classes" dir
		// Servlet 3.0 annotation will work
		final WebResourceRoot resources = new StandardRoot(ctx);
		resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", new File("target/classes").getAbsolutePath(), "/"));
		ctx.setResources(resources);

		ctx.addApplicationListener(Listener.class.getName());

		// Enable JNDI InitialContext
		tomcat.enableNaming();

		// Start server
		tomcat.start();
		tomcat.getServer().await();
		/*
		 * @Inject FleetService staticDataService;
		 * 
		 * public App() { }
		 * 
		 * @PostConstruct void init() { LOGGER.info("Fetch static data");
		 * staticDataService.getShips(); }
		 */
	}

	public WeldContainer startWeld() {
		final Weld weld = new Weld();
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(weld));
		final WeldContainer container = weld.initialize(); // Fires events
		return container;
	}

	static class ShutdownHook extends Thread {
		private final Weld weld;

		ShutdownHook(final Weld weld) {
			this.weld = weld;
		}

		@Override
		public void run() {
			this.weld.shutdown();
		}
	}

}
