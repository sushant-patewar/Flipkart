//package com.shoppingcart.model;
//
//import java.util.List;
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;
//
//import org.hibernate.annotations.ManyToAny;
//
//import lombok.Data;
//
//@Data
//@Table(name = "subcategory")
//@Entity
//public class SubCategory {
//
//	private static final long serialVersionUID = 1L;
//	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Integer id;
//	
//	private String name;
//	
//	@ManyToOne
//	@JoinColumn(name = "categoryId")
//	private Category category;
//	
//	@OneToMany(mappedBy = "subcategory")
//	private List<SmallSubCategory> smallSubCategories;
//	
//	@OneToMany(mappedBy = "subcategory")
//	private List<Brand> brands;
//	
//}
