package com.bpodgursky.nlpviz.servlet;

import com.bpodgursky.nlpviz.ParseHelper2;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ParseServlet extends HttpServlet {
  private static final Logger LOG = LoggerFactory.getLogger(ParseServlet.class);

  private final ParseHelper2 helper;

  public ParseServlet() throws IOException {
    helper = new ParseHelper2();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    try {
      String sentence = req.getParameter("text");
      LOG.info("Processing request: "+sentence);

      resp.getWriter().append(helper.parse(sentence).toString());

    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }
}
