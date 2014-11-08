package indexing;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Index {
	LoadXml xml;	
	public IndexWriter w;
	StandardAnalyzer analyzer;
	Directory index;
	public IndexSearcher searcher;
	public Index(){
		xml = new LoadXml();
		
		analyzer = new StandardAnalyzer(Version.LUCENE_4_10_2);

		index = new RAMDirectory(); // index is saving into ram memmory

	    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);

	    try {
			w = new IndexWriter(index, config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	public void indexLoadedXml(){
		 NodeList nodeList = xml.doc.getElementsByTagName("Artcl");
	    for (int i = 0; i < nodeList.getLength(); i++) {  // createing index form loaded xml document
	        Node currentNode = nodeList.item(i);
	        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	        	NodeList currNodeList = currentNode.getChildNodes();
	        	add(currNodeList);	//adding 1 article to index
	        }
	    }
	}
	
	public ScoreDoc[] search(String s){

		    Query q;
		    ScoreDoc[] hits = null;
			try {
				q = new QueryParser(Version.LUCENE_4_10_2, "Lbl", analyzer).parse(s);
			
			    IndexReader reader = DirectoryReader.open(index);
			    searcher = new IndexSearcher(reader);
			    TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
			    
			    searcher.search(q, collector);
			    hits  = collector.topDocs().scoreDocs;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return hits;

	}
	
	private void add(NodeList currNodeList){
		Document doc = new Document();
		for (int j = 0; j < currNodeList.getLength(); j++) { // foreach artcl
    		Node currentLNode = currNodeList.item(j);
    		 if (currentLNode.getNodeType() == Node.ELEMENT_NODE){ // if node is element and dont have child elements save its conent to index- for example Abstrct
    			 doc.add(new TextField(currentLNode.getNodeName(), currentLNode.getTextContent(), Field.Store.YES));
    			 
    		 }
		
			 NodeList innerNode = currentLNode.getChildNodes(); //if node has children save all of them - for example Rfrncs
			 int k = 0;
			 for(int i = 1; i< innerNode.getLength(); i++){
				 Node innerNodec = innerNode.item(i);
				 if (innerNodec.getNodeType() == Node.ELEMENT_NODE){
					 doc.add(new TextField(innerNodec.getNodeName() + k, innerNodec.getTextContent(), Field.Store.YES));
					 k++;
				 }
    		 }
    	}
	   

	    try {
			w.addDocument(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
