package com.university.schedule.handler;

import com.university.schedule.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;


@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {


	//	@ExceptionHandler(RuntimeException.class)
	//	public String handleException(RuntimeException runtimeException, Model model) {
	//		model.addAttribute("exceptionMessage", runtimeException.getMessage());
	//		throw runtimeException;
	//		/*return "error";*/
	//	}

	@ExceptionHandler(ValidationException.class)
	public String handleValidationException(ValidationException validationException,
	                                        RedirectAttributes redirectAttributes, WebRequest request) {
		redirectAttributes.addFlashAttribute("validationServiceErrors", validationException.getViolations());

		log.error("ValidationException occurs, ", validationException);
		String referer = request.getHeader("Referer");
		if (referer != null && !referer.isEmpty()) {
			return "redirect:" + referer;
		} else {
			return "redirect:/welcome?redirect";
		}
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public RedirectView handle404Exception(Authentication authentication) {
		if (authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
			return new RedirectView("/admin/dashboard");
		}
		return new RedirectView("/welcome?redirect");
	}
}
