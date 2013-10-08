import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.naming.AuthenticationException;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import com.ywh.train.Util;
import com.ywh.train.bean.Result;


public class Test {
 public static final String ENCODE = "UTF-8";
 
	public static void main(String[] args) throws Exception {
		Integer a=1;
		Integer b=2;
		Integer c=3;
		Integer d=3;
		Integer e=320;
		Integer f=320;
		Long g=3l;
		System.out.println(c==d);
		System.out.println(e==f);
		System.out.println();
		System.out.println(c==(a+b));
		System.out.println(c.equals(a+b));
		System.out.println(g==(a+b));
		System.out.println(g.equals(a+b));
		System.out.println();
/*		File f = new File("E:\\hc.txt");
		StringBuilder content = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = br.readLine()) != null) {
			//	System.out.println(line);
				content.append(line);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ss = content.toString().replace("\n","");
		Result rs = new Result();
		Util.parserTokenAndLeftTicket(ss,rs);
		
		System.out.println(rs);
		//Parser parser = Parser.createParser(ss, ENCODE);
	 	Parser parser = new Parser( ss );
		
//访问所有内容
 		TextExtractingVisitor visitor = new TextExtractingVisitor();
        parser.visitAllNodesWith(visitor);
        String textInPage = visitor.getExtractedText();
        System.out.println(textInPage.contains("目前您还有未处理的订单"));
        
        // Parser parser = new Parser( (HttpURLConnection) (new URL("http://127.0.0.1:8080/HTMLParserTester.html")).openConnection() );
		//System.out.println(ss);
		
//
// 解析特定元素
    	NodeFilter inputFilter = new TagNameFilter("input");
    	NodeFilter nameFilter = new HasAttributeFilter("name","org.apache.struts.taglib.html.TOKEN");
		NodeFilter nameLeftTicketFilter = new HasAttributeFilter("name","leftTicketStr");
		NodeFilter orFilter = new OrFilter(nameFilter,nameLeftTicketFilter);
		NodeFilter tokenFilter = new AndFilter(inputFilter,orFilter);
	//	NodeFilter leftTicketFilter = new AndFilter(inputFilter,nameLeftTicketFilter);
		
		//NodeList tokenList = parser.extractAllNodesThatMatch(tokenFilter);
		NodeList leftTiketList = parser.extractAllNodesThatMatch(tokenFilter);
		
        if(leftTiketList!=null) {
        	System.out.println(leftTiketList.size());
            for (int i = 0; i < leftTiketList.size(); i++) {
                Node textnode = (Node) leftTiketList.elementAt(i);
                
                System.out.println("getText:"+textnode.toHtml());
                System.out.println("=================================================");
            }
        }*///
		
	}
}
