package com.bpodgursky.textgrep.servlet;

import com.bpodgursky.textgrep.ParseHelper;
import com.bpodgursky.textgrep.ParseNode;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ParseServlet extends HttpServlet {

  private final ParseHelper helper;

  public ParseServlet() throws IOException {
    helper = new ParseHelper();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    try {
      String sentence = req.getParameter("sentence");
      ParseNode parse = helper.parse(sentence);

      resp.getWriter().append(parse.toJSON().toString());

    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }
}
