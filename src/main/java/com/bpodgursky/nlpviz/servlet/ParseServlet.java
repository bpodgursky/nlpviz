package com.bpodgursky.nlpviz.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.bpodgursky.nlpviz.AbstractParser;
import com.bpodgursky.nlpviz.EnglishParser;
import com.bpodgursky.nlpviz.SpanishParser;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseServlet extends HttpServlet {
  private static final Logger LOG = LoggerFactory.getLogger(ParseServlet.class);

  private final AbstractParser englishParser;
  private final AbstractParser spanishParser;

  public ParseServlet() throws IOException {
    englishParser = new EnglishParser();
    spanishParser = new SpanishParser();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    try {
      String sentence = req.getParameter("text");
      LOG.info("Processing request: "+sentence);

      String lang = req.getParameter("lang");

      if(lang == null || lang.equals("en")) {
        resp.getWriter().append(englishParser.parse(sentence).toString());
      }else{
        resp.getWriter().append(spanishParser.parse(sentence).toString());
      }

    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }
}
