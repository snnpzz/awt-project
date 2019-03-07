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

package it.polimi.awt.project.peak;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import it.polimi.awt.project.annotation.Annotation;
import it.polimi.awt.project.load.Load;
import it.polimi.awt.project.peaklocalizedname.PeakLocalizedName;

@Entity
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@Table(name = "peak", schema = "public")
public class Peak {

	@Id
	private String id;
	private Float elevation;
	@OneToMany(cascade = CascadeType.MERGE, mappedBy = "peak", orphanRemoval = true)
	@JsonBackReference
	private List<Load> campaigns;
	private float latitude;
	@JsonManagedReference
	@OneToMany(cascade = CascadeType.MERGE, mappedBy = "peak", orphanRemoval = true)
	private List<PeakLocalizedName> localizedNames;
	private float longitude;
	private String name;
	private String provenance;
	@JsonManagedReference
	@OneToMany(cascade = CascadeType.MERGE, mappedBy = "peak", orphanRemoval = true)
	private List<Annotation> annotations;

	public Peak() {
	}

	public Peak(Builder builder) {
		this.elevation = builder.elevation;
		this.id = builder.id;
		this.latitude = builder.latitude;
		this.localizedNames = builder.localizedNames;
		this.longitude = builder.longitude;
		this.name = builder.name;
		this.provenance = builder.provenance;
		this.annotations = builder.annotations;
	}

	public Float getElevation() {
		return elevation;
	}

	public void setElevation(Float elevation) {
		this.elevation = elevation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public List<PeakLocalizedName> getLocalizedNames() {
		return localizedNames;
	}

	public void setLocalizedNames(List<PeakLocalizedName> localizedNames) {
		this.localizedNames = localizedNames;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProvenance() {
		return provenance;
	}

	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	public static class Builder {

		private Float elevation;
		private String id;
		private float latitude;
		private List<PeakLocalizedName> localizedNames;
		private float longitude;
		private String name;
		private String provenance;
		private List<Annotation> annotations;

		public Builder() {
		}

		public Builder withElevation(Float elevation) {
			this.elevation = elevation;
			return this;
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withLatitude(float latitude) {
			this.latitude = latitude;
			return this;
		}

		public Builder withLocalizedNames(List<PeakLocalizedName> localizedNames) {
			this.localizedNames = localizedNames;
			return this;
		}

		public Builder withLongitude(float longitude) {
			this.longitude = longitude;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withProvenance(String provenance) {
			this.provenance = provenance;
			return this;
		}

		public Peak build() {
			return new Peak(this);
		}

	}

}
