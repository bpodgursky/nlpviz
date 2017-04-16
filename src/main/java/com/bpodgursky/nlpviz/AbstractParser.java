package com.bpodgursky.nlpviz;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.google.common.collect.Lists;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractParser {

  private final StanfordCoreNLP pipeline;

  public AbstractParser(Properties properties) {
    pipeline = new StanfordCoreNLP(properties);
  }

  public JSONArray parse(String text) throws JSONException {
    Annotation document = new Annotation(text);
    pipeline.annotate(document);

    JSONArray array = new JSONArray();

    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

    for (CoreMap sentence : sentences) {
      Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
      List<CoreLabel> coreLabels = sentence.get(CoreAnnotations.TokensAnnotation.class);

      array.put(toJSON(tree, coreLabels.iterator()));
    }

    return array;
  }

  public static JSONObject toJSON(Tree tree, Iterator<CoreLabel> labels) throws JSONException {

    List<JSONObject> children = Lists.newArrayList();
    for (Tree child : tree.getChildrenAsList()) {
      children.add(toJSON(child, labels));
    }

    JSONObject obj = new JSONObject();

    if(tree.isLeaf()){
      CoreLabel next = labels.next();

      String word = next.get(CoreAnnotations.TextAnnotation.class);
      String pos = next.get(CoreAnnotations.PartOfSpeechAnnotation.class);
      String ne = next.get(CoreAnnotations.NamedEntityTagAnnotation.class);

      System.out.println(pos);

      obj.put("word", word);
      obj.put("pos", pos);
      obj.put("ne", ne);
      obj.put("type", "TK");

    }else{

//      System.out.println(tree.label());

      obj.put("type", tree.label());
    }

    return new JSONObject()
        .put("data", obj)
        .put("children", new JSONArray(children));
  }
}
