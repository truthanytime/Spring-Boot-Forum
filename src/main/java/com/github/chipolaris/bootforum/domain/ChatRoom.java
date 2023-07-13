package com.github.chipolaris.bootforum.domain;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name="CHAT_ROOM_T")
@TableGenerator(name="ChatRoomIdGenerator", table="ENTITY_ID_T", pkColumnName="GEN_KEY", 
	pkColumnValue="CHAT_ROOM_ID", valueColumnName="GEN_VALUE", initialValue = 1000, allocationSize=10)
public class ChatRoom extends BaseEntity {
	
	@PrePersist
	public void prePersist() {
		Date now = Calendar.getInstance().getTime();
		this.setCreateDate(now);
	}
	
	@PreUpdate
	public void preUpdate() {
		
		Date now = Calendar.getInstance().getTime();
		this.setUpdateDate(now);
	}
	
	// persisted attributes
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE, generator="ChatRoomIdGenerator")
	private Long id;
	
	@Column(name = "LABEL", length = 100, unique = true)
	private String label;
	
	@Column(name="ICON", length=30)
	private String icon;
	
	@Column(name="COLOR", length=30)
	private String color;
	
	@Column(name="DISABLED")
	private boolean disabled;
	
	@Column(name="SORT_ORDER")
	private Integer sortOrder;

	@Override
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public Integer getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
}

