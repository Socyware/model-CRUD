package com.socyware.modelcrud.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.socyware.modelcrud.entities.Category;


public interface CategoryRepository extends JpaRepository<Category, Long> {

}
