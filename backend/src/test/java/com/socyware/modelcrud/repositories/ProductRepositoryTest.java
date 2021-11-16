package com.socyware.modelcrud.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.socyware.modelcrud.entities.Product;
import com.socyware.modelcrud.testes.Factory;

@DataJpaTest
public class ProductRepositoryTest {

	@Autowired
	private ProductRepository repository;
	
	private long exintingId;
	private long noExintingId;
	private long countTotalProducts;
		
	@BeforeEach
	void setUp() throws Exception {
		exintingId = 1L;
		noExintingId = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void FindByIdSholudReturnNonEmptyOptionalWhenExists () {
		
				
		Optional<Product> result = repository.findById(exintingId);
		
		Assertions.assertTrue(result.isPresent());	
	}
	
	@Test
	public void FindByIdSholudReturnEmptyOptionalWhenIdDoesNotExist () {
		
				
		Optional<Product> result = repository.findById(noExintingId);
		
		Assertions.assertTrue(result.isEmpty());	
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull () {
		
		Product product =  Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1 , product.getId());
		
	}

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {

		repository.deleteById(exintingId);

		Optional<Product> result = repository.findById(exintingId);

		Assertions.assertFalse(result.isPresent());

	}

	@Test
	public void deleteshouldWhenthrowsEmptyResultDataAccessException() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(noExintingId);
		});

	}
	

}
