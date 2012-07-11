/*
 * Copyright 2009-2012 by KNURT Systeme (http://www.knurt.de)
 *
 * Licensed under the Creative Commons License Attribution-NonCommercial-ShareAlike 3.0 Unported;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.knurt.fam.template.model;

import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;

import de.knurt.fam.core.content.html.factory.FamSubmitButtonFactory;
import de.knurt.fam.core.control.persistence.dao.config.RoleConfigDao;
import de.knurt.fam.core.control.persistence.dao.couchdb.CouchDBDao4Soa;
import de.knurt.fam.core.model.persist.User;
import de.knurt.fam.core.util.UserFactory;
import de.knurt.fam.core.util.VelocityContextFactory;
import de.knurt.fam.core.util.mvc.RequestInterpreter;
import de.knurt.fam.core.util.termsofuse.TermsOfUsePage;
import de.knurt.fam.template.util.TermsOfUseResolver;
import de.knurt.heinzelmann.ui.html.HtmlElement;
import de.knurt.heinzelmann.util.query.QueryString;
import de.knurt.heinzelmann.util.velocity.VelocityStringRenderUtil;

/**
 * produce the model for terms of use pages
 * 
 * @see TemplateContentProperties#getTemplateModel(TemplateResource)
 * @author Daniel Oltmanns
 * @since 1.3.0 (10/19/2010)
 */
public class TermsOfUseModelFactory {

	// TODO #5 enhance effectivity

	private TermsOfUseResolver termsOfUseResolver = null;

	private Properties getPropertiesDefault(TemplateResource templateResource, User userToShow) {
		Properties result = new Properties();
		// ↘ this is what to set
		String goToAfterPost = this.getGoToAfterPost(templateResource);
		TermsOfUsePage pageToShow = null;
		if (this.canHandleRequest(userToShow, templateResource, goToAfterPost)) {
			int pagenoToShow = this.getPageNumberToRequest(templateResource.getRequest());
			if (this.isAdminTermsOfUseSinglePage(templateResource) && termsOfUseResolver.showBasePageFirst()) {
				pagenoToShow++;
			}
			pageToShow = termsOfUseResolver.getPageForUser(pagenoToShow);
		}
		if (pageToShow != null) {
			// ↖ have a page to show (it is not a redirect)
			// ↓ prepare model and view
			result.put("page", pageToShow);
			result.put("pagenumber", pageToShow.getPageno() + 1);
			result.put("pagecount", this.termsOfUseResolver.getPageCount());
			result.put("page_content", this.getPageContent(pageToShow, userToShow));
			result.put("isAdminTermsOfUseSinglePage", this.isAdminTermsOfUseSinglePage(templateResource));
			result.put("acceptButton", this.getAcceptButton(pageToShow, userToShow, templateResource));
			result.put("method", this.getFormMethod(pageToShow));
			result.put("hiddenInputsForNextPage", this.getFormHiddenInputs(pageToShow, userToShow, goToAfterPost));
			String isReview = "f";
			if (userToShow.isAcceptedStatementOfAgreement()) {
				isReview = "t";
			}
			result.put("jsonvar", "var Review = '" + isReview + "';");
		}
		return result;
	}

	private boolean isAdminTermsOfUseSinglePage(TemplateResource templateResource) {
		return templateResource.getAuthUser().isAdmin() && templateResource.getName().equals("termsofuse") && templateResource.getFilename().equals("default");
	}

	public Properties getProperties(TemplateResource templateResource) {
		Properties result = new Properties();
		User userToShow = this.getUserToShowTermsFor(templateResource);
		termsOfUseResolver = new TermsOfUseResolver(userToShow);
		if (this.isRequestForSingleTerms(templateResource)) {
			result = this.getPropertiesForSingle(templateResource);
		} else {
			result = this.getPropertiesDefault(templateResource, userToShow);
		}
		return result;
	}

	private Properties getPropertiesForSingle(TemplateResource templateResource) {
		Properties result = new Properties();
		String requestedTitle = templateResource.getRequest().getParameter("title").toLowerCase();
		User userToShow = templateResource.getAuthUser();
		TermsOfUsePage pageToShow = null;
		List<TermsOfUsePage> candidates = CouchDBDao4Soa.getInstance().getActiveTermsOfUsePages(userToShow);
		for (TermsOfUsePage candidate : candidates) {
			if (candidate.getTitle() != null && candidate.getRoleId() != null && candidate.getTitle().toLowerCase().equals(requestedTitle) && candidate.getRoleId().equals(userToShow.getRoleId())) {
				pageToShow = candidate;
			}
		}
		if (pageToShow == null) {
			pageToShow = new TermsOfUseResolver(userToShow).getPageForUser(0);
		}
		if (pageToShow != null) {
			result.put("page", pageToShow);
			result.put("pagenumber", pageToShow.getPageno() + 1);
			result.put("pagecount", this.termsOfUseResolver.getPageCount());
			result.put("page_content", this.getPageContent(pageToShow, userToShow));
			result.put("acceptButton", this.getAcceptButton(pageToShow, userToShow, templateResource));
			result.put("method", this.getFormMethod(pageToShow));
			result.put("hiddenInputsForNextPage", this.getFormHiddenInputs(pageToShow, userToShow, this.getGoToAfterPost(templateResource)));
			String isReview = "f";
			if (userToShow.isAcceptedStatementOfAgreement()) {
				isReview = "t";
			}
			result.put("jsonvar", "var Review = '" + isReview + "';");
		}
		return result;
	}

	private boolean isRequestForSingleTerms(TemplateResource templateResource) {
		return templateResource.getAuthUser() != null && templateResource.getRequest().getParameter("title") != null;
	}

	private String getPageContent(TermsOfUsePage pageToShow, User userToShow) {
		VelocityContext userContext = VelocityContextFactory.me().getUser(userToShow);
		return VelocityStringRenderUtil.getInstance().getRendered(pageToShow.getHtmlContent(), userContext);
	}

	private String getGoToAfterPost(TemplateResource templateResource) {
		String result = RequestInterpreter.getToAsString(templateResource.getRequest());
		return result == null ? "corehome" : result;
	}

	private boolean isOnlyAReview(User user, TemplateResource templateResource) {
		boolean isAdmin = templateResource.getAuthUser() != null && templateResource.getAuthUser().isAdmin();
		return user.isAcceptedStatementOfAgreement() == true || isAdmin;
	}

	private Object getFormHiddenInputs(TermsOfUsePage toup, User user, String goToAfterPost) {
		QueryString qsForNextPage = new TermsOfUseResolver(user).getQueryString(toup.getPageno() + 1, goToAfterPost);
		return qsForNextPage.getAsHtmlInputsTypeHidden();
	}

	private boolean canHandleRequest(User userToShowTermsFor, TemplateResource templateResource, String goToAfterPost) {
		boolean result = false;
		if (userToShowTermsFor != null && goToAfterPost != null) {
			if (new TermsOfUseResolver(userToShowTermsFor).isRightUser(RequestInterpreter.getSecret(templateResource.getRequest()))) {
				result = true;
			} else if (templateResource.getAuthUser() != null) {
				result = true;
			}
		}
		return result;
	}

	private User getUserToShowTermsFor(TemplateResource templateResource) {
		// ↖ user to show terms for must not be user auth
		User user = templateResource.getAuthUser();
		if (user == null) {
			// ↖ coming from registration page
			user = RequestInterpreter.getUser(templateResource.getRequest());
		}
		if (user != null && user.isAdmin() && this.isAdminTermsOfUseSinglePage(templateResource)) {
			// ↖ an admin can view whatever he want
			String role = RequestInterpreter.getRole(templateResource.getRequest());
			if (role == null || !RoleConfigDao.getInstance().keyExists(role) || RoleConfigDao.getInstance().isAdmin(role)) {
				// ↖ requested role is invalid, because is null, is admin role
				// or does not exist
				// ↘ set standard role
				role = RoleConfigDao.getInstance().getStandardId();
			}
			User example = UserFactory.getInstance().getJoeBloggs();
			example.setRoleId(role);
			user = example;
		}
		return user;
	}

	private String getFormMethod(TermsOfUsePage actual) {
		return termsOfUseResolver.isLastPage(actual.getPageno()) ? "POST" : "GET";
	}

	private HtmlElement getAcceptButton(TermsOfUsePage actual, User user, TemplateResource templateResource) {
		String message = "";
		if (this.isOnlyAReview(user, templateResource)) {
			if (termsOfUseResolver.isLastPage(actual.getPageno())) {
				message += "Go home"; // INTLANG
			} else {
				message += "View next"; // INTLANG
			}
		} else {
			message = "Accept these Terms of Use Agreements"; // INTLANG
			if (!termsOfUseResolver.isLastPage(actual.getPageno())) {
				message += " and go to next page"; // INTLANG
			}
		}
		HtmlElement result = FamSubmitButtonFactory.getNextButton(message);
		if (actual.isForcePrinting()) {
			result.id("print_button");
		}
		return result;
	}

	private int getPageNumberToRequest(HttpServletRequest rq) {
		int pageno = 0;
		if (RequestInterpreter.hasPageNo(rq)) {
			pageno = RequestInterpreter.getPageNo(rq);
		}
		return pageno;
	}

}
