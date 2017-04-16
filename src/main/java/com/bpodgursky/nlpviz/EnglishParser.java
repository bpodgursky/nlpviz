package com.bpodgursky.nlpviz;

import java.util.Properties;

public class EnglishParser extends AbstractParser {

  public EnglishParser(){
    super(getProperties());
  }

  private static Properties getProperties(){
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
    return props;
  }

}
