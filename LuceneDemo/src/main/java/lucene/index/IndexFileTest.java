package lucene.index;

import io.FileOperation;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Lu Xugang
 * @date 2019-02-21 09:58
 */
public class IndexFileTest {
  private Directory directory;

  {
    try {
      FileOperation.deleteFile("./data");
      directory = new MMapDirectory(Paths.get("./data"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Analyzer analyzer = new WhitespaceAnalyzer();
  private IndexWriterConfig conf = new IndexWriterConfig(analyzer);
  private IndexWriter indexWriter;

  public void doIndex() throws Exception {
    conf.setUseCompoundFile(false);
    indexWriter = new IndexWriter(directory, conf);

    int count = 0;
//    while (count++ < 440000) {
      // 0
      Document doc = new Document();
      doc.add(new TextField("author", "aab b aab aabbcc ", Field.Store.YES));
      doc.add(new TextField("content", "a", Field.Store.YES));
      indexWriter.addDocument(doc);

      // 1
      doc = new Document();
      doc.add(new TextField("author", "cd aab", Field.Store.YES));
      doc.add(new TextField("content", "b", Field.Store.YES));
      indexWriter.addDocument(doc);

      // 2
      doc = new Document();
      doc.add(new TextField("author", "aab aabb aab", Field.Store.YES));
      doc.add(new TextField("content", "a", Field.Store.YES));
      indexWriter.addDocument(doc);

//    }
    indexWriter.commit();

    DirectoryReader  reader = DirectoryReader.open(indexWriter);
    IndexSearcher searcher = new IndexSearcher(reader);
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
//    builder.add(new TermQuery(new Term("content", "a")), BooleanClause.Occur.MUST);
    builder.add(new TermQuery(new Term("author", "a")), BooleanClause.Occur.MUST);
    Query query = builder.build();
    ScoreDoc[] scoreDoc = searcher.search(query, 100).scoreDocs;
    Document document  = reader.document(2);
    System.out.println(document.get("author"));
    System.out.println(scoreDoc.length);

    // Per-top-reader state:
  }

  public static void main(String[] args) throws Exception{
    IndexFileTest test = new IndexFileTest();
    test.doIndex();
  }
}
