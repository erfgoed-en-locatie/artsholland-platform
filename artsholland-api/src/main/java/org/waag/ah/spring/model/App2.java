//package org.waag.ah.spring.model;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//import org.waag.ah.model.App;
//import org.waag.ah.model.User;
//
//@Entity
//@Table(name="app")
//public class App2 implements App {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "api_key")
//	private String apiKey;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "user_email", nullable = false)
//	private User user;
//
//	@Column
//	private String secret;
//
//	@Column
//	private String name;
//
//	@Column
//	private String title;
//
//	@Column
//	private String url;
//
//	@Column
//	private String description;
//
//	@Override
//	public String getApiKey() {
//		return apiKey;
//	}
//
//	@Override
//	public void setApiKey(String apiKey) {
//		this.apiKey = apiKey;
//	}
//
//	@Override
//	public User getUser() {
//		return user;
//	}
//
//	@Override
//	public void setUser(User user) {
//		this.user = user;
//	}
//
//	@Override
//	public String getSecret() {
//		return secret;
//	}
//
//	@Override
//	public void setSecret(String secret) {
//		this.secret = secret;
//	}
//
//	@Override
//	public String getName() {
//		return name;
//	}
//
//	@Override
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	@Override
//	public String getTitle() {
//		return title;
//	}
//
//	@Override
//	public void setTitle(String title) {
//		this.title = title;
//	}
//
//	@Override
//	public String getURL() {
//		return url;
//	}
//
//	@Override
//	public void setURL(String url) {
//		this.url = url;
//	}
//
//	@Override
//	public String getDescription() {
//		return description;
//	}
//
//	@Override
//	public void setDescription(String description) {
//		this.description = description;
//	}
//
//}
