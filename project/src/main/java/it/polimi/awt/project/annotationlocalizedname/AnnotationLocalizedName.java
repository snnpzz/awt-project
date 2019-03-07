/**
 * awt-project
 * Copyright (C) 2019  Susanna Pozzoli
 *
 * This file is part of awt-project.
 *
 * awt-project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * awt-project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with awt-project.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.polimi.awt.project.annotationlocalizedname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.polimi.awt.project.annotation.Annotation;

@Entity
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@Table(name = "annotation_localized_name", schema = "public")
public class AnnotationLocalizedName {

	@Column(columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	int id;
	@JoinColumn(name = "annotation_id")
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	Annotation annotation;
	String language;
	String name;

	public AnnotationLocalizedName() {
	}

	public AnnotationLocalizedName(Builder builder) {
		this.id = builder.id;
		this.annotation = builder.annotation;
		this.language = builder.language;
		this.name = builder.name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static class Builder {

		int id;
		Annotation annotation;
		String language;
		String name;

		public Builder() {
		}

		public Builder withId(int id) {
			this.id = id;
			return this;
		}

		public Builder withAnnotation(Annotation annotation) {
			this.annotation = annotation;
			return this;
		}

		public Builder withLanguage(String language) {
			this.language = language;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public AnnotationLocalizedName build() {
			return new AnnotationLocalizedName(this);
		}

	}

}
