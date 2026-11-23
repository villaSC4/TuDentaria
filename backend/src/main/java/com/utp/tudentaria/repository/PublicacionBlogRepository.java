package com.utp.tudentaria.repository;

import com.utp.tudentaria.model.PublicacionBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionBlogRepository extends JpaRepository<PublicacionBlog, Integer> {
}
