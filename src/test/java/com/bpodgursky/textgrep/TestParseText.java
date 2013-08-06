package com.bpodgursky.textgrep;


import com.bpodgursky.textgrep.rule.DefaultVPExtractor;
import com.bpodgursky.textgrep.rule.ExtractionRule;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestParseText {

  private static ParseHelper helper;

  static{
    DOMConfigurator.configure(ParseText.class.getResource("/com/bpodgursky/textgrep/log4j.xml"));
    try {
      helper = new ParseHelper();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
	public void testParseAndMatch() throws IOException{

    ParseHelper helper = new ParseHelper();

		//	build each rule and test sentences against model sentence
		ParseHelper.writeGraph(helper.parse("John Jacob Smith is an outstanding banker."),
        "john_sentence.dot");
	
		ParseHelper.writeGraph(helper.parse("Auren Raphael Hoffman (born 1974 ) is an American entrepreneur, CEO of Rapleaf, editor of Summation.net, angel investor, and member of Council on Foreign Relations."),
        "auren_sentence.dot");
		
		ParseHelper.writeGraph(helper.parse("Frederick Lee Ford (born March 30, 1938 in Bakersfield, California) is a former American football halfback for the Buffalo Bills and Los Angeles Chargers of the American Football League."),
        "fred_sentence.dot");
		
		ExtractionRule simplePastLocation = buildRule("john lived in chicago", getParseMap("john", "tk", "subject", "chicago", "tk", "location"));
		checkExtract(simplePastLocation, "Bob lived in maryland", "#subject", "Bob", "#location", "maryland");
		checkExtract(simplePastLocation, "Bob lived in maryland with his wife", "#subject", "Bob", "#location", "maryland");
		checkExtract(simplePastLocation, "Bob always lived in maryland with his wife", "#subject", "Bob", "#location", "maryland");
		checkExtract(simplePastLocation, "Bob in his early years lived in maryland with his wife", "#subject", "Bob", "#location", "maryland");
		checkExtract(simplePastLocation, "In his early years Bob lived in maryland with his wife.", "#subject", "Bob", "#location", "maryland");
		checkExtract(simplePastLocation, "In his early years, Bob lived in maryland with his wife.", "#subject", "Bob", "#location", "maryland");
		checkExtract(simplePastLocation, "For the rest of his life, Bob lived in maryland with his wife.", "#subject", "Bob", "#location", "maryland");
		checkExtract(simplePastLocation, "For the rest of his life Bob lived in maryland with his wife.", "#subject", "Bob", "#location", "maryland");
		
		ExtractionRule simpleCurrentLocation = buildRule("Ben lives in Chicago.", getParseMap("Ben", "tk", "subject", "Chicago", "tk", "location"));
		checkExtract(simpleCurrentLocation, "Joseph lives in Maryland.", "#subject", "Joseph", "#location", "Maryland");
		checkExtract(simpleCurrentLocation, "Joseph lives in Maryland with his wife.", "#subject", "Joseph", "#location", "Maryland");
		checkExtract(simpleCurrentLocation, "Bob lives in Maryland with his wife because of his age.", "#subject", "Bob", "#location", "Maryland");
		
		ExtractionRule prnCurrentLocation = buildRule("He lives in Chicago.", getParseMap("He", "NP", "subject", "Chicago", "tk", "location"));
		checkExtract(prnCurrentLocation, "Joseph lives in Maryland.", "#subject", "Joseph", "#location", "Maryland");
		checkExtract(prnCurrentLocation, "She currently lives in Maryland.", "#subject", "She", "#location", "Maryland");		
		
		ExtractionRule occupation = buildRule("John Jacob Smith is an outstanding banker.", getParseMap(
				"John", "NNP", "first",
				"Jacob", "NNP", "middle",
				"Smith", "NNP", "last",
				"outstanding", "JJ", "descriptor", 
				"banker","NN", "occupation"));		
		checkExtract(occupation, "Auren Raphael Hoffman (born 1974 ) is an American entrepreneur.", 
				"#first", "Auren",
				"#middle", "Raphael",
				"#last", "Hoffman",
				"#descriptor", "American",
				"#occupation", "entrepreneur");
		checkExtract(occupation, "Auren Raphael Hoffman (born 1974 ) is an American entrepreneur, CEO of Rapleaf, editor of Summation.net, angel investor, and member of Council on Foreign Relations.", 
				"#first", "Auren",
				"#middle", "Raphael",
				"#last", "Hoffman",
				"#descriptor", "American",
				"#occupation", "entrepreneur");
		
		ExtractionRule occupationNN = buildRule("John Jacob Smith is an investment banker.", getParseMap(
				"John", "NNP", "first",
				"Jacob", "NNP", "middle",
				"Smith", "NNP", "last",
				"investment", "NN", "descriptor", 
				"banker","NN", "occupation"));		
		checkExtract(occupationNN, "Frederick Lee Ford (born March 1938 in Bakersfield, California) is an American football halfback for the Buffalo Bills and Los Angeles Chargers of the American Football League.",
				"#first", "Frederick",
				"#middle", "Lee",
				"#last", "Ford",
				"#descriptor", "football",
				"#occupation", "halfback");
		
		ExtractionRule npBornDate = buildRule("Bob (born 1935)", getParseMap(
				"Bob", "NP", "subject",
				"1935", "CD", "birthYear"));
		checkExtract(npBornDate, "Auren Raphael Hoffman (born 1974 )",
				"#subject", "Auren Raphael Hoffman",
				"#birthYear", "1974");
		checkExtract(npBornDate, "Auren Raphael Hoffman (born 1974 ) is an American entrepreneur.",
				"#subject", "Auren Raphael Hoffman",
				"#birthYear", "1974");
		
		ExtractionRule vpBornDate = buildRule("Bob Jacob Smith (born 1935) was a Belgian musician.", getParseMap(
				"Bob", "NNP", "first",
				"Jacob", "NNP", "middle",
				"Smith", "NNP", "last",
				"1935", "CD", "birthYear",
				"was a Belgian musician", "VP", "remainder"));
		checkExtract(vpBornDate, "Auren Raphael Hoffman (born 1974 ) is an American entrepreneur.",
				"#first", "Auren",
				"#middle", "Raphael",
				"#last", "Hoffman",
				"#birthYear", "1974",
				"#remainder", "is an American entrepreneur");
		checkExtract(vpBornDate, "Frederick Lee Ford (born March 1938 in Bakersfield, California) is a former American football halfback for the Buffalo Bills and Los Angeles Chargers of the American Football League.",
				"#first", "Frederick",
				"#middle", "Lee",
				"#last", "Ford",
				"#birthYear", "1938",
				"#remainder", "is a former American football halfback for the Buffalo Bills and Los Angeles Chargers of the American Football League");
	
		ExtractionRule simpleCollege = buildRule("He is a graduate of Harvard University", getParseMap(
				"He", "NP", "subject",
				"Harvard University", "NP", "location"));
		checkExtract(simpleCollege, "From an academic household in California, he is a graduate of San Diego State University (1987), Pennsylvania State University (1989) and received his Ph.D. from Rutgers University in 1992, working with Neil Smith.", 
				"#subject", "he",
				"#location", "San Diego State University");
	}
	
	private void checkExtract(ExtractionRule rule, String sentence, String ... results) throws IOException {
		Map<String, String> expectedMatches = new HashMap<String, String>();
		for(int i = 0; i < results.length-1; i+=2){
			expectedMatches.put(results[i], results[i+1]);
		}
		
		Map<String, String> matches = rule.matches(sentence);
		
		assertEquals(expectedMatches.size(), matches.size());
		
		for(String key: matches.keySet()){
			assertTrue(expectedMatches.containsKey(key));
			assertEquals(expectedMatches.get(key), matches.get(key));
		}
	}
	
	private Map<ParseNode.ParseToken, String> getParseMap(String ... tokens){
		Map<ParseNode.ParseToken, String> map = new HashMap<ParseNode.ParseToken, String>();
		for(int i = 0; i < tokens.length; i+=3){
			map.put(new ParseNode.ParseToken(tokens[i+1].toUpperCase(), tokens[i], false), tokens[i+2]);
		}
		return map;
	}
	
	private ExtractionRule buildRule(String text, Map<ParseNode.ParseToken, String> contentToLabel) throws IOException{
		return new DefaultVPExtractor(ParseHelper.copyWithGenerics(helper.parse(text), contentToLabel), helper);
	}

	//	TODO "" => bob is vbd? (punctuation problems)
	//	TODO "Ben lives in Chicago." parses diff from "John lives in Chicago."
	//	TODO think about allowing more detail in in parsetoken besides "generic, matches everything" and "precise match"  ex, allow regex,
	//		or synonym, etc
	//	TODO prev especially for equivalent stuff like "a vs an"
}
