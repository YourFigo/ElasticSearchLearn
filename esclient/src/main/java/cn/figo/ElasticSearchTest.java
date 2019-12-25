package cn.figo;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;

/**
 * @Author Figo
 * @Date 2019/12/25 22:11
 */
public class ElasticSearchTest {

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
    public void createIndex() throws Exception {
        //1、创建一个Settings对象，相当于是一个配置信息。主要配置集群的名称。
        Settings settings = Settings.builder().put("cluster.name", "my-elasticsearch").build();
        //2、创建一个客户端Client对象
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9301));
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9302));
        //3、使用client对象创建一个索引库
        // get() 才会执行创建的操作
        client.admin().indices().prepareCreate("index_hello").get();
        //4、关闭client对象
        client.close();
    }

    @Test
    public void setMappings() throws Exception{
        Settings settings = Settings.builder().put("cluster.name","my-elasticsearch").build();
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9300));
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9301));
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9302));
        //创建一个Mappings信息
        /*{
            "article":{
            "properties":{
                "id":{
                    "type":"long",
                            "store":true
                },
                "title":{
                    "type":"text",
                            "store":true,
                            "index":true,
                            "analyzer":"ik_smart"
                },
                "content":{
                    "type":"text",
                            "store":true,
                            "index":true,
                            "analyzer":"ik_smart"
                }
            }
        }
        }*/

        // 使用 XContentBuilder 描述json数据
        XContentBuilder builder = XContentFactory.jsonBuilder()
            .startObject()
                .startObject("article")
                    .startObject("properties")
                        .startObject("id")
                            .field("type","long")
                            .field("store", true)
                        .endObject()
                        .startObject("title")
                            .field("type", "text")
                            .field("store", true)
                            .field("analyzer", "ik_smart")
                        .endObject()
                        .startObject("content")
                            .field("type", "text")
                            .field("store", true)
                            .field("analyzer","ik_smart")
                        .endObject()
                    .endObject()
                .endObject()
            .endObject();

        //使用client把mapping信息设置到索引库中
        client.admin().indices()
                //设置要做映射的索引
                .preparePutMapping("index_hello")
                //设置要做映射的type
                .setType("article")
                //mapping信息，可以是XContentBuilder对象可以是json格式的字符串
                .setSource(builder)
                //执行操作
                .get();
        //关闭链接
        client.close();
    }

    @Test
    public void testAddDocument() throws Exception {
        //创建一个client对象
        //创建一个文档对象
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("id", 1l)
                .field("title", "1111北方入秋速度明显加快 多地降温幅度最多可达10度")
                .field("content", "1111阿联酋一架客机在纽约机场被隔离 10名乘客病倒")
                .endObject();
        //把文档对象添加到索引库
        client.prepareIndex()
                //设置索引名称
                .setIndex("index_hello")
                //设置type
                .setType("article")
                //设置文档的id，如果不设置的话自动的生成一个id
                .setId("1")
                //设置文档信息
                .setSource(builder)
                //执行操作
                .get();
        //关闭客户端
        client.close();
    }

}
