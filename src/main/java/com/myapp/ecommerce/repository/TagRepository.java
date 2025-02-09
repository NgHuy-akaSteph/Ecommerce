package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, String>, JpaSpecificationExecutor<Tag> {
    Tag findByName(String tagName);
}
