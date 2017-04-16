package com.bpodgursky.nlpviz;

import java.util.Properties;

public class SpanishParser extends AbstractParser {

  public SpanishParser() {
    super(getProperties());
  }

  private static Properties getProperties() {
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
    props.put("tokenize.language", "es");
    props.put("pos.model", "edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger");
    props.put("ner.model", "edu/stanford/nlp/models/ner/spanish.ancora.distsim.s512.crf.ser.gz");
    props.put("ner.applyNumericClassifiers", "false");
    props.put("ner.applyNumericClassifiers", "false");
    props.put("ner.useSUTime", "false");
    props.put("parse.model", "edu/stanford/nlp/models/lexparser/spanishPCFG.ser.gz");
    return props;
  }

}

