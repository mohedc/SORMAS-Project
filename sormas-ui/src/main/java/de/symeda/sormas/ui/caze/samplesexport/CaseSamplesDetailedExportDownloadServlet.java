/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 */
package de.symeda.sormas.ui.caze.samplesexport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.samplesexport.CaseSamplesDetailedExportFileDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@WebServlet(urlPatterns = {
	"/export-downloads/*" })
@ServletSecurity(@HttpConstraint())
public class CaseSamplesDetailedExportDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String pathInfo = request.getPathInfo();
		if (StringUtils.isBlank(pathInfo) || "/".equals(pathInfo)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		String path = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
		try {
			CaseSamplesDetailedExportFileDto fileDto;
			String userName = principalName(request);
			if (path.startsWith("auth/")) {
				if (request.getUserPrincipal() == null || !request.isUserInRole(UserRight.CASE_EXPORT.name())) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				fileDto = FacadeProvider.getCaseSamplesDetailedExportFacade().getFileForAuth(path.substring("auth/".length()));
			} else {
				fileDto = FacadeProvider.getCaseSamplesDetailedExportFacade().getFileForToken(path);
			}

			Path file = Paths.get(fileDto.getFilePath());
			if (!Files.isRegularFile(file)) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			response.setContentType("text/csv; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileDto.getFileName() + "\"");
			response.setContentLengthLong(Files.size(file));
			try (InputStream in = Files.newInputStream(file); OutputStream out = response.getOutputStream()) {
				byte[] buffer = new byte[8192];
				int read;
				while ((read = in.read(buffer)) >= 0) {
					out.write(buffer, 0, read);
				}
			}
			FacadeProvider.getCaseSamplesDetailedExportFacade().recordDownload(fileDto.getExportUuid(), userName);
		} catch (ValidationRuntimeException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
		}
	}

	private static String principalName(HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		return principal == null ? null : principal.getName();
	}
}
