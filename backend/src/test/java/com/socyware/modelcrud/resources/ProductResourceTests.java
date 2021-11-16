package com.socyware.modelcrud.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socyware.modelcrud.dto.ProductDTO;
import com.socyware.modelcrud.services.ProductService;
import com.socyware.modelcrud.services.exceptions.DatabaseException;
import com.socyware.modelcrud.services.exceptions.ResourceNotFoundException;
import com.socyware.modelcrud.testes.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private Long existinId;	
	private Long nonExistinId;	
	private Long dependentId;	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO>page;
	
	@BeforeEach
	void setUp() throws Exception {
		
		existinId = 1L;
		nonExistinId = 2L;
		dependentId = 3L;
		
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPaged(any())).thenReturn(page);
		when(service.findById(existinId)).thenReturn(productDTO);
		when(service.findById(nonExistinId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.insert(any())).thenReturn(productDTO);
		
		when(service.update(eq(existinId), any())).thenReturn(productDTO);
		when(service.update(eq(nonExistinId), any())).thenThrow(ResourceNotFoundException.class);
		
		doNothing().when(service).delete(existinId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistinId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
					
	}
	

	@Test	
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception{
		
		ResultActions result =
				mockMvc.perform(delete("/products/{id}", existinId)
						.accept(MediaType.APPLICATION_JSON));
	
		
		result.andExpect(status().isNoContent());
		
	}
	

	@Test	
	public void deliteShouldReturnNoTFoundtWhenDoesNotIdExists() throws Exception{
		
		ResultActions result =
				mockMvc.perform(delete("/products/{id}", nonExistinId)
					   .accept(MediaType.APPLICATION_JSON));
	
		result.andExpect(status().isNotFound());
		
	}
	
	@Test	
	public void insertShouldReturnProductDTOWhenCreated() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result =
				mockMvc.perform(post("/products")
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	
	}
	
	@Test	
	public void udpateShouldReturnProductDTOWhenIdExists() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result =
				mockMvc.perform(put("/products/{id}", existinId)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void udpateShouldReturnProductDTOWhenIdDoesNotExists() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result =
				mockMvc.perform(put("/products/{id}", nonExistinId)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
		
	}
	
	
	@Test
	public void findAllShouldReturnPage() throws Exception{
		ResultActions result =
				mockMvc.perform(get("/products")
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
				
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExist () throws Exception{
		ResultActions result =
				mockMvc.perform(get("/products/{id}", existinId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
				
	}
	
	@Test
	public void findByIdShouldReturnProductWhenDoesNotIdExist () throws Exception {
		ResultActions result =
				mockMvc.perform(get("/products/{id}", nonExistinId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
		
	}
	
}
