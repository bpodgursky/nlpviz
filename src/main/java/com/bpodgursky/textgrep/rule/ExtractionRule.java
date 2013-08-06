package com.bpodgursky.textgrep.rule;

import com.bpodgursky.textgrep.ParseHelper;
import com.bpodgursky.textgrep.ParseNode;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public abstract class ExtractionRule implements Serializable {

  private transient ParseNode model;
  private final ParseHelper helper;

  public static final Logger LOG = Logger.getLogger(ExtractionRule.class);

  public ExtractionRule(ParseHelper helper, ParseNode model) {
    this.model = model;
    this.helper = helper;
  }

  //	can override in extractor
  protected String preProcess(String sentence) {
    return sentence;
  }

  public Map<String, String> matches(String sentence) throws IOException {
    Map<String, String> matches = new HashMap<String, String>();

    ParseNode query = helper.parse(preProcess(sentence));
    if (subMatch(query, model, matches)) {
      return matches;
    }

    return null;
  }

  public ParseNode getModel() {
    return model;
  }

  protected abstract boolean typeMatches(String queryType, String modelType);

  private boolean subMatch(ParseNode query, ParseNode model, Map<String, String> matchesMap) {

    LOG.debug("model: " + model);
    LOG.debug("query: " + query);

    if (typeMatches(query.getType(), model.getType())) {

      if (model.isGeneric()) {
        matchesMap.put(model.getContent(), query.getContent());

        LOG.debug("generic match");
        return true;
      }

      if (query.getType().equals("TK") && model.getType().equals("TK")) {
        boolean value = query.toString().equals(model.toString());

        if (value) LOG.debug("token match");
        return value;
      }

      for (ParseNode queryChild : query.getChildren()) {
        if (subMatch(queryChild, model, matchesMap)) {
          return true;
        }
      }

      Iterator<ParseNode> modelChildIter = model.getChildren().iterator();
      Iterator<ParseNode> queryChildIter = query.getChildren().iterator();

      //	if no model children, all 0 match
      if (!modelChildIter.hasNext()) {
        return true;
      }
      //	query can't match if has nothing
      else if (modelChildIter.hasNext() && !queryChildIter.hasNext()) {
        return false;
      } else {
        ParseNode queryChild = queryChildIter.next();
        ParseNode modelChild = modelChildIter.next();

        while (true) {

          if (subMatch(queryChild, modelChild, matchesMap)) {
            if (!modelChildIter.hasNext()) return true;
            if (!queryChildIter.hasNext()) return false;

            modelChild = modelChildIter.next();
            queryChild = queryChildIter.next();
          } else {
            if (queryChildIter.hasNext()) {
              queryChild = queryChildIter.next();
            } else {
              return false;
            }
          }
        }
      }
    }

    return false;
  }


  public String toString() {
    try {

      JSONObject obj = new JSONObject();
      obj.put("model", this.model.toString());

      return obj.toString();

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return "";
  }
}