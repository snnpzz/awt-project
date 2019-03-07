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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

	@Autowired
	protected AuthenticationManager authenticationManager;

	private ManagerService managerService;
	private WorkerService workerService;

	@Autowired
	public UserController(ManagerService managerService, WorkerService workerService) {
		this.managerService = managerService;
		this.workerService = workerService;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/users")
	public String create(HttpServletRequest request, HttpServletResponse response, @RequestParam String name, @RequestParam UserRole role, @RequestParam String email,
			@RequestParam String password) throws Exception {
		if (role.equals(UserRole.MANAGER)) {
			managerService.insertManager(name, role, email, password);
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(name, password);
			token.setDetails(new WebAuthenticationDetails(request));
			Authentication authentication = authenticationManager.authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return "redirect:/manager/";
		}
		if (role.equals(UserRole.WORKER)) {
			workerService.insertWorker(name, role, email, password);
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(name, password);
			token.setDetails(new WebAuthenticationDetails(request));
			Authentication authentication = authenticationManager.authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return "redirect:/worker/";
		}
		return "redirect:/";
	}

}
