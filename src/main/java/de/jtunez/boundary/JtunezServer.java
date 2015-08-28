package de.jtunez.boundary;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.servlet.ServletContainer;

public class JtunezServer {

  private static final int PORT = 8000;

  private final Server jettyServer;

  public JtunezServer() {
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
    context.setContextPath("/");
    context.setResourceBase("./src/main/webapp/");

    jettyServer = new Server(PORT);
    jettyServer.setHandler(context);
    jettyServer.addBean(new JtunezErrorHandler());

    context.addServlet(new ServletHolder(new DefaultServlet()), "/*");
    ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/rest/*");
    jerseyServlet.setInitOrder(0);
    jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", RestInterface.class.getCanonicalName());

    FilterHolder filterHolder = new FilterHolder(CrossOriginFilter.class);
    filterHolder.setInitParameter("allowedOrigins", "*");
    filterHolder.setInitParameter("allowedMethods", "GET,POST");
    context.addFilter(filterHolder, "/*", null);
  }

  public void start() {
    try {
      jettyServer.start();
//      jettyServer.join();
    } catch (Exception ex) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "fatal error: ", ex);
      jettyServer.destroy();
    }
  }

  public void stop() {
    try {
      jettyServer.stop();
    } catch (Exception ex) {
      Logger.getLogger(JtunezServer.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      jettyServer.destroy();
    }
  }
}
