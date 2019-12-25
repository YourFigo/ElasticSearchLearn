package cn.figo;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
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

}
