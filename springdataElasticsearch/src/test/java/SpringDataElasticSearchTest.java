import cn.figo.es.entity.Article;
import cn.figo.es.repositories.ArticleRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * @Author Figo
 * @Date 2019/12/26 22:34
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringDataElasticSearchTest {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ElasticsearchTemplate template;

    /**
     * 创建索引和映射信息
     * @throws Exception
     */
    @Test
    public void createIndex() throws Exception {
        //创建索引，并配置映射关系
        template.createIndex(Article.class);
        //配置映射关系
        //template.putMapping(Article.class);
    }

    /**
     * 保存文档
     */
    @Test
    public void saveArticle(){
        for (int i = 20; i < 30; i++) {

            Article article = new Article();
            article.setId(i);
            article.setTitle("检索服务器 title " + i);
            article.setContent("检索服务器 content " + i);
            articleRepository.save(article);
        }
    }

    @Test
    public void deleteDocumentById() throws Exception{
        articleRepository.deleteById(100l);
    }

    @Test
    public void findAll() throws Exception{
        Iterable<Article> articles = articleRepository.findAll();
        articles.forEach(article -> System.out.println(article));
    }

    @Test
    public void findById() throws Exception{
        Optional<Article> optional = articleRepository.findById(1l);
        Article article = optional.get();
        System.out.println(article);
    }

    @Test
    public void findByTitle() throws Exception{
        List<Article> articles = articleRepository.findByTitle("title 3");
        articles.stream().forEach(article -> System.out.println(article));
    }

    @Test
    public void findByTitleOrContent() throws Exception{
        // 默认分页10条数据
        List<Article> articles = articleRepository.findByTitleOrContent("title", "content 5");
        articles.stream().forEach(article -> System.out.println(article));
    }

    @Test
    public void findByTitleOrContentPageable() throws Exception{
        // 设置分页信息从第0页开始
        Pageable pageable = PageRequest.of(0,15);
        // 自带的查询也是先分词再查询，但多个分词之间是and关系，也就是说多个分词需要出现在一句话中，
        // 某一个分词出现在这句话中时，查询结果中并没有这句话，这并不是 QueryString
        List<Article> articles = articleRepository.findByTitleOrContent("数据库服务器", "content 5",pageable);
        articles.stream().forEach(article -> System.out.println(article));
    }

    // 使用Elasticsearch的原生查询对象进行查询
    @Test
    public void findByNativeQuery() {
        //创建一个SearchQuery对象
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                //设置查询条件，此处可以使用QueryBuilders创建多种查询
                .withQuery(QueryBuilders.queryStringQuery("数据库服务器").defaultField("title"))
                //还可以设置分页信息
                .withPageable(PageRequest.of(1, 5))
                //创建SearchQuery对象
                .build();

        //使用模板对象执行查询
        List<Article> articleList = template.queryForList(query, Article.class);
        articleList.forEach(article -> System.out.println(article));
    }

}
