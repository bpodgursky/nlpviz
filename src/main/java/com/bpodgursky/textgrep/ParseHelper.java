package com.bpodgursky.textgrep;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;


public class ParseHelper {
	private Parser parser;
	public static final Logger LOG = Logger.getLogger(ParseHelper.class);
	private static final Scanner prompt = new Scanner(System.in);	

  public ParseHelper() throws IOException {
    InputStream modelIn= ParseHelper.class.getResource("/com/bpodgursky/textgrep/data/en-parser-chunking.bin").openStream();
    ParserModel model = new ParserModel(modelIn);
    parser = ParserFactory.create(model);
  }
	
	public ParseNode parse(String s) throws IOException{
		return convertParse(ParserTool.parseLine(s, parser, 1)[0]);
	}
	
	private static ParseNode convertParse(Parse p){
		
		List<ParseNode> children = new ArrayList<ParseNode>();
		for(Parse child: p.getChildren()){
			children.add(convertParse(child));
		}
		
		return new ParseNode(p.getType(), p.toString().replaceAll("[.,;?!]", ""),false, children);
	}
	
	public static void show(ParseNode p, String buffer){
		for(ParseNode child: p.getChildren()){
			show(child, buffer+" ");
		}
	}
	
	public static ParseNode copyWithGenerics(ParseNode p, Map<ParseNode.ParseToken, String> replacements){
		
		if(replacements.containsKey(p.getToken())){
			return new ParseNode(p.getType(), replacements.get(p.getToken()), true, Collections.<ParseNode>emptyList());
		}else if(p.getType().equals("TK")){
			return new ParseNode(p.getType(), p.getContent(), false, Collections.<ParseNode>emptyList());
		}{
			List<ParseNode> newChildren = new ArrayList<ParseNode>();
			List<String> childNames = new ArrayList<String>();
			for(ParseNode child: p.getChildren()){
				ParseNode copy = copyWithGenerics(child, replacements);
				childNames.add(copy.getContent());
				newChildren.add(copy);
			}
			
			return new ParseNode(p.getType(), StringUtils.join(childNames, " "), false, newChildren);
		}
	}
	
	public static ParseNode copyWithGenerics(ParseNode p){
		if(prompt("Extract value instead of matching token \""+p.getType()+":"+p.toString()+"\"?")){
			return new ParseNode(p.getType(), promptString("Enter extraction label: "), true, 
					Collections.<ParseNode>emptyList());
		}else if(p.getType().equals("TK")){
			return new ParseNode(p.getType(), p.getContent(), false, Collections.<ParseNode>emptyList());
		} else {
			
			List<ParseNode> newChildren = new ArrayList<ParseNode>();
			List<String> childNames = new ArrayList<String>();
			for(ParseNode child: p.getChildren()){
				ParseNode copy = copyWithGenerics(child);
				childNames.add(copy.getContent());
				newChildren.add(copy);
			}
			
			return new ParseNode(p.getType(), StringUtils.join(childNames, " "), false, newChildren);
		}
	}
	
	public static boolean prompt(String query){
		
		String response = "";
		while(!response.equals("y") && !response.equals("n")){
			System.out.println(query);
			response = prompt.nextLine().trim();
		}
		
		return response.equals("y");
	}
	
	public static String promptString(String query){
		System.out.println(query);
		return prompt.nextLine().trim();
	}
	
	public static void writeGraph(ParseNode p, String path) throws IOException{
		FileWriter fw = new FileWriter(path);
		fw.append(toDotFile(p));
		fw.close();
	}
	

  /**
   * Return a string representation which can be turned into
   * a graph by graphviz
   */
  public static String toDotFile(ParseNode n){
    
    Map<ParseNode, Integer> uniques = new HashMap<ParseNode, Integer>();
    Queue<ParseNode> search = new LinkedList<ParseNode>();
    int count = 0;
    StringBuilder str = new StringBuilder("digraph clusters {");
    
    //Start search
    search.add(n);
    
    //Recursively inspect every node
    while(!search.isEmpty()){
      //List<E> items = new LinkedList<E>();
      ParseNode top = search.poll();
      
      //add the node
      str.append("\"").append(count).append("\" [label = \"").append(top.getType()).append((top.getType().equals("TK") || top.isGeneric()) ? (":" + top.getContent()) : "")
        .append("\"];\n");
      
      uniques.put(top, count++);
      search.addAll(top.getChildren());

    }

    search.add(n);
  
    //each node does not have a unique label, so once all the
    //nodes are collected and instantiated, actually generate
    //the edge
    for(Map.Entry<ParseNode, Integer> entry: uniques.entrySet()){
      for(ParseNode child: entry.getKey().getChildren()){
        str.append("\"").append(entry.getValue()).append("\" -> \"").append(uniques.get(child)).append("\";");
      }
    }
    
    str.append("}");
    return str.toString();
  }
}
