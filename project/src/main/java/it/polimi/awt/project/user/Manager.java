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

package it.polimi.awt.project.user;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.TypeDef;

import it.polimi.awt.project.config.CustomEnumType;

@DiscriminatorValue(value = "MANAGER")
@Entity
@Table(name = "manager", schema = "public")
@TypeDef(name = "postgresql_enum", typeClass = CustomEnumType.class)
public class Manager extends User {

	public Manager() {
	}

	public Manager(Builder builder) {
		this.setName(builder.name);
		this.setEmail(builder.email);
		this.setEnabled(builder.enabled);
		this.setPassword(builder.password);
		this.setRole(builder.role);
	}

	public static class Builder {

		private String email;
		private boolean enabled;
		private String name;
		private String password;
		private UserRole role;

		public Builder() {
			this.enabled = true;
			this.role = UserRole.MANAGER;
		}

		public Builder withEmail(String email) {
			this.email = email;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withPassword(String password) {
			this.password = password;
			return this;
		}

		public Manager build() {
			return new Manager(this);
		}

	}

}
