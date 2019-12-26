package cn.figo.es.repositories;

import cn.figo.es.entity.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author Figo
 * @Date 2019/12/26 22:33
 */
@Repository("articleRepository")
public interface ArticleRepository extends ElasticsearchRepository<Article, Long> {

    // 根据springDataES的命名规则编写，则不需要写具体实现
    List<Article> findByTitle(String title);
    List<Article> findByTitleOrContent(String title, String content);
    List<Article> findByTitleOrContent(String title, String content, Pageable pageable);
}
