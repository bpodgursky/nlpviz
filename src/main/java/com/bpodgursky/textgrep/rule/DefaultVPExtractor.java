package com.bpodgursky.textgrep.rule;

import com.bpodgursky.textgrep.ParseHelper;
import com.bpodgursky.textgrep.ParseNode;

//	TODO formalize what it does
public class DefaultVPExtractor extends ExtractionRule {

	public DefaultVPExtractor(ParseNode model, ParseHelper helper) {
		super(helper, model);
	}

	protected boolean typeMatches(String queryType, String modelType){
		
		//	is a sub-type of the other (NNS from NN, etc)
		if(queryType.matches("^("+modelType+")"+".*")){
			return true;
		}
		
		//	match clauses like "bob lived in maryland with his wife" from "in his early years bob lived in maryland with his wife"
		//	against #subject lived in #location, because opennlp sadly parses "bob in his early years lived in maryland with his wife"
		//	diff from "in his early years bob lived in maryland with his wife" TODO retrain
		if(queryType.matches("(VP|S|TOP)") && modelType.matches("(VP|S|TOP)")){
			return true;
		}
		
		return false;
	}
}
