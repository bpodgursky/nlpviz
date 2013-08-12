package com.bpodgursky.textgrep.scripts;

import com.google.common.collect.Lists;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class StanfordCoreNLPTest {


  public static void main(String[] args) throws IOException, ClassNotFoundException, JSONException {

    // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    // read some text in the text variable
    String text = "Bob is a truck driver.  He drives a lot every day.";// Add your text here!

    // create an empty Annotation just with the given text
    Annotation document = new Annotation(text);

    // run all Annotators on this text
    pipeline.annotate(document);

    // these are all the sentences in this document
    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

    for (CoreMap sentence : sentences) {
      // traversing the words in the current sentence
      // a CoreLabel is a CoreMap with additional token-specific methods
      for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
        // this is the text of the token
        String word = token.get(CoreAnnotations.TextAnnotation.class);
        // this is the POS tag of the token
        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
        // this is the NER label of the token
        String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

        System.out.println();
        System.out.println(token.docID());
        System.out.println(word);
        System.out.println(pos);
        System.out.println(ne);
      }

      // this is the parse tree of the current sentence
      Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
      List<CoreLabel> coreLabels = sentence.get(CoreAnnotations.TokensAnnotation.class);

      tree.label();

      System.out.println(tree);


      JSONObject json = toJSON(tree, coreLabels.iterator());
      System.out.println(json);

      // this is the Stanford dependency graph of the current sentence
      SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);

      System.out.println(dependencies);

    }

    // This is the coreference link graph
    // Each chain stores a set of mentions that link to each other,
    // along with a method for getting the most representative mention
    // Both sentence and token offsets start at 1!
    Map<Integer, CorefChain> graph =
        document.get(CorefCoreAnnotations.CorefChainAnnotation.class);

    System.out.println(graph);

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

      obj.put("word", word);
      obj.put("pos", pos);
      obj.put("ne", ne);

    }else{
      obj.put("type", tree.label());
    }

    return new JSONObject()
        .put("data", obj)
        .put("children", new JSONArray(children));
  }
}
