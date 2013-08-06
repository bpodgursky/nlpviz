package com.bpodgursky.textgrep;

import com.bpodgursky.textgrep.rule.DefaultVPExtractor;
import com.bpodgursky.textgrep.rule.ExtractionRule;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ParseText {	

	private final static String RULE_FILE = "extraction_rules";	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException{
    DOMConfigurator.configure(ParseText.class.getResource("/com/bpodgursky/textgrep/log4j.xml"));

    ParseHelper helper = new ParseHelper();

		List<ExtractionRule> rules = new ArrayList<ExtractionRule>();
		while(true){
			
			String action = ParseHelper.promptString("\nadd rule [add], test sentence against rules [test], clear rules [clear], load rules [load], save rules [save]");
			System.out.println("\n");
			
			if(action.equals("add")){
				String s = ParseHelper.promptString("Enter model sentence:");
				ParseHelper.writeGraph(helper.parse(s), "model_full_sentence.dot");
				
				ParseNode present = getPresent(helper.parse(s));
				ParseHelper.show(present, "");
				ParseHelper.writeGraph(present, "model_reduced_sentence.dot");

				Map<ParseNode.ParseToken, String> tokenToLabels = new HashMap<ParseNode.ParseToken, String>();
				while(ParseHelper.prompt("Extract another generic from sentence? (y/n)")){
					
					String substring = ParseHelper.promptString("Enter substring to match:");
					String pos = ParseHelper.promptString("Enter part of speech to match:").toUpperCase();
					String label = ParseHelper.promptString("Label to give substring:");
					
					tokenToLabels.put(new ParseNode.ParseToken(pos, substring, false), label);
				}
				
				ExtractionRule rule = new DefaultVPExtractor(ParseHelper.copyWithGenerics(present, tokenToLabels), helper);
				ParseHelper.writeGraph(rule.getModel(), "proposed_model_sentence.dot");
				
				System.out.println("added rule: "+rule);
				rules.add(rule);
			}
			
			else if(action.equals("test")){
				
				String sentence = ParseHelper.promptString("Enter sentence to check against rules:");
				ParseHelper.writeGraph(helper.parse(sentence), "test_sentence.dot");
						
				for(ExtractionRule rule: rules){
					System.out.println(rule.matches(sentence));
				}
			}
			
			else if(action.equals("clear")){
				rules.clear();
				System.out.println("rules: "+rules);
			}
			
			else if(action.equals("load")){
				rules = loadRules();
				System.out.println("rules: "+rules);
			}
			
			else if(action.equals("save")){
				saveRules(rules);
				System.out.println("wrote "+rules.size()+" to file");
			}
		}
	}
	

	private static void saveRules(List<ExtractionRule> allRules) throws IOException{
		FileOutputStream fos = new FileOutputStream(RULE_FILE);
		ObjectOutputStream out = new ObjectOutputStream(fos);

		out.writeObject(allRules);
		out.close();
	}
	
	private static List<ExtractionRule> loadRules() throws IOException, ClassNotFoundException{
		FileInputStream fis = new FileInputStream(RULE_FILE);
		ObjectInputStream oin = new ObjectInputStream(fis);
		
		return (List<ExtractionRule>) oin.readObject();
	}

	
	//	TODO collapse after removing content
	private static ParseNode getPresent(ParseNode p){

		if(p.getType().equals("NP") || p.getType().equals("VP") || p.getType().equals("PP") || p.getType().equals("CC")){
			if(!ParseHelper.prompt("Require phrase: \"" + p.toString() + "\"? y/n")){
				return null;
			}
		}
		
		List<ParseNode> newChildren = new ArrayList<ParseNode>();
		for(ParseNode child: p.getChildren()){
			ParseNode copy = getPresent(child);
			
			if(copy != null){
				newChildren.add(copy);
			}
		}
		
		return new ParseNode(p.getType(), p.getContent(), p.isGeneric(), newChildren);
	}
	

}
