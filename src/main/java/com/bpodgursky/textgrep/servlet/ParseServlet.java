package com.bpodgursky.textgrep.servlet;

import com.bpodgursky.textgrep.ParseHelper2;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ParseServlet extends HttpServlet {

  private final ParseHelper2 helper;

  public ParseServlet() throws IOException {
    helper = new ParseHelper2();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    try {
      String sentence = req.getParameter("text");

      resp.getWriter().append(helper.parse(sentence).toString());

    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }
}
