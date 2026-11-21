package org.example.spring4bookblogproject.service;

import java.util.List;

import org.example.spring4bookblogproject.domain.Article;
import org.example.spring4bookblogproject.dto.AddArticleRequest;
import org.example.spring4bookblogproject.dto.UpdateArticleRequest;
import org.example.spring4bookblogproject.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BlogService {
	private final BlogRepository blogRepository;

	public Article save(AddArticleRequest request, String userName) {
		return blogRepository.save(request.toEntity(userName));
	}

	public List<Article> findAll() {
		return blogRepository.findAll();
	}

	public Article findById(Long id) {
		return blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found: " + id));
	}

	public void delete(Long id) {
		Article article = blogRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("not found : " + id));

		authorizeArticleAuthor(article);
		blogRepository.deleteById(id);
	}

	@Transactional
	public Article update(Long id, UpdateArticleRequest request) {
		Article article = blogRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("not found: " + id)
		);

		authorizeArticleAuthor(article);
		article.update(request.getTitle(), request.getContent());

		return article;
	}

	// 게시글을 작성한 유저 확인
	private static void authorizeArticleAuthor(Article article) {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!article.getAuthor().equals(userName)) {
			throw new IllegalArgumentException("not authorized");
		}
	}
}
