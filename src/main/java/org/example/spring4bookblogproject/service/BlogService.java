package org.example.spring4bookblogproject.service;

import java.util.List;

import org.example.spring4bookblogproject.domain.Article;
import org.example.spring4bookblogproject.dto.AddArticleRequest;
import org.example.spring4bookblogproject.dto.UpdateArticleRequest;
import org.example.spring4bookblogproject.repository.BlogRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BlogService {
	private final BlogRepository blogRepository;

	public Article save(AddArticleRequest request) {
		return blogRepository.save(request.toEntity());
	}

	public List<Article> findAll() {
		return blogRepository.findAll();
	}

	public Article findById(Long id) {
		return blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found: " + id));
	}

	public void delete(Long id) {
		blogRepository.deleteById(id);
	}

	@Transactional
	public Article update(Long id, UpdateArticleRequest request) {
		Article article = blogRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("not found: " + id)
		);

		article.update(request.getTitle(), request.getContent());

		return article;
	}
}
