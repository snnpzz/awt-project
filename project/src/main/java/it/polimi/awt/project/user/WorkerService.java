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

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.polimi.awt.project.config.CustomUserDetails;

public class WorkerService {

	private static final String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

	private PasswordEncoder passwordEncoder;
	private WorkerRepository workerRepository;

	@Autowired
	public WorkerService(PasswordEncoder passwordEncoder, WorkerRepository workerRepository) {
		this.passwordEncoder = passwordEncoder;
		this.workerRepository = workerRepository;
	}

	public void insertWorker(String name, UserRole role, String email, String password) throws Exception {
		if ((name.contains("\\")) || (name.contains("/")) || (name.contains(":")) || (name.contains("*"))
				|| (name.contains("?")) || (name.contains("\"")) || (name.contains("<")) || (name.contains(">"))
				|| (name.contains("|"))) {
			// TODO
			throw new Exception();
		}
		if (!(role.equals(UserRole.WORKER))) {
			// TODO
			throw new Exception();
		}
		if ((email.contains("\\")) || (email.contains("/")) || (email.contains(":")) || (email.contains("*"))
				|| (email.contains("?")) || (email.contains("\"")) || (email.contains("<")) || (email.contains(">"))
				|| (email.contains("|"))) {
			// TODO
			throw new Exception();
		}
		if (!(Pattern.matches(regex, email))) {
			// TODO
			throw new Exception();
		}
		Worker worker = new Worker.Builder().withName(name).withEmail(email)
				.withPassword(passwordEncoder.encode(password)).build();
		workerRepository.save(worker);
	}

	public void updateEmail(String email) throws Exception {
		Worker worker = (Worker) ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal()).getUser();
		if ((email.contains("\\")) || (email.contains("/")) || (email.contains(":")) || (email.contains("*"))
				|| (email.contains("?")) || (email.contains("\"")) || (email.contains("<")) || (email.contains(">"))
				|| (email.contains("|"))) {
			// TODO
			throw new Exception();
		}
		// TODO
		if (!(Pattern.matches(regex, email))) {
			// TODO
			throw new Exception();
		}
		worker.setEmail(email);
		workerRepository.save(worker);
	}

	public void updateName(String name) throws Exception {
		Worker worker = (Worker) ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal()).getUser();
		if ((name.contains("\\")) || (name.contains("/")) || (name.contains(":")) || (name.contains("*"))
				|| (name.contains("?")) || (name.contains("\"")) || (name.contains("<")) || (name.contains(">"))
				|| (name.contains("|"))) {
			// TODO
			throw new Exception();
		}
		worker.setName(name);
		workerRepository.save(worker);
	}

	public void updatePassword(String oldPassword, String newPassword, String confirmNewPassword) throws Exception {
		Worker worker = (Worker) ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal()).getUser();
		if (!(passwordEncoder.matches(oldPassword, worker.getPassword()))) {
			// TODO
			throw new Exception();
		}
		if (!(newPassword.equals(confirmNewPassword))) {
			// TODO
			throw new Exception();
		}
		worker.setPassword(passwordEncoder.encode(newPassword));
		workerRepository.save(worker);
	}

}
