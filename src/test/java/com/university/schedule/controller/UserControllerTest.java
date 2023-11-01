package com.university.schedule.controller;

import com.university.schedule.config.WebTestConfig;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.UserRegisterDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.UserRegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(WebTestConfig.class)
@ActiveProfiles("test")
public class UserControllerTest {

    public static final String USERNAME = "testUsername";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRegistrationService userRegistrationService;
    @MockBean
    private RoleService roleService;

    @Test
    public void getLoginForm_happyPath() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andExpect(status().is2xxSuccessful())
            .andExpect(view().name("login/userLogin"));
    }

    @Test
    public void getRegistrationForm_happyPath() throws Exception {

        List<RoleDTO> roleDTOList = List.of(new RoleDTO());

        when(roleService.findAllForRegistrationAsDTO()).thenReturn(roleDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/register")).andExpect(status().is2xxSuccessful())
            .andExpect(view().name("login/userRegister")).andExpect(model().attribute("roleDTOList", roleDTOList));

        verify(roleService, times(1)).findAllForRegistrationAsDTO();
    }


    @Test
    public void getRegistrationForm_whenRoleServiceThrowsServiceException_thenProcessError() throws Exception {
        String exceptionMessage = "RoleService exception";
        ServiceException serviceException = new ServiceException(exceptionMessage);

        when(roleService.findAllForRegistrationAsDTO()).thenThrow(serviceException);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/register")).andExpect(status().is2xxSuccessful())
            .andExpect(view().name("error")).andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verify(roleService, times(1)).findAllForRegistrationAsDTO();
    }

    @Test
    public void register_whenValidUser_thenRedirectLogin() throws Exception {
        UserRegisterDTO userRegisterDTO =
            new UserRegisterDTO("user1@example.com", "John", "Doe", "password123", "password123", new RoleDTO());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/register").with(csrf()).flashAttr("userRegisterDTO", userRegisterDTO))
            .andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/user/login"))
            .andExpect(flash().attributeExists("registerSuccess"));

        verify(userRegistrationService, times(1)).register(userRegisterDTO);
    }

    @Test
    public void register_whenInvalidUser_thenProcessForm() throws Exception {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("", "", "", "", "", new RoleDTO());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/register").with(csrf()).flashAttr("userRegisterDTO", userRegisterDTO))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("login/userRegister"))
            .andExpect(model().attributeExists("roleDTOList"));

        verify(userRegistrationService, times(0)).register(any());
    }

    @Test
    public void register_whenPasswordMismatch_thenProcessForm() throws Exception {
        UserRegisterDTO userRegisterDTO =
            new UserRegisterDTO("user1@example.com", "John", "Doe", "password123", "mismatch123", new RoleDTO());

        List<RoleDTO> roleDTOList = List.of(new RoleDTO());

        when(roleService.findAllForRegistrationAsDTO()).thenReturn(roleDTOList);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/register").with(csrf()).flashAttr("userRegisterDTO", userRegisterDTO))
            .andExpect(status().is3xxRedirection());

        verify(userRegistrationService, times(1)).register(any());
        verifyNoInteractions(roleService);
    }

    @Test
    public void register_whenUserRegistrationServiceThrowsValidationException_thenProcessForm() throws Exception {
        UserRegisterDTO userRegisterDTO =
            new UserRegisterDTO("user1@example.com", "John", "Doe", "password123", "password123", new RoleDTO());

        ValidationException validationException = new ValidationException("testException", List.of("myError"));

        doThrow(validationException).when(userRegistrationService).register(userRegisterDTO);
        when(roleService.findAllForRegistrationAsDTO()).thenReturn(new ArrayList<>());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/register").with(csrf()).flashAttr("userRegisterDTO", userRegisterDTO))
            .andExpect(status().is3xxRedirection());

        verify(userRegistrationService, times(1)).register(userRegisterDTO);
    }

    @Test
    public void register_whenRoleServiceThrowsException_thenProcessError() throws Exception {
        UserRegisterDTO userRegisterDTO =
            new UserRegisterDTO("user1@example.com", "", "Doe", "password123", "password123", new RoleDTO());

        String exceptionMessage = "Service Exception";

        when(roleService.findAllForRegistrationAsDTO()).thenThrow(new RuntimeException(exceptionMessage));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/register").with(csrf()).flashAttr("userRegisterDTO", userRegisterDTO))
            .andExpect(status().is2xxSuccessful()).andExpect(view().name("error"))
            .andExpect(model().attribute("exceptionMessage", exceptionMessage));

        verifyNoInteractions(userRegistrationService);
        verify(roleService, times(1)).findAllForRegistrationAsDTO();
    }


}
