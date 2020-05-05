//package com.shoppingcart.model;
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//import lombok.Data;
//
//@Data
//@Table(name = "smallsubcategory")
//@Entity
//public class SmallSubCategory {
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
//	@JoinColumn(name = "subcategoryId")
//	private SubCategory subCategory;
//}
