package cn.figo;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author Figo
 * @Date 2019/12/25 23:16
 */
public class SearchIndex {

    private TransportClient client;

    @Before
    public void init() throws Exception {
        //创建一个Settings对象
        Settings settings = Settings.builder()
                .put("cluster.name", "my-elasticsearch")
                .build();
        //创建一个TransPortClient对象
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300))
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301))
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9302));
    }

    @Test
    public void testSearchById() throws Exception {
        //创建一个client对象
        //创建一个查询对象
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds("1", "2");
        //执行查询
        search(queryBuilder);
    }

    @Test
    public void testQueryByTerm() throws Exception {
        //创建一个QueryBuilder对象
        //参数1：要搜索的字段
        //参数2：要搜索的关键词
        QueryBuilder queryBuilder = QueryBuilders.termQuery("title", "北方");
        search(queryBuilder);
    }

    @Test
    public void testQueryStringQuery() throws Exception {
        //创建一个QueryBuilder对象
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("速度与激情")
                .defaultField("title");
        search(queryBuilder);
    }

    private void search(QueryBuilder queryBuilder) throws Exception {
        //执行查询
        SearchResponse searchResponse = client.prepareSearch("index_hello")
                .setTypes("article")
                .setQuery(queryBuilder)
                //设置分页信息
                .setFrom(0)
                //每页显示的行数
                .setSize(5)
                .get();
        //取查询结果
        SearchHits searchHits = searchResponse.getHits();
        //取查询结果的总记录数
        System.out.println("查询结果总记录数：" + searchHits.getTotalHits());
        //查询结果列表
        Iterator<SearchHit> iterator = searchHits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            System.out.println(searchHit.getSourceAsString());
            //取文档的属性
            System.out.println("-----------文档的属性");
            Map<String, Object> document = searchHit.getSource();
            System.out.println(document.get("id"));
            System.out.println(document.get("title"));
            System.out.println(document.get("content"));
        }
        //关闭client
        client.close();
    }

}
