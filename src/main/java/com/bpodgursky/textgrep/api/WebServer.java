package com.bpodgursky.textgrep.api;

import com.bpodgursky.textgrep.servlet.HomeServlet;
import com.bpodgursky.textgrep.servlet.ParseServlet;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.GzipFilter;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.DispatcherType;
import java.net.URL;
import java.util.EnumSet;
import java.util.concurrent.Semaphore;

public class WebServer implements Runnable {
  public static final int DEFAULT_PORT = 43315;
  public static final String PARSER = "/parser";
  public static final String HOME = "/home";


  private final Semaphore shutdownLock = new Semaphore(0);

  public WebServer(){}

  public final void shutdown() {
    shutdownLock.release();
  }

  @Override
  public void run() {
    try {

      Server uiServer = new Server(DEFAULT_PORT);
      final URL warUrl = uiServer.getClass().getClassLoader().getResource("com/bpodgursky/textgrep/www");
      final String warUrlString = warUrl.toExternalForm();

      WebAppContext context = new WebAppContext(warUrlString, "/");
      context.addServlet(new ServletHolder(new ParseServlet()), PARSER);
      context.addServlet(new ServletHolder(new HomeServlet()), HOME);
      context.addFilter(GzipFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

      uiServer.setHandler(context);

      uiServer.start();

      shutdownLock.acquire();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws InterruptedException {
    DOMConfigurator.configure(WebServer.class.getResource("/com/bpodgursky/textgrep/log4j.xml"));

    WebServer server = new WebServer();
    Thread thread1 = new Thread(server);

    thread1.start();
    thread1.join();
  }
}
