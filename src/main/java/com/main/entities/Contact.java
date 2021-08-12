package com.main.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "contact")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(nullable = false)
	private String firstName;
	@Column(nullable = false)
	private String lastName;
	@NotNull
	private String email;
	@NotNull
	private String phoneNumber;
	private String work;
	@Column(nullable = false, length = 64)
	private String image;
	@Column(length = 2000)
	private String description;
	
	@ManyToOne
	@JsonIgnore
	private User user;

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String name) {
		this.firstName = name;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String nickName) {
		this.lastName = nickName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public String getWork() {
		return work;
	}


	public void setWork(String work) {
		this.work = work;
	}


	public String getImage() {
		return image;
	}


	public void setImage(String image) {
		this.image = image;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}

//	@Override
//	public String  toString() {
//		return "Contact{" +
//				"id=" + id +
//				", firstName='" + firstName + '\'' +
//				", lastName='" + lastName + '\'' +
//				", email='" + email + '\'' +
//				", phoneNumber='" + phoneNumber + '\'' +
//				", work='" + work + '\'' +
//				", image='" + image + '\'' +
//				", description='" + description + '\'' +
//				", user=" + user +
//				'}';
//	}
}
