package com.bpodgursky.textgrep;

import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ParseNode {
	
	private final ParseToken token;
	private final List<ParseNode> children = new ArrayList<ParseNode>();

	public ParseNode(String type, String content, boolean isGeneric, List<ParseNode> children){
		token = new ParseToken(type, content, isGeneric);
		this.children.addAll(children);
	}
	
	public String getType(){
		return token.type;
	}
	
	public String getContent(){
		return token.content;
	}
	
	public boolean isGeneric(){
		return token.isGeneric;
	}
	
	public ParseToken getToken(){
		return token;
	}
	
	public String toString(){
		return token.toString();
	}

	public List<ParseNode> getChildren(){
		return children;
	}

  public JSONObject toJSON() throws JSONException {

    List<JSONObject> children = Lists.newArrayList();
    for (ParseNode child : this.children) {
      children.add(child.toJSON());
    }

    return new JSONObject()
        .put("data", token.toJSON())
        .put("children", new JSONArray(children));
  }
	
	public static class ParseToken{

		public final String type;
		public final String content; 	//	generic title if generic
		public final boolean isGeneric;
		
		public ParseToken(String type, String content, boolean isGeneric){
			this.type = type;
			this.isGeneric = isGeneric;

			if(isGeneric){
				this.content = "#"+content;
			}else{
				this.content = content;
			}
		}
		
		public String toString(){
			return "{type="+type+","+"data="+content+"}";
		}

    public JSONObject toJSON() throws JSONException {
      return new JSONObject()
          .put("type", type)
          .put("content", content);
    }
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((content == null) ? 0 : content.hashCode());
			result = prime * result + (isGeneric ? 1231 : 1237);
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ParseToken other = (ParseToken) obj;
			if (content == null) {
				if (other.content != null)
					return false;
			} else if (!content.equals(other.content))
				return false;
			if (isGeneric != other.isGeneric)
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
	}
}