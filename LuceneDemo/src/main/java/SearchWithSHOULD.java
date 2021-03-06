import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Lu Xugang
 * @date 2018-12-04 20:49
 */
public class SearchWithSHOULD {
        private Directory directory;

    {
        try {
            directory = new MMapDirectory(Paths.get("./data"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Analyzer analyzer = new WhitespaceAnalyzer();
    private IndexWriterConfig conf = new IndexWriterConfig(analyzer);
    private IndexWriter writer;

    private void doSearch() throws Exception{
        writer = new IndexWriter(directory, conf);

        String[] docs = {
                "b d a b b",
                "b b b a",
                "b",
                "a",
//                "b b",                // 3
//                "c b b",
//                "c e a b",
//                "d",
//                "c d e c",
//                "a c a c", // 8
//                "a b",
//                "a c e",
//                "c a"
        };

        for (String s: docs
        ) {
            Document document = new Document();
            document.add(new TextField("content", s, Field.Store.YES));
            writer.addDocument(document);
        }
        writer.commit();
        IndexReader reader = DirectoryReader.open(writer);
        IndexSearcher searcher = new IndexSearcher(reader);
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        query.add(new TermQuery(new Term("content", "a")), BooleanClause.Occur.SHOULD);
        query.add(new TermQuery(new Term("content", "c")), BooleanClause.Occur.SHOULD);
        query.add(new TermQuery(new Term("content", "e")), BooleanClause.Occur.SHOULD);
        query.add(new TermQuery(new Term("content", "b")), BooleanClause.Occur.MUST_NOT);
//        query.add(new TermQuery(new Term("content", "f")), BooleanClause.Occur.SHOULD);
//        query.add(new TermQuery(new Term("content", "g")), BooleanClause.Occur.SHOULD);
//        query.add(new TermQuery(new Term("content", "c")), BooleanClause.Occur.MUST);
//        query.add(new TermQuery(new Term("content", "e")), BooleanClause.Occur.SHOULD);
//        query.add(new TermQuery(new Term("content", "b")), BooleanClause.Occur.SHOULD);
//        query.add(new TermQuery(new Term("content", "a")), BooleanClause.Occur.MUST);
//        query.add(new TermQuery(new Term("content", "c")), BooleanClause.Occur.SHOULD);
//        query.add(new TermQuery(new Term("content", "a")), BooleanClause.Occur.FILTER);
        query.setMinimumNumberShouldMatch(2);

        ScoreDoc[] hits;
        hits = searcher.search(query.build(), 1000).scoreDocs;

        System.out.println("hit size: "+ hits.length +"");
        for (int i = 0 ; i < hits.length && i < 10; i++) {
            Document d = searcher.doc(hits[i].doc);
            System.out.println(i + " " + hits[i].score + " " + d.get("content"));
        }
        reader.close();
        directory.close();
    }

    public static void main(String[] args) throws Exception{
        SearchWithSHOULD simpleSearch = new SearchWithSHOULD();
        simpleSearch.doSearch();
    }
}
