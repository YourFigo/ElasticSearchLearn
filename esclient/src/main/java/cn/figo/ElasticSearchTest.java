package cn.figo;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.net.InetAddress;

/**
 * @Author Figo
 * @Date 2019/12/25 22:11
 */
public class ElasticSearchTest {

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



}
