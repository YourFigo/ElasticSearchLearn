package cn.figo.es.repositories;

import cn.figo.es.entity.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author Figo
 * @Date 2019/12/26 22:33
 */
public interface ArticleRepository extends ElasticsearchRepository<Article, Long> {
}
