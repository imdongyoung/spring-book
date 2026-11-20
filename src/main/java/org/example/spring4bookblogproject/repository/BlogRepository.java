package org.example.spring4bookblogproject.repository;

import org.example.spring4bookblogproject.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
